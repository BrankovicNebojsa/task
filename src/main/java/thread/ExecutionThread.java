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
            executeNewPacket();
        } else {
            executePreviousPacket();
        }
    }

    private void executePreviousPacket() {
        Instant timeAfterDelay = dummyPacket.getTimeReceived().plusSeconds(dummyPacket.getDelay()[0]);
        if (timeAfterDelay.isAfter(Instant.now())) {
            waitOutTheDelay(timeAfterDelay);
            sendDummyPacketToServer();
        } else {
            sendCancelPacketToServer();
        }
        removePacketFromJsonFile();
    }


    private void executeNewPacket() {
        addNewPacketToJsonFile();
        waitOutTheDelay(dummyPacket.getTimeReceived().plusSeconds(dummyPacket.getDelay()[0]));
        sendDummyPacketToServer();
        removePacketFromJsonFile();
    }

    private void waitOutTheDelay(Instant timeAfterDelay) {
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

    private void addNewPacketToJsonFile() {
        System.out.println(this.getName() + " has started adding packet to json.");
        jsonPersistenceSingleton.addToJsonFile(dummyPacket);
        System.out.println(this.getName() + " has finished adding packet to json.");
    }

    private void removePacketFromJsonFile() {
        System.out.println(this.getName() + " has started deleting the packet from json.");
        jsonPersistenceSingleton.removeFromJsonFile(dummyPacket.getId());
        System.out.println(this.getName() + " has finished deleting the packet from json.");
    }

    private void sendCancelPacketToServer() {
        try {
            System.out.println(this.getName() + " is able to send the cancel packet back to server.");
            CancelPacket cancelPacket = new CancelPacket(dummyPacket.getId());
            synchronized (out) {
                System.out.println(this.getName() + " is sending the cancel packet back to server.");
                out.write(cancelPacket.getPacketType());
                out.write(cancelPacket.getLength());
                out.write(cancelPacket.getId());
                out.flush();
            }
            System.out.println(this.getName() + " has finished sending the cancel packet back to server.");
        } catch (IOException ex) {
            System.out.println("Error in " + this.getName() + " while trying to write to OutputStream. " + ex.getMessage());
        }
    }

    private void sendDummyPacketToServer() {
        try {
            System.out.println(this.getName() + " is able to send the dummy packet back to server.");
            synchronized (out) {
                System.out.println(this.getName() + " is sending the dummy packet back to server.");
                out.write(dummyPacket.getPacketType());
                out.write(dummyPacket.getLength());
                out.write(dummyPacket.getId());
                out.write(dummyPacket.getDelay());
                out.flush();
            }
            System.out.println(this.getName() + " has finished sending the dummy packet back to server.");
        } catch (IOException ex) {
            System.out.println("Error in " + this.getName() + " while trying to write to OutputStream. " + ex.getMessage());
        }
    }

}
