package service;

import java.time.LocalDateTime;               // Consulta, Paciente, Medico, StatusConsulta, etc.
import java.util.*;
import model.*;
import repo.MedicoRepo;
import repo.PacienteRepo;           // List, ArrayList, Collections

public class AgendamentoService {

    // consultas em memória (a persistência entra no Dia 4)
    private final List<Consulta> consultas = new ArrayList<>();

    private final PacienteRepo pacienteRepo;
    private final MedicoRepo   medicoRepo;

    public AgendamentoService(PacienteRepo pacienteRepo, MedicoRepo medicoRepo) {
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo   = medicoRepo;
    }

    // Agenda uma nova consulta aplicando validações e descontos de plano
    public Consulta agendar(String cpfPaciente, String crmMedico, LocalDateTime dataHora, String local) {
        // 1) validar entradas
        if (cpfPaciente == null || cpfPaciente.isBlank()) throw new IllegalArgumentException("CPF do paciente vazio");
        if (crmMedico  == null || crmMedico.isBlank())    throw new IllegalArgumentException("CRM do médico vazio");
        if (dataHora   == null)                            throw new IllegalArgumentException("dataHora nula");
        if (local      == null || local.isBlank())         throw new IllegalArgumentException("local vazio");

        // 2) buscar entidades
        Paciente paciente = pacienteRepo.buscarPorCpf(cpfPaciente)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + cpfPaciente));
        Medico medico = medicoRepo.buscarPorCrm(crmMedico)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: " + crmMedico));

        // 3) conflito: mesmo médico + mesma data/hora (ignora CANCELADAS)
        boolean conflitoMedico = consultas.stream().anyMatch(c ->
                c.getStatus() != StatusConsulta.CANCELADA &&
                c.getMedico().getCrm().equalsIgnoreCase(crmMedico) &&
                c.getDataHora().equals(dataHora)
        );
        if (conflitoMedico) throw new IllegalStateException("Conflito: o médico já tem consulta nesse horário.");

        // 4) conflito: mesmo local + mesma data/hora (ignora CANCELADAS)
        boolean conflitoLocal = consultas.stream().anyMatch(c ->
                c.getStatus() != StatusConsulta.CANCELADA &&
                c.getLocal().equalsIgnoreCase(local) &&
                c.getDataHora().equals(dataHora)
        );
        if (conflitoLocal) throw new IllegalStateException("Conflito: o local já está ocupado nesse horário.");

        // 5) calcular preço final (com plano, se houver)
        double base = medico.getCustoBaseConsulta();
        double precoFinal = (paciente.getPlano() == null)
                ? base
                : paciente.getPlano().aplicarDesconto(
                        medico.getEspecialidade(),
                        paciente.getIdade(),
                        base
                  );

        // 6) criar, guardar e retornar
        Consulta nova = new Consulta(paciente, medico, dataHora, local, precoFinal);
        consultas.add(nova);
        return nova;
    }

    // Apoio para testes/relatórios
    public List<Consulta> listarTodas() {
        return Collections.unmodifiableList(consultas);
    }
}
