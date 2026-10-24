package rmi;

public class MensagemRPC {
    public int messageType;
    public int requestId;
    public String objectReference;
    public String methodId;
    public byte[] arguments; 

    public MensagemRPC(int messageType, int requestId, String objectReference, String methodId, byte[] arguments) {
        this.messageType = messageType;
        this.requestId = requestId;
        this.objectReference = objectReference;
        this.methodId = methodId;
        this.arguments = arguments;
    }
}
