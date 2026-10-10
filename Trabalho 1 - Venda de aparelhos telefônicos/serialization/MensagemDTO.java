package serialization;

import java.io.Serializable;

public class MensagemDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String operacao;
    private String payload;

    public MensagemDTO(String operacao, String payload) {
        this.operacao = operacao;
        this.payload = payload;
    }

    public String getOperacao() { return operacao; }
    public String getPayload() { return payload; }
}