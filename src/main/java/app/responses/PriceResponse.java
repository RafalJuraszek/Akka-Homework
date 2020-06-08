package app.responses;

import java.io.Serializable;

public class PriceResponse implements Serializable {
    public Integer price;
    public Integer numberOfQuestions;


    public PriceResponse(Integer price, Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
        this.price = price;
    }

    public PriceResponse() {

    }
}