package engine.transfer.receiver.exception;

public class SignerKeyNotFoundException extends ReceiverException{
    public SignerKeyNotFoundException(long keyID) {
        super(keyID);
    }
}
