package model;

// 10% de desconto geral e, se o paciente tiver 60+, mais 5%.

public class PlanoBasico implements PlanoSaude {

    @Override
    public double aplicarDesconto(Especialidade esp, int idade, double precoBase) {
        double desconto = 0.10; // 10% de desconto geral
        if (idade >= 60) {
            desconto += 0.05; // mais 5% se tiver 60+
        }
        double precoFinal = precoBase * (1 - desconto);
        return Math.max(precoFinal, 0); // nunca negativo
    }

    @Override
    public boolean internacaoGratuitaAte7Dias() {
        return false; // plano básico não tem internação gratuita
    }

    @Override
    public String toString() { return "Plano Básico"; }
}