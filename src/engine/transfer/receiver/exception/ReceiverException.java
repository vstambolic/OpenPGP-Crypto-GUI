package engine.transfer.receiver.exception;

public class ReceiverException extends Exception {
    public long getKeyId() {
        return keyId;
    }

    public String getKeyIdHexString() {
        return String.format("[0x%016X]", keyId);
    }
    private final long keyId;

    public ReceiverException(long keyID) {
        this.keyId = keyID;
    }



}
