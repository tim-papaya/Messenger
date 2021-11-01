package com.db.edu.client;

import com.db.edu.connection.Connector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Proxy {
    private final Connector connector;

    public Proxy(Connector connector) {
        this.connector = connector;
    }

    public void send(Object message) throws IOException {
        int count = 0;
        int maxTries = 10;
        ObjectOutputStream output = connector.getOutput();

        while (true) {
            try {
                output.writeObject(message);
                output.flush();
                return;
            } catch (NullPointerException | IOException e) {
                connector.connect();
                if (++count == maxTries) throw e;
            }
        }
    }

    public Object receive() throws IOException {
        int count = 0;
        int maxTries = 10;
        ObjectInputStream input = connector.getInput();

        while (true) {
            try {
                return input.readObject();
            } catch (NullPointerException | IOException | ClassNotFoundException e) {
                connector.connect();
                if (++count == maxTries) try {
                    throw e;
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            }
        }
    }
}
