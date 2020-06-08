package app.responses;

import akka.actor.NotInfluenceReceiveTimeout;

import java.io.Serializable;

public class InternalNumberOfQuestions implements Serializable, NotInfluenceReceiveTimeout {

    public int number;

    public InternalNumberOfQuestions(int number) {
        this.number = number;
    }
}