package json;

import com.google.gson.*;
import packet.DummyPacket;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JsonPersistenceSingleton {
    private final Lock lock = new ReentrantLock();
    private static JsonArray jsonArray;
    private static JsonPersistenceSingleton instance;

    private JsonPersistenceSingleton() {
        try {
            jsonArray = getJsonArrayFromFile();
        } catch (IllegalStateException ex) {
            jsonArray = new JsonArray();
        }
    }

    public static JsonPersistenceSingleton getInstance() {
        if (instance == null) {
            instance = new JsonPersistenceSingleton();
        }
        return instance;
    }

    public void addToJsonFile(DummyPacket dummyPacket) {
        lock.lock();
        JsonObject jsonObject = dummyPacket.returnAsJsonObject();
        jsonArray.add(jsonObject);
        updateFile();
        lock.unlock();
    }

    public void removeFromJsonFile(byte[] id) {
        int[] temp = new int[4];
        lock.lock();
        Iterator<JsonElement> iterator = jsonArray.iterator();

        while (iterator.hasNext()) {
            JsonElement jsonElement = iterator.next();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray jsonArray2 = jsonObject.getAsJsonArray("id");
            for (int i = 0; i < 4; i++) {
                temp[i] = jsonArray2.get(i).getAsInt();
            }
            if (isSameId(temp, id)) {
                iterator.remove();
                break;
            }
        }
        updateFile();
        lock.unlock();
    }

    private void updateFile() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter fw = new FileWriter("json/packets.json");
            BufferedWriter out = new BufferedWriter(fw);
            String json = gson.toJson(jsonArray);
            out.write(json);
            out.close();
        } catch (ConcurrentModificationException ex) {
            System.out.println("Error in updateFile");
            throw ex;
        } catch (IOException ex) {
            System.out.println("Error in adding data to json file: " + ex.getMessage());
        }
    }

    public List<DummyPacket> getPreviousPackets() {
        List<DummyPacket> previousPackets = new ArrayList<>();

        try {
            jsonArray = getJsonArrayFromFile();
        } catch (IllegalStateException ex) {
            jsonArray = new JsonArray();
            return previousPackets;
        }

        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            previousPackets.add(new DummyPacket(jsonObject));
        }
        return previousPackets;
    }

    private boolean isSameId(int[] temp, byte[] id) {
        boolean status = false;
        for (int i = 0; i < 4; i++) {
            if ((byte) temp[i] != id[i]) {
                return false;
            } else {
                status = true;
            }
        }
        return status;
    }

    private JsonArray getJsonArrayFromFile() {
        try {
            FileReader fileReader = new FileReader("json/packets.json");
            return JsonParser.parseReader(fileReader).getAsJsonArray();
        } catch (IOException ex) {
            System.out.println("Error while getting json array from file: " + ex.getMessage());
            return null;
        } catch (IllegalStateException ex) {
            System.out.println("Json file is probably empty. " + ex.getMessage());
            throw ex;
        }
    }

}
