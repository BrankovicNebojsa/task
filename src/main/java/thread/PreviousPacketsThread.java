package thread;

import json.JsonPersistence;
import packet.CancelPacket;
import packet.DummyPacket;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

public class PreviousPacketsThread extends Thread {

    private final OutputStream out;
    private final DummyPacket dummyPacket;
    private final JsonPersistence jsonPersistence;

    public PreviousPacketsThread(OutputStream out, DummyPacket dummyPacket) {
        this.out = out;
        this.dummyPacket = dummyPacket;
        this.jsonPersistence = JsonPersistence.getInstance();
    }

    @Override
    public void run() {
        boolean status = true;
        while (status) {
            if (dummyPacket.getTimeReceived().plusSeconds(dummyPacket.getDelay()[0]).isBefore(Instant.now())) {
                System.out.println("Thread is able to send the packet back to server.");
                try {
                    CancelPacket cancelPacket = new CancelPacket(this.dummyPacket.getId());
                    synchronized (out) {
                        System.out.println("Thread is sending the packet back to server.");
                        out.write(cancelPacket.getPacketType());
                        out.write(cancelPacket.getLength());
                        out.write(cancelPacket.getId());
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
                    sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
