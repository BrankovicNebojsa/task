import json.JsonPersistence;
import packet.CancelPacket;
import packet.DummyPacket;
import thread.ExecutionThread;
import thread.PreviousPacketsThread;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

public class Task {

    public static void main(String[] args) {
        Task task = new Task();
        task.execute();
    }

    private void execute() {
        try (Socket socket = new Socket("hermes.plusplus.rs", 4000);
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {
            sendFromPreviousSession(out);
            startThisSession(in, out);
        } catch (Exception e) {
            System.out.println("Error in execute method!" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendFromPreviousSession(OutputStream out) {
        JsonPersistence jsonPersistence = JsonPersistence.getInstance();
        List<DummyPacket> dummyPackets = jsonPersistence.getPreviousPackets();
        for (DummyPacket dummyPacket : dummyPackets) {
            PreviousPacketsThread previousPacketsThread = new PreviousPacketsThread(out, dummyPacket);
            previousPacketsThread.start();
        }
    }

    private void startThisSession(InputStream in, OutputStream out) throws Exception {
        Instant time;
        int threadNumber = 0;

        while (true) {
            ++threadNumber;
            byte[] data = new byte[4];

            for (int i = 0; i < 4; i++) {
                data[i] = (byte) in.read();
            }

            if (data[0] == 1) {
                System.out.println("Dummy packet");
                time = Instant.now(); // uzimam trenutno vreme da bih ga stavio kao atribut dummy paketa
                DummyPacket dummyPacket = new DummyPacket();
                dummyPacket.setPacketType(data);
                dummyPacket.setTimeReceived(time);

                for (int j = 0; j < 3; j++) {
                    for (int i = 0; i < 4; i++) {
                        data[i] = (byte) in.read();
                    }
                    switch (j) {
                        case 0:
                            dummyPacket.setLength(data);
                            break;
                        case 1:
                            dummyPacket.setId(data);
                            break;
                        case 2:
                            dummyPacket.setDelay(data);
                            break;
                    }
                }

                ExecutionThread executionThread = new ExecutionThread(out, dummyPacket, threadNumber);
                executionThread.start();
                System.out.println("Main thread created Thread " + threadNumber);
            } else if (data[0] == 2) {
                System.out.println("Cancel packet!");
                CancelPacket cancelPacket = new CancelPacket();
                cancelPacket.setPacketType(data);
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < 8; i++) {
                        data[i] = (byte) in.read();
                    }
                    switch (j) {
                        case 0:
                            cancelPacket.setLength(data);
                            break;
                        case 1:
                            cancelPacket.setId(data);
                            break;
                    }
                }
            } else {
                throw new Exception("Error! First byte is neither 0 nor 1.");
            }
        }
    }
}
