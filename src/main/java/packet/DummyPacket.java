package packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DummyPacket extends Packet {

    private byte[] delay;
    protected Instant timeReceived;

    public DummyPacket(JsonObject jsonObject) {
        this.packetType = returnJsonArrayAsBytes(jsonObject.get("packetType").getAsJsonArray());
        this.length = returnJsonArrayAsBytes(jsonObject.get("length").getAsJsonArray());
        this.id = returnJsonArrayAsBytes(jsonObject.get("id").getAsJsonArray());
        this.delay = returnJsonArrayAsBytes(jsonObject.get("delay").getAsJsonArray());
        this.timeReceived = getDateFromString(jsonObject.get("timeReceived").getAsString());
    }

    public DummyPacket(byte[] packetType, byte[] length, byte[] id, byte[] delay, Instant timeReceived) {
        super(packetType, length, id);
        this.delay = delay;
        this.timeReceived = timeReceived;
    }

    public byte[] getDelay() {
        return delay;
    }

    public Instant getTimeReceived() {
        return timeReceived;
    }

    public JsonObject returnAsJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("packetType", returnBytesAsJsonArray(this.getPacketType()));
        jsonObject.add("length", returnBytesAsJsonArray(this.getLength()));
        jsonObject.add("id", returnBytesAsJsonArray(this.getId()));
        jsonObject.add("delay", returnBytesAsJsonArray(this.getDelay()));
        jsonObject.addProperty("timeReceived", getStringFromDate(this.getTimeReceived()));

        return jsonObject;
    }

    public JsonArray returnBytesAsJsonArray(byte[] dataBytes) {
        JsonArray jsonArray = new JsonArray();
        for (byte b :
                dataBytes) {
            jsonArray.add(b);
        }
        return jsonArray;
    }

    public byte[] returnJsonArrayAsBytes(JsonArray jsonArray) {
        byte[] dataBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            dataBytes[i] = jsonArray.get(i).getAsByte();
        }
        return dataBytes;
    }

    private Instant getDateFromString(String timeReceived) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss:SSS dd.MM.yyyy");
        LocalDateTime localDateTime = LocalDateTime.parse(timeReceived, dtf);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    private String getStringFromDate(Instant timeReceived) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss:SSS dd.MM.yyyy");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(timeReceived, ZoneId.systemDefault());
        return dtf.format(localDateTime);
    }
}