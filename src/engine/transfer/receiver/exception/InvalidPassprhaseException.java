package engine.transfer.receiver.exception;

public class InvalidPassprhaseException extends ReceiverException {

    public InvalidPassprhaseException(long keyID) {
        super(keyID);
    }
}
