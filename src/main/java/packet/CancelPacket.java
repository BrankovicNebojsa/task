package packet;

public class CancelPacket extends Packet {
    public CancelPacket(byte[] id) {
        super(new byte[]{2,0,0,0}, new byte[]{12,0,0,0});
        this.id = id;
    }
}
