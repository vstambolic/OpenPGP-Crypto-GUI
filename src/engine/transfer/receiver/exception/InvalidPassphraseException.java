package engine.transfer.receiver.exception;

public class InvalidPassphraseException extends ReceiverException {

    public InvalidPassphraseException(long keyID) {
        super(keyID);
    }
}
