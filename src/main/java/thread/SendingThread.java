package thread;

import json.JsonPersistenceSingleton;
import packet.CancelPacket;
import packet.DummyPacket;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayDeque;

public class SendingThread extends Thread {
    private final OutputStream out;
    private final boolean isFromPreviousSession;
    private final JsonPersistenceSingleton jsonPersistenceSingleton;
    private ArrayDeque<DummyPacket> packetQueue;

    public SendingThread(OutputStream out, boolean isFromPreviousSession) {
        this.out = out;
        this.isFromPreviousSession = isFromPreviousSession;
        this.jsonPersistenceSingleton = JsonPersistenceSingleton.getInstance();
        this.packetQueue = new ArrayDeque<>();
    }

    @Override
    public void run() {
        if (isFromPreviousSession) {
            sendPacketsFromPreviousSession();
        } else {
            sendPacketsFromThisSession();
        }
    }

    private void sendPacketsFromPreviousSession() {
        while (!packetQueue.isEmpty()) {
            execute();
        }
    }

    private void sendPacketsFromThisSession() {
        while (true) {
            if (!packetQueue.isEmpty()) {
                execute();
            } else {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void execute() {
        DummyPacket dummyPacket = packetQueue.getFirst();
        if (isAbleToSend(dummyPacket)) {
            send(dummyPacket, isFromPreviousSession);
            removePacketFromJsonFile(dummyPacket);
            packetQueue.removeFirst();
        } else {
            packetQueue.removeFirst();
            addPacketToQueue(dummyPacket);
        }
    }

    private boolean isAbleToSend(DummyPacket dummyPacket) {
        return dummyPacket.getTimeAfterDelay().isBefore(Instant.now());
    }

    private void send(DummyPacket dummyPacket, boolean isFromPreviousSession) {
        if (isFromPreviousSession) {
            sendCancelPacket(new CancelPacket(dummyPacket.getId()));
        } else {
            sendDummyPacket(dummyPacket);
        }
    }

    private void sendCancelPacket(CancelPacket cancelPacket) {
        try {
            System.out.println("Cancel packet with an id: " + cancelPacket.getId()[0] + " is being sent back to server.");
            synchronized (out) {
                out.write(cancelPacket.getPacketType());
                out.write(cancelPacket.getLength());
                out.write(cancelPacket.getId());
                out.flush();
            }
            System.out.println("Cancel packet with an id: " + cancelPacket.getId()[0] + " was sent back to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDummyPacket(DummyPacket dummyPacket) {
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

    public synchronized void addPacketToQueue(DummyPacket dummyPacket) {
        packetQueue.addLast(dummyPacket);
    }

}
