package packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.Instant;

public class CancelPacket extends AbstractPacket {

    public CancelPacket() {
    }

    public CancelPacket(byte[] id) {
        super(new byte[]{2, 0, 0, 0}, new byte[]{12, 0, 0, 0}, id);
    }

    @Override
    public JsonObject returnAsJsonObject() {
        throw new UnsupportedOperationException("This method hasn't been implemented.");
    }

    @Override
    public JsonArray returnBytesAsJsonArray(byte[] dataBytes) {
        throw new UnsupportedOperationException("This method hasn't been implemented.");
    }

    @Override
    public byte[] returnJsonArrayAsBytes(JsonArray jsonArray) {
        throw new UnsupportedOperationException("This method hasn't been implemented.");
    }

    @Override
    public Instant getDateFromString(String timeReceived) {
        throw new UnsupportedOperationException("This method hasn't been implemented.");
    }

    @Override
    public String getStringFromDate(Instant timeReceived) {
        throw new UnsupportedOperationException("This method hasn't been implemented.");
    }
}
