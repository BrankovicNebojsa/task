package thread;

import json.JsonPersistenceSingleton;
import packet.DummyPacket;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;

public class WaitingThread extends Thread {
    private OutputStream out;
    private DummyPacket dummyPacket;
    private JsonPersistenceSingleton jsonPersistenceSingleton;

    public WaitingThread(OutputStream out, DummyPacket dummyPacket) {
        this.out = out;
        this.dummyPacket = dummyPacket;
        this.jsonPersistenceSingleton = JsonPersistenceSingleton.getInstance();
    }

    @Override
    public void run() {
        waitOutDelay();
        sendDummyPacket();
        removePacketFromJsonFile(dummyPacket);
    }


    private void waitOutDelay() {
        long sleepingDuration = Duration.between(dummyPacket.getTimeAfterDelay(), Instant.now()).abs().toMillis();
        try {
            System.out.println("Packet with an id: " + dummyPacket.getId()[0] + " started sleeping for: " + sleepingDuration + " seconds.");
            sleep(sleepingDuration);
            System.out.println("Packet with an id: " + dummyPacket.getId()[0] + " finished sleeping for: " + sleepingDuration + " seconds.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendDummyPacket() {
        try {
            System.out.println("Dummy packet with an id: " + dummyPacket.getId()[0] + " is being sent back to server.");
            synchronized (out) {
                out.write(dummyPacket.getPacketType());
                out.write(dummyPacket.getLength());
                out.write(dummyPacket.getId());
                out.write(dummyPacket.getDelay());
                out.flush();
            }
            System.out.println("Dummy packet with an id: " + dummyPacket.getId()[0] + " was sent back to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removePacketFromJsonFile(DummyPacket dummyPacket) {
        System.out.println("Packet with an id: " + dummyPacket.getId()[0] + " is being deleted from json.");
        jsonPersistenceSingleton.removeFromJsonFile(dummyPacket.getId());
        System.out.println("Packet with an id: " + dummyPacket.getId()[0] + " was deleted from json.");
    }
}
