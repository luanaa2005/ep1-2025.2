package model;

import java.time.LocalDateTime;

public class Internacao{
    private final String id;
    private final Paciente paciente;
    private final Medico medicoResponsavel;
    private final String quarto;
    private final LocalDateTime entrada;
    private LocalDateTime saida;
    private final double custoBaseDia;

    public Internacao(Paciente paciente, Medico medicoResponsavel, String quarto,LocalDateTime entrada, double custoBaseDia){
       if (paciente == null)                throw new IllegalArgumentException("Paciente nulo");
       if (medicoResponsavel == null)       throw new IllegalArgumentException("Médico responsável nulo");
       if (quarto == null || quarto.isBlank()) throw new IllegalArgumentException("Quarto vazio");
       if (entrada == null)                 throw new IllegalArgumentException("Entrada nula");
       if (custoBaseDia < 0)                throw new IllegalArgumentException("Custo base negativo");

        this.id = java.util.UUID.randomUUID().toString();
        this.paciente = paciente;               
        this.medicoResponsavel = medicoResponsavel;
        this.quarto = quarto;
        this.entrada = entrada;
        this.custoBaseDia = custoBaseDia;
        this.saida = null;
    }
    // === Getters ===
    public String getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedicoResponsavel() { return medicoResponsavel; }
    public String getQuarto() { return quarto; }
    public LocalDateTime getEntrada() { return entrada; }
    public LocalDateTime getSaida() { return saida; }
    public double getCustoBaseDia() { return custoBaseDia; }

    public void darAlta(LocalDateTime saida) {
        if (saida == null) {
            throw new IllegalArgumentException("Data/hora de saída nula");
        }
        if (this.saida != null) {
            throw new IllegalStateException("Internação já possui alta");
        }
        if (!saida.isAfter(this.entrada)) {
            throw new IllegalArgumentException("Saída deve ser após a entrada");
        }

        this.saida = saida;
    }

    public double calcularCustoTotal() {
         // se ainda não teve alta, calcula até agora (para relatório parcial)
        LocalDateTime fim = (this.saida != null) ? this.saida : LocalDateTime.now();

        long horas = java.time.Duration.between(this.entrada, fim).toHours();
        long dias = Math.max(1L, (long) Math.ceil(horas / 24.0)); // arredonda p/ cima e mínimo 1

        boolean gratis7dias = (this.paciente.getPlano() != null)
                && this.paciente.getPlano().internacaoGratuitaAte7Dias();

        if (gratis7dias && dias < 7) {
            return 0.0;
        }
        return dias * this.custoBaseDia;
    }
}
