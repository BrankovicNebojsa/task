package thread;

import json.JsonPersistence;
import packet.DummyPacket;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

public class PreviousPacketsThread extends Thread {

    private final OutputStream out;
    private final DummyPacket dummyPacket;
    private final JsonPersistence jsonPersistence;

    public PreviousPacketsThread(OutputStream out, DummyPacket dummyPacket, JsonPersistence jsonPersistence) {
        this.out = out;
        this.dummyPacket = dummyPacket;
        this.jsonPersistence = jsonPersistence;
    }

    @Override
    public void run() {
        boolean status = true;
        while (status) {
            if (dummyPacket.getTimeReceived().plusSeconds(dummyPacket.getDelay()[0]).isBefore(Instant.now())) {
                System.out.println("Thread is able to send the packet back to server.");
                try {
                    synchronized (out) {
                        System.out.println("Thread is sending the packet back to server.");
                        out.write(this.dummyPacket.getPacketType());
                        out.write(this.dummyPacket.getLength());
                        out.write(this.dummyPacket.getId());
                        out.write(this.dummyPacket.getDelay());
                        out.flush();
                    }
                } catch (IOException ex) {
                    System.out.println("Error while trying to write to OutputStream. " + ex.getMessage());
                }
                System.out.println("Thread has finished sending the packet back to server.");
                System.out.println("Thread has started deleting the packet from json file.");
                jsonPersistence.removeFromJsonFile(this.dummyPacket.getId());
                System.out.println("Thread has finished deleting the packet from json file.");
                status = false;
            } else {
                try {
                    System.out.println("Thread has started sleeping.");
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
