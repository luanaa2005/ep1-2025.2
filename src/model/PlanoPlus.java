package model;

// CARDIOLOGIA: 20%
//PEDIATRIA: 15%
//ORTOPEDIA: 12%
//GERAL: 10%
//60+ ganha +5% extra.


public class PlanoPlus implements PlanoSaude {

    @Override
    public double aplicarDesconto(Especialidade esp, int idade, double precoBase) {
        double desconto;
        switch (esp) {
            case CARDIOLOGIA:
                desconto = 0.20; // 20% de desconto
                break;
            case PEDIATRIA:
                desconto = 0.15; // 15% de desconto
                break;
            case GERAL:
                desconto = 0.10; // 10% de desconto
                break;
            default:
                desconto = 0.12; // 12% para outras especialidades
                break;
        }
        if (idade >= 60) {
            desconto += 0.05; // mais 5% se tiver 60+
        }
        double precoFinal = precoBase * (1 - desconto);
        return Math.max(precoFinal, 0); // nunca negativo
    }

    @Override
    public boolean internacaoGratuitaAte7Dias() {
        return true; // plano Plus tem internação gratuita
    }

    @Override
    public String toString() { return "Plano Plus"; }
}