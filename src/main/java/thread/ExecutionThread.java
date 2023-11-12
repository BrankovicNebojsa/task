package thread;

import json.JsonPersistenceSingleton;
import packet.CancelPacket;
import packet.DummyPacket;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;

public class ExecutionThread extends Thread {

    private final OutputStream out;
    private final DummyPacket dummyPacket;
    private final boolean shouldAddToJson;
    private final JsonPersistenceSingleton jsonPersistenceSingleton;

    public ExecutionThread(OutputStream out, DummyPacket dummyPacket, boolean shouldAddToJson) {
        this.out = out;
        this.dummyPacket = dummyPacket;
        this.shouldAddToJson = shouldAddToJson;
        this.jsonPersistenceSingleton = JsonPersistenceSingleton.getInstance();
    }

    @Override
    public void run() {
        if (shouldAddToJson) {
            System.out.println(this.getName() + " is saving data to json.");
            jsonPersistenceSingleton.addToJsonFile(dummyPacket);
            System.out.println(this.getName() + " saved data to json.");
        }

        Instant timeAfterDelay = dummyPacket.getTimeReceived().plusSeconds(dummyPacket.getDelay()[0]);
        if (timeAfterDelay.isAfter(Instant.now())) {
            try {
                long sleepingDuration = Duration.between(timeAfterDelay, Instant.now()).abs().toMillis();
                System.out.println(this.getName() + " is sleeping for " + sleepingDuration + " seconds.");
                sleep(sleepingDuration);
                System.out.println(this.getName() + " has finished sleeping for " + sleepingDuration + " seconds.");
            } catch (InterruptedException e) {
                System.out.println("Error in " + this.getName() + " while sleeping. ");
                e.printStackTrace();
            }
        }

        try {
            System.out.println(this.getName() + " is able to send the packet back to server.");
            CancelPacket cancelPacket = new CancelPacket(dummyPacket.getId());
            synchronized (out) {
                System.out.println(this.getName() + " is sending the packet back to server.");
                out.write(cancelPacket.getPacketType());
                out.write(cancelPacket.getLength());
                out.write(cancelPacket.getId());
                out.flush();
            }
            System.out.println(this.getName() + " has finished sending the packet back to server.");
            System.out.println(this.getName() + " has started deleting the packet from json file.");
            jsonPersistenceSingleton.removeFromJsonFile(dummyPacket.getId());
            System.out.println(this.getName() + " has finished deleting the packet from json file.");
        } catch (IOException ex) {
            System.out.println("Error in " + this.getName() + " while trying to write to OutputStream. " + ex.getMessage());
        }
    }
}
