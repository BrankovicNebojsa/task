package thread;

import json.JsonPersistence;
import packet.DummyPacket;

import java.io.IOException;
import java.io.OutputStream;

public class ExecutionThread extends Thread {

    private final OutputStream out;
    private final DummyPacket dummyPacket;
    private final int threadNumber;
    private final JsonPersistence jsonPersistence;

    public ExecutionThread(OutputStream out, DummyPacket dummyPacket, int threadNumber, JsonPersistence jsonPersistence) {
        this.out = out;
        this.dummyPacket = dummyPacket;
        this.threadNumber = threadNumber;
        this.jsonPersistence = jsonPersistence;
    }

    @Override
    public void run() {
        try {
            System.out.println("Thread " + threadNumber + " is saving data to json.");
            jsonPersistence.addToJsonFile(this.dummyPacket);
            System.out.println("Thread " + threadNumber + " saved data to json.");
            System.out.println("Thread " + threadNumber + " is delayed for " + this.dummyPacket.getDelay()[0] + " seconds.");
            sleep(this.dummyPacket.getDelay()[0] * 1000);
            System.out.println("Thread " + threadNumber + " has finished sleeping for " + this.dummyPacket.getDelay()[0] + " seconds.");
            System.out.println("Thread " + threadNumber + " is able to send the packet back to server.");
            synchronized (out) {
                System.out.println("Thread " + threadNumber + " is sending the packet back to server.");
                out.write(this.dummyPacket.getPacketType());
                out.write(this.dummyPacket.getLength());
                out.write(this.dummyPacket.getId());
                out.write(this.dummyPacket.getDelay());
                out.flush();
            }
            System.out.println("Thread " + threadNumber + " has finished sending the packet back to server.");
            System.out.println("Thread " + threadNumber + " has started deleting the packet from json file.");
            jsonPersistence.removeFromJsonFile(this.dummyPacket.getId());
            System.out.println("Thread " + threadNumber + " has finished deleting the packet from json file.");
        } catch (InterruptedException ex) {
            System.out.println("Error in Thread " + threadNumber + ": " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error while trying to write to OutputStream. " + ex.getMessage());
        }
    }
}