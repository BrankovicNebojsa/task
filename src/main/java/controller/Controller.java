package controller;

import json.JsonPersistenceSingleton;
import packet.CancelPacket;
import packet.DummyPacket;
import thread.ExecutionThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

public class Controller {

    public void execute() {
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
        JsonPersistenceSingleton jsonPersistenceSingleton = JsonPersistenceSingleton.getInstance();
        List<DummyPacket> dummyPackets = jsonPersistenceSingleton.getPreviousPackets();
        for (DummyPacket dummyPacket : dummyPackets) {
            ExecutionThread executionThread = new ExecutionThread(out, dummyPacket, false);
            executionThread.start();
        }
    }

    private void startThisSession(InputStream in, OutputStream out) throws Exception {
        while (true) {
            byte[] data = readBytesFromInputStream(in);

            if (data[0] == 1) {
                System.out.println("Dummy packet");
                DummyPacket dummyPacket = fillDummyPacket(in);
                dummyPacket.setPacketType(data);

                ExecutionThread executionThread = new ExecutionThread(out, dummyPacket, true);
                executionThread.start();
                System.out.println("Main thread created " + executionThread.getName());
            } else if (data[0] == 2) {
                System.out.println("Cancel packet!");
                CancelPacket cancelPacket = fillCancelPacket(in);
                cancelPacket.setPacketType(data);
                /*
                    Server nikada ne salje CANCEL paket, ali ukoliko bi se to promenilo
                    ovde moze da se uradi implementacija tog dela
                */
            } else {
                throw new Exception("Error! First byte is neither 0 nor 1.");
            }
        }
    }

    private byte[] readBytesFromInputStream(InputStream in) throws IOException {
        byte[] data = new byte[4];
        for (int i = 0; i < 4; i++) {
            data[i] = (byte) in.read();
        }
        return data;
    }

    private DummyPacket fillDummyPacket(InputStream in) throws IOException {
        DummyPacket dummyPacket = new DummyPacket();
        dummyPacket.setTimeReceived(Instant.now()); // uzimam trenutno vreme da bih znao kada moze da se vrati paket serveru
        dummyPacket.setLength(readBytesFromInputStream(in));
        dummyPacket.setId(readBytesFromInputStream(in));
        dummyPacket.setDelay(readBytesFromInputStream(in));
        return dummyPacket;
    }

    private CancelPacket fillCancelPacket(InputStream in) throws IOException {
        CancelPacket cancelPacket = new CancelPacket();
        cancelPacket.setLength(readBytesFromInputStream(in));
        cancelPacket.setId(readBytesFromInputStream(in));
        return cancelPacket;
    }
}
