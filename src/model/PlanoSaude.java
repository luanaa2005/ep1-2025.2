package model;

// A interface diz quais métodos todo plano deve ter. 
//assim, podemos trocar de plano e o resto do sistema 
//continua funcionando (polimorfismo).


public interface PlanoSaude {
    /**
     * Calcula o preço final de uma consulta após aplicar as regras do plano.
     *
     * @param esp        especialidade do médico (ex.: CARDIOLOGIA)
     * @param idade      idade do paciente (pode ter regras para 60+)
     * @param precoBase  custo base da consulta (do médico)
     * @return           preço final (nunca negativo)
     */
    double aplicarDesconto(Especialidade esp, int idade, double precoBase);

    /**
     * Indica se o plano torna internações com duração < 7 dias gratuitas.
     */
    boolean internacaoGratuitaAte7Dias();
}