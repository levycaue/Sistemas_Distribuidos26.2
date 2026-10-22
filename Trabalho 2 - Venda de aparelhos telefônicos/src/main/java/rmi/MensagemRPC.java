package rmi;

public class MensagemRPC {
    public int messageType; // 0 = Request, 1 = Reply
    public int requestId;
    public String objectReference;
    public String methodId;
    public byte[] arguments; // Argumentos codificados em JSON (Passagem por valor)

    public MensagemRPC(int messageType, int requestId, String objectReference, String methodId, byte[] arguments) {
        this.messageType = messageType;
        this.requestId = requestId;
        this.objectReference = objectReference;
        this.methodId = methodId;
        this.arguments = arguments;
    }
}
