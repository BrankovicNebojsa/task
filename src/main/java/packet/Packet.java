package packet;

public abstract class Packet {

    protected byte[] packetType;
    protected byte[] length;
    protected byte[] id;

    public Packet() {
    }

    public Packet(byte[] packetType, byte[] length) {
        this.packetType = packetType;
        this.length = length;
    }

    public Packet(byte[] packetType, byte[] length, byte[] id) {
        this.packetType = packetType;
        this.length = length;
        this.id = id;
    }

    public byte[] getPacketType() {
        return packetType;
    }

    public byte[] getLength() {
        return length;
    }

    public byte[] getId() {
        return id;
    }

}
