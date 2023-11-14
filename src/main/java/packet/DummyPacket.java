package packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DummyPacket extends AbstractPacket {

    private byte[] delay;
    protected Instant timeReceived;

    public DummyPacket() {
        super();
    }

    public DummyPacket(JsonObject jsonObject) {
        this.packetType = returnJsonArrayAsBytes(jsonObject.get("packetType").getAsJsonArray());
        this.length = returnJsonArrayAsBytes(jsonObject.get("length").getAsJsonArray());
        this.id = returnJsonArrayAsBytes(jsonObject.get("id").getAsJsonArray());
        this.delay = returnJsonArrayAsBytes(jsonObject.get("delay").getAsJsonArray());
        this.timeReceived = getDateFromString(jsonObject.get("timeReceived").getAsString());
    }

    public byte[] getDelay() {
        return delay;
    }

    public void setDelay(byte[] delay) {
        this.delay = delay;
    }

    public Instant getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(Instant timeReceived) {
        this.timeReceived = timeReceived;
    }

    public Instant getTimeAfterDelay() {
        return this.getTimeReceived().plusSeconds(this.getDelay()[0]);
    }

    @Override
    public JsonObject returnAsJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("packetType", returnBytesAsJsonArray(this.getPacketType()));
        jsonObject.add("length", returnBytesAsJsonArray(this.getLength()));
        jsonObject.add("id", returnBytesAsJsonArray(this.getId()));
        jsonObject.add("delay", returnBytesAsJsonArray(this.getDelay()));
        jsonObject.addProperty("timeReceived", getStringFromDate(this.getTimeReceived()));

        return jsonObject;
    }

    @Override
    public JsonArray returnBytesAsJsonArray(byte[] dataBytes) {
        JsonArray jsonArray = new JsonArray();
        for (byte b :
                dataBytes) {
            jsonArray.add(b);
        }
        return jsonArray;
    }

    @Override
    public byte[] returnJsonArrayAsBytes(JsonArray jsonArray) {
        byte[] dataBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            dataBytes[i] = jsonArray.get(i).getAsByte();
        }
        return dataBytes;
    }

    @Override
    public Instant getDateFromString(String timeReceived) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss:SSS dd.MM.yyyy");
        LocalDateTime localDateTime = LocalDateTime.parse(timeReceived, dtf);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    @Override
    public String getStringFromDate(Instant timeReceived) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss:SSS dd.MM.yyyy");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(timeReceived, ZoneId.systemDefault());
        return dtf.format(localDateTime);
    }
}
