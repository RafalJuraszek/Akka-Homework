package app;

import akka.actor.AbstractLoggingActor;
import app.db.Database;

public class DatabaseWriter extends AbstractLoggingActor {

    private Database database;

    public DatabaseWriter(Database database) {
        this.database = database;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    database.insertOrReplace(s, 1);
                }).build();
    }

}
