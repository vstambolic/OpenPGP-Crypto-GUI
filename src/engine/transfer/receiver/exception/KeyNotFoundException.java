package engine.transfer.receiver.exception;

public class KeyNotFoundException extends ReceiverException {

    public KeyNotFoundException(long keyID) {
        super(keyID);
    }
}
