package packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.Instant;

public interface Packet {

     JsonObject returnAsJsonObject();

     JsonArray returnBytesAsJsonArray(byte[] dataBytes);

     byte[] returnJsonArrayAsBytes(JsonArray jsonArray);

     Instant getDateFromString(String timeReceived);

     String getStringFromDate(Instant timeReceived);

}
