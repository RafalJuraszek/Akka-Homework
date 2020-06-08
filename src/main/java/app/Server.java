package app;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import app.db.Database;
import app.actors.PriceManager;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.Duration;

import static akka.actor.SupervisorStrategy.resume;

public class Server extends AbstractLoggingActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final Database database = new Database();

    public static void main(String[] args) throws Exception {

        File configFile = new File("server.conf");
        Config config = ConfigFactory.parseFile(configFile);


        final ActorSystem system = ActorSystem.create("server_system", config);
        final ActorRef local = system.actorOf(Props.create(Server.class), "server");


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                local.tell("close", null);
                break;
            }
            local.tell(line, null);
        }

        system.terminate();
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {


                    if (s.equals("close")) {
                        database.close();
                    } else {
                        context().actorOf(Props.create(PriceManager.class, database)).tell(s, getSender());
                    }


                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy()
    {
        return new OneForOneStrategy(
                10,
                Duration.ofSeconds(30),
                DeciderBuilder.matchAny(any -> resume())
                        .build()
        );
    }

}
