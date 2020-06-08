package app;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import app.responses.PriceResponse;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Client extends AbstractLoggingActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final String SERVER_PATH = "akka://server_system@127.0.0.1:2555/user/server";

    public static void main(String[] args) throws Exception {

        File configFile = new File("client.conf");
        System.out.println(configFile.getAbsolutePath());
        Config config = ConfigFactory.parseFile(configFile);


        final ActorSystem system = ActorSystem.create("client", config);
        final ActorRef local = system.actorOf(Props.create(Client.class), "client");


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
            local.tell(line, null);
        }

        system.terminate();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {


                    getContext().actorSelection(SERVER_PATH).tell(s, getSelf());

                })
                .match(PriceResponse.class, response -> {
                    String numberCommmunicate = response.numberOfQuestions == null ? " No info for number of questions" : " Number of questions: " +response.numberOfQuestions;
                    String priceCommunicate = response.price == null ? "Price not available for this product" : "Price: "+response.price;
                    System.out.println(priceCommunicate +numberCommmunicate);
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();

    }


}
