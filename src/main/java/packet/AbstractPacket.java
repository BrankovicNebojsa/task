package packet;

public abstract class AbstractPacket implements Packet {
    protected byte[] packetType;
    protected byte[] length;
    protected byte[] id;

    public AbstractPacket() {
    }

    public AbstractPacket(byte[] packetType, byte[] length, byte[] id) {
        this.packetType = packetType;
        this.length = length;
        this.id = id;
    }

    public byte[] getPacketType() {
        return packetType;
    }

    public void setPacketType(byte[] packetType) {
        this.packetType = packetType;
    }

    public byte[] getLength() {
        return length;
    }

    public void setLength(byte[] length) {
        this.length = length;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }
}
