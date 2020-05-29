package app;

import akka.actor.AbstractLoggingActor;

import java.util.Random;

public class PriceHandler extends AbstractLoggingActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s-> {

                    Random random = new Random();
                    int checkTime = Math.abs(random.nextInt())%400 +100;
                    Thread.sleep(checkTime);
                    int price = Math.abs(random.nextInt())%10 +1;
                    InternalPriceResponse response = new InternalPriceResponse(price);
                    getSender().tell(response, getSelf());
                    context().stop(self());

        }).build();
    }
}
