package model;

import java.time.LocalDateTime; // data/hora da consulta
import java.util.UUID;          // gerador de id único (string aleatória)

// Representa uma consulta entre um Paciente e um Medico.
// Tem dados fixos (id, paciente, medico, dataHora, local, precoFinal)
// e um estado que muda (status, diagnostico, prescricao).
public class Consulta {

    // --- atributos principais (imutáveis depois de criada) ---
    private final String id;               // identificador único (UUID)
    private final Paciente paciente;       // quem será atendido
    private final Medico medico;           // quem atende
    private final LocalDateTime dataHora;  // quando
    private final String local;            // onde (ex.: "Sala 3")
    private final double precoFinal;       // preço já calculado no agendamento

    // --- estado que pode mudar ao longo do tempo ---
    private StatusConsulta status;         // AGENDADA -> CONCLUIDA / CANCELADA
    private String diagnostico;            // preenchido ao concluir
    private String prescricao;             // preenchido ao concluir

    // Construtor: cria consulta já como "AGENDADA"
    public Consulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String local, double precoFinal) {
        // 1) validações defensivas (evitam objetos inválidos)
        if (paciente == null) throw new IllegalArgumentException("Paciente nulo");
        if (medico == null)   throw new IllegalArgumentException("Médico nulo");
        if (dataHora == null) throw new IllegalArgumentException("dataHora nula");
        if (local == null || local.isBlank()) throw new IllegalArgumentException("local vazio");
        if (precoFinal < 0)   throw new IllegalArgumentException("preço negativo");

        // 2) inicializações dos campos "fixos"
        this.id = UUID.randomUUID().toString(); // gera id único
        this.paciente   = paciente;
        this.medico     = medico;
        this.dataHora   = dataHora;
        this.local      = local;
        this.precoFinal = precoFinal;

        // 3) estado inicial
        this.status = StatusConsulta.AGENDADA;
        // diagnostico/prescricao começam como null (não definidos ainda)
    }

    // --- getters (somente leitura) ---
    public String getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getLocal() { return local; }
    public double getPrecoFinal() { return precoFinal; }
    public StatusConsulta getStatus() { return status; }
    public String getDiagnostico() { return diagnostico; }
    public String getPrescricao() { return prescricao; }

    // Concluir consulta: só pode se ainda estiver AGENDADA
    public void concluir(String diagnostico, String prescricao) {
        if (this.status != StatusConsulta.AGENDADA)
            throw new IllegalStateException("Só é possível concluir consultas AGENDADAS.");
        this.diagnostico = diagnostico;
        this.prescricao  = prescricao;
        this.status      = StatusConsulta.CONCLUIDA;
    }

    // Cancelar consulta: não pode cancelar se já foi concluída
    public void cancelar() {
        if (this.status == StatusConsulta.CONCLUIDA)
            throw new IllegalStateException("Consulta concluída não pode ser cancelada.");
        this.status = StatusConsulta.CANCELADA;
    }
}
