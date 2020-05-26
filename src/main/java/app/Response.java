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


    public PriceResponse(Integer price) {

        this.price = price;
    }
    public PriceResponse() {

    }
}
class PriceNotAvailableResponse implements Serializable {

    public PriceNotAvailableResponse() {

    }
}
class InternalNumberOfQuestions implements Serializable, NotInfluenceReceiveTimeout {
    int number;

    public InternalNumberOfQuestions(int number) {
        this.number = number;
    }
}
class NumberOfQuestionsResponse implements Serializable {
    int number;

    public NumberOfQuestionsResponse(int number) {
        this.number = number;
    }
}
