package engine.transfer.receiver.exception;

public class PassphraseRequiredException extends ReceiverException {


    public PassphraseRequiredException(long keyID) {
        super(keyID);
    }
}
