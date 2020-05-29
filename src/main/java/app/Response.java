package app;

import akka.actor.NotInfluenceReceiveTimeout;

import java.io.Serializable;

class InternalPriceResponse implements Serializable, NotInfluenceReceiveTimeout {


    Integer price;

    public InternalPriceResponse(Integer price) {
        this.price = price;
    }
}

class PriceResponse implements Serializable {
    Integer price;
    Integer numberOfQuestions;


    public PriceResponse(Integer price, Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
        this.price = price;
    }

    public PriceResponse() {

    }
}


class InternalNumberOfQuestions implements Serializable, NotInfluenceReceiveTimeout {
    int number;

    public InternalNumberOfQuestions(int number) {
        this.number = number;
    }
}


