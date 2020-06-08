package app.responses;

import akka.actor.NotInfluenceReceiveTimeout;

import java.io.Serializable;

public class InternalPriceResponse implements Serializable, NotInfluenceReceiveTimeout {


    public Integer price;

    public InternalPriceResponse(Integer price) {
        this.price = price;
    }
}