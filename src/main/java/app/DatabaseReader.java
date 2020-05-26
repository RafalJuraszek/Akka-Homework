package app;

import akka.actor.AbstractLoggingActor;
import app.db.Database;

public class DatabaseReader extends AbstractLoggingActor {

    private Database database;

    public DatabaseReader(Database database) {
        this.database = database;
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    getSender().tell(new InternalNumberOfQuestions(database.findNumberByName(s)), getSelf());
                }).build();
    }
}
