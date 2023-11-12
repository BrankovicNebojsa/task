import json.JsonPersistence;
import packet.DummyPacket;
import thread.ExecutionThread;
import thread.PreviousPacketsThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class Task {
    private InputStream in;
    private OutputStream out;
    private final JsonPersistence jsonPersistence;

    public Task() {
        this.jsonPersistence = new JsonPersistence();
    }

    public static void main(String[] args) throws Exception {
        Task task = new Task();
        task.connectWithServer();
        task.sendFromPreviousSession();
        task.startThisSession();
    }

    private void connectWithServer() {
        try {
            Socket socket = new Socket("hermes.plusplus.rs", 4000);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    private void sendFromPreviousSession() {
        List<DummyPacket> dummyPackets = jsonPersistence.getPreviousPackets();
        for (DummyPacket dummyPacket : dummyPackets) {
            PreviousPacketsThread previousPacketsThread = new PreviousPacketsThread(out, dummyPacket, jsonPersistence);
            previousPacketsThread.start();
        }
    }

    private void startThisSession() throws Exception {
        try {
            Instant date;
            int threadNumber = 0;

            while (true) {
                ++threadNumber;
                byte[] data = new byte[4];

                for (int i = 0; i < 4; i++) {
                    data[i] = (byte) in.read();
                }

                if (data[0] == 1) {
                    date = Instant.now(); // uzimam trenutno vreme da bih ga stavio kao atribut dummy paketa
                    System.out.println("Dummy packet");
                    byte[] data2 = new byte[12];
                    for (int i = 0; i < 12; i++) {
                        data2[i] = (byte) in.read();
                    }
                    ExecutionThread executionThread = new ExecutionThread(out, new DummyPacket(data, Arrays.copyOfRange(data2, 0, 4),
                            Arrays.copyOfRange(data2, 4, 8), Arrays.copyOfRange(data2, 8, 12), date), threadNumber, jsonPersistence);
                    executionThread.start();
                    System.out.println("Main thread created Thread " + threadNumber);
                } else if (data[0] == 2) {
                    for (byte b : data) {
                        System.out.println(b);
                    }
                    throw new Exception("Cancel packet!");
                } else {
                    throw new Exception("Error! First byte is neither 0 nor 1.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error!" + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error! " + e.getMessage());
            throw e;
        }
    }
}
