package app;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.PatternsCS;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import static akka.http.javadsl.server.PathMatchers.segment;




import java.io.File;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class HttpServer extends AllDirectives {

    private final String OPINEO_URL = "https://www.opineo.pl/?szukaj=%s&s=2";
    private final ActorRef httpActor;
    private final ActorSystem httpSystem;
    private final ActorMaterializer actorMaterializer;


    public HttpServer(ActorRef httpActor, ActorSystem system, ActorMaterializer actorMaterializer) {
        this.httpActor = httpActor;
        this.httpSystem = system;
        this.actorMaterializer = actorMaterializer;
    }

    public static void main(String[] args) throws Exception {

        File configFile = new File("server.conf");
        Config config = ConfigFactory.parseFile(configFile);
        ActorSystem system = ActorSystem.create("server_system", config);


        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);


        ActorRef httpActor = system.actorOf(Props.create(Server.class), "server");
        HttpServer app = new HttpServer(httpActor, system, materializer);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.routes().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("localhost", 8080), materializer);

        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }

    public Route routes() {
        return path(segment("price").slash(segment(Pattern.compile(".*"))), this::getPrice)
                .orElse(path(segment("review").slash(segment(Pattern.compile(".*"))), this::getReview));
    }

    private Route getPrice(String item) {

            return get( () -> {CompletionStage<PriceResponse> price =
                    PatternsCS.ask(httpActor, item, Duration.ofSeconds(1))
                            .thenApply(obj -> (PriceResponse) obj);



            return onSuccess(price, (resp) -> {

                String numberCommmunicate = resp.numberOfQuestions == null ? " No info for number of questions" : " Number of questions: " +resp.numberOfQuestions;
                String priceCommunicate = resp.price == null ? "Price not available for this product" : "Price: "+resp.price;
                System.out.println(priceCommunicate +numberCommmunicate);
                return complete(priceCommunicate + numberCommmunicate);

            });
            }

            );
    }

    private Route getReview(String item) {
        String url = String.format(OPINEO_URL, item);
        return get(() -> {
            CompletionStage<String> resp = Http.get(httpSystem).singleRequest(HttpRequest.create(url)).thenCompose(response ->
                response.entity().toStrict(10000, actorMaterializer)).thenApply(entity -> {

            return Jsoup.parseBodyFragment(entity.getData().utf8String());

        }).thenApply(body -> {
                Elements elements = body.getElementsByClass("pl_attr");

                for(Element el : elements) {

                    if(el.getElementsByTag("span").hasClass("pros")) {
                        Elements list = el.getElementsByTag("li");
                        StringBuilder s = new StringBuilder();
                        for(Element e : list) {
                            s.append(e.text());
                            s.append("\n");
                        }
                        return s.toString();
                    }

                }
            return "No positive reviews founded";
        });
        return onSuccess(resp, r -> {
            return complete(r);
        });
        });
    }

}