package app;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import app.db.Database;
import scala.concurrent.duration.Duration;


import java.util.concurrent.TimeUnit;

public class PriceManager extends AbstractLoggingActor {

   Integer value1;
   Integer value2;
   ActorRef client;
   private final Database database;
   Integer numberOfQuestions;


    public PriceManager(Database database) {
        this.database = database;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    context().setReceiveTimeout(Duration.apply(300, TimeUnit.MILLISECONDS));
                    client = getSender();


                    context().actorOf(Props.create(PriceHandler.class)).tell(s, getSelf());
                    context().actorOf(Props.create(PriceHandler.class)).tell(s, getSelf());
                    context().actorOf(Props.create(DatabaseReader.class, database)).tell(s, getSelf());
                    context().actorOf(Props.create(DatabaseWriter.class, database)).tell(s, getSelf());


                })
                .match(InternalNumberOfQuestions.class, response -> {
                    System.out.println("inside internal number");
                    numberOfQuestions = response.number;

                })
                .match(InternalPriceResponse.class, response -> {

                    if(value1 == null)
                    {
                        value1 = response.price;
                    }
                    else {
                        value2 = response.price;
                        Integer result = value1 >value2 ? value2 : value1;
                        PriceResponse r = new PriceResponse(result, numberOfQuestions);
                        client.tell(r, getSelf());
                        context().setReceiveTimeout(Duration.Undefined());
                        context().stop(self());
                    }


                }).match(ReceiveTimeout.class, r -> {
                    // To turn it off
                    context().setReceiveTimeout(Duration.Undefined());
                    System.out.println("Timeout received");
                    Integer result = null;
                    if(numberOfQuestions==null) {
                        System.out.println("Reading was too long");
                    }

                    if(value1 == null && value2== null) {
                        PriceResponse response = new PriceResponse(null, numberOfQuestions);
                        client.tell(response, getSelf());
                        System.out.println(client);
                    }
                    else {
                        if(value1 == null) {
                            result = value2;
                        }
                        else{
                            result = value1;
                        }
                        PriceResponse response = new PriceResponse(result, numberOfQuestions);
                        client.tell(response, getSelf());
                    }

                    context().stop(self());

                }).build();
    }
}
