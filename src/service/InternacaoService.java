package service;

import java.time.LocalDateTime;
import java.util.*;

import model.Internacao;     // <- importante!
import model.Paciente;
import model.Medico;

import repo.PacienteRepo;
import repo.MedicoRepo;
import repo.InternacaoRepo;


public class InternacaoService {

    private final List<Internacao> internacoes = new ArrayList<>();
    private final PacienteRepo pacienteRepo;
    private final MedicoRepo   medicoRepo;
    private final InternacaoRepo internacaoRepo;

    public InternacaoService(PacienteRepo pacienteRepo, MedicoRepo medicoRepo, InternacaoRepo internacaoRepo) {
        this.pacienteRepo = pacienteRepo;
        this.medicoRepo = medicoRepo;
        this.internacaoRepo = internacaoRepo;
    }

    private void salvar() {
        try { internacaoRepo.salvarTodos(internacoes); }
        catch (java.io.IOException e) { throw new RuntimeException("Erro salvando internacoes.csv", e); }
    }


    // Internar com checagem de sobreposição de intervalos por quarto
    public Internacao internar(String cpfPaciente,
                           String crmMedico,
                           String quarto,
                           LocalDateTime entrada,
                           double custoBaseDia) {
    // 1) validações
    if (cpfPaciente == null || cpfPaciente.isBlank()) throw new IllegalArgumentException("CPF vazio");
    if (crmMedico  == null || crmMedico.isBlank())    throw new IllegalArgumentException("CRM vazio");
    if (quarto     == null || quarto.isBlank())        throw new IllegalArgumentException("Quarto vazio");
    if (entrada    == null)                            throw new IllegalArgumentException("Entrada nula");
    if (custoBaseDia < 0)                              throw new IllegalArgumentException("Custo base negativo");

    // 2) buscar entidades
    Paciente paciente = pacienteRepo.buscarPorCpf(cpfPaciente)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + cpfPaciente));
    Medico medico = medicoRepo.buscarPorCrm(crmMedico)
            .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: " + crmMedico));

    // 3) regra de QUARTO OCUPADO (checa sobreposição)
    // Intervalos [A_início, A_fim) e [B_início, B_fim) se sobrepõem se:
    // A_início < B_fim E B_início < A_fim
    for (Internacao i : internacoes) {
        if (!i.getQuarto().equalsIgnoreCase(quarto)) continue;

        LocalDateTime aIni = i.getEntrada();
        LocalDateTime aFim = (i.getSaida() != null) ? i.getSaida() : LocalDateTime.MAX;

        LocalDateTime bIni = entrada;
        LocalDateTime bFim = LocalDateTime.MAX; // nova internação ainda sem alta

        boolean sobrepoe = aIni.isBefore(bFim) && bIni.isBefore(aFim);
        if (sobrepoe) {
            throw new IllegalStateException("Quarto " + quarto + " já ocupado no período.");
        }
    }

    // 3.1) Não permitir DUAS internações ATIVAS do MESMO paciente
    boolean jaAtivo = internacoes.stream()
        .anyMatch(i -> i.getPaciente().getCpf().equals(cpfPaciente) && i.getSaida() == null);
    if (jaAtivo) {
        throw new IllegalStateException("Paciente já possui internação ativa.");
    }


    // 4) criar, guardar e persistir
    Internacao nova = new Internacao(paciente, medico, quarto, entrada, custoBaseDia);
    internacoes.add(nova);
    salvar();

    // 5) retornar a internação criada
    return nova;
}


    public void alta(String idInternacao, LocalDateTime saida) {
        Internacao i = acharPorId(idInternacao);
        i.darAlta(saida);
        salvar();
    }

    public void cancelar(String idInternacao) {
        Internacao i = acharPorId(idInternacao);
        if (i.getSaida() != null) throw new IllegalStateException("Não é possível cancelar após alta.");
        internacoes.remove(i); // simples: remove a internação ativa
        salvar();
    }

    public List<Internacao> listarAtivas() {
        return internacoes.stream().filter(i -> i.getSaida() == null).toList();
    }

    private Internacao acharPorId(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id vazio");
        return internacoes.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Internação não encontrada: " + id));
    }
}
