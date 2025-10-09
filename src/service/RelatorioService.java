package service;

import model.*;                  // Consulta, Especialidade, StatusConsulta, Paciente, Internacao...
import repo.*;                   // PacienteRepo, MedicoRepo, ConsultaRepo, InternacaoRepo

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioService {

    private final PacienteRepo   pacienteRepo;
    private final MedicoRepo     medicoRepo;
    private final ConsultaRepo   consultaRepo;
    private final InternacaoRepo internacaoRepo;

    public RelatorioService(PacienteRepo p, MedicoRepo m, ConsultaRepo c, InternacaoRepo i) {
        this.pacienteRepo   = p;
        this.medicoRepo     = m;
        this.consultaRepo   = c;
        this.internacaoRepo = i;
    }

    // ===== 1) CONSULTAS FUTURAS (com filtros opcionais) =====
    public List<Consulta> consultasFuturas(String cpfPaciente, String crmMedico, Especialidade esp) throws Exception {
        List<Consulta> todas = consultaRepo.carregarTodos();
        LocalDateTime agora = LocalDateTime.now();

        return todas.stream()
                .filter(c -> c.getDataHora().isAfter(agora))                     // só futuras
                .filter(c -> c.getStatus() != StatusConsulta.CANCELADA)          // ignora canceladas
                .filter(c -> cpfPaciente == null || cpfPaciente.isBlank() ||
                             c.getPaciente().getCpf().equals(cpfPaciente))       // filtro paciente
                .filter(c -> crmMedico == null || crmMedico.isBlank() ||
                             c.getMedico().getCrm().equalsIgnoreCase(crmMedico)) // filtro médico
                .filter(c -> esp == null || c.getMedico().getEspecialidade() == esp) // filtro esp.
                .sorted(Comparator.comparing(Consulta::getDataHora))             // mais próximas primeiro
                .toList();
    }

    // ===== 2) CONSULTAS PASSADAS =====
    public List<Consulta> consultasPassadas(String cpfPaciente, String crmMedico, Especialidade esp) throws Exception {
        List<Consulta> todas = consultaRepo.carregarTodos();
        LocalDateTime agora = LocalDateTime.now();

        return todas.stream()
                .filter(c -> c.getDataHora().isBefore(agora))
                .filter(c -> c.getStatus() != StatusConsulta.CANCELADA)
                .filter(c -> cpfPaciente == null || cpfPaciente.isBlank()
                        || c.getPaciente().getCpf().equals(cpfPaciente))
                .filter(c -> crmMedico == null || crmMedico.isBlank()
                        || c.getMedico().getCrm().equalsIgnoreCase(crmMedico))
                .filter(c -> esp == null || c.getMedico().getEspecialidade() == esp)
                .sorted(Comparator.comparing(Consulta::getDataHora).reversed()) // mais recentes primeiro
                .toList();
    }

    // ===== 2.1) HISTÓRICO de consultas por paciente =====
    public List<Consulta> historicoConsultasDoPaciente(String cpf) throws Exception {
        if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("CPF vazio");
        List<Consulta> todas = consultaRepo.carregarTodos();
        return todas.stream()
                .filter(c -> c.getPaciente().getCpf().equals(cpf))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .toList();
    }

    // ===== 2.2) HISTÓRICO de internações por paciente =====
    public List<Internacao> historicoInternacoesDoPaciente(String cpf) throws Exception {
        if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("CPF vazio");
        List<Internacao> todas = internacaoRepo.carregarTodos();
        return todas.stream()
                .filter(i -> i.getPaciente().getCpf().equals(cpf))
                .sorted(Comparator.comparing(Internacao::getEntrada))
                .toList();
    }

    // ===== RECORDS usados no menu =====
    public record TopMedico(String nome, String crm, long quantidade) {}
    public record TopEspecialidade(Especialidade especialidade, long quantidade) {}
    public record InternadoAgora(String nome, String cpf, String quarto, long horas) {}
    public record EstatPlanos(long basico, long plus, long especial, long nenhum, double economiaTotal) {}

    // ===== 3) MÉDICO que mais atendeu (CONCLUÍDAS) =====
    public Optional<TopMedico> medicoQueMaisAtendeu() throws Exception {
        List<Consulta> todas = consultaRepo.carregarTodos();

        Map<String, Long> porCrm = todas.stream()
                .filter(c -> c.getStatus() == StatusConsulta.CONCLUIDA)
                .collect(Collectors.groupingBy(c -> c.getMedico().getCrm(), Collectors.counting()));

        return porCrm.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> {
                    String crm = e.getKey();
                    long qtd   = e.getValue();
                    String nome = medicoRepo.buscarPorCrm(crm)
                            .map(Medico::getNome)
                            .orElse(crm);
                    return new TopMedico(nome, crm, qtd);
                });
    }

    // ===== 4) ESPECIALIDADE MAIS PROCURADA (CONCLUÍDAS) =====
    public Optional<TopEspecialidade> especialidadeMaisProcurada() throws Exception {
        List<Consulta> todas = consultaRepo.carregarTodos();

        Map<Especialidade, Long> porEsp = todas.stream()
                .filter(c -> c.getStatus() == StatusConsulta.CONCLUIDA)
                .collect(Collectors.groupingBy(c -> c.getMedico().getEspecialidade(), Collectors.counting()));

        return porEsp.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new TopEspecialidade(e.getKey(), e.getValue()));
    }

    // ===== 5) PACIENTES INTERNADOS AGORA (com duração em horas) =====
    public List<InternadoAgora> pacientesInternadosAgora() throws Exception {
        List<Internacao> todas = internacaoRepo.carregarTodos();
        LocalDateTime agora = LocalDateTime.now();

        return todas.stream()
                .filter(i -> i.getSaida() == null) // ainda internados
                .map(i -> {
                    long horas = Math.max(0, Duration.between(i.getEntrada(), agora).toHours());
                    return new InternadoAgora(
                            i.getPaciente().getNome(),
                            i.getPaciente().getCpf(),
                            i.getQuarto(),
                            horas
                    );
                })
                .sorted(Comparator.comparingLong(InternadoAgora::horas).reversed())
                .toList();
    }

    // ===== 6) PLANOS: contagem por tipo + economia total =====
    public EstatPlanos estatisticaPlanos() throws Exception {
        // contagem por tipo de plano
        List<Paciente> pacientes = pacienteRepo.listarTodos();
        long basico = pacientes.stream().filter(p -> p.getPlano() instanceof PlanoBasico).count();
        long plus   = pacientes.stream().filter(p -> p.getPlano() instanceof PlanoPlus).count();
        long esp    = pacientes.stream().filter(p -> p.getPlano() instanceof PlanoEspecial).count();
        long nenhum = pacientes.stream().filter(p -> p.getPlano() == null).count();

        double economia = 0.0;

        // economia em CONSULTAS: considerar apenas CONCLUÍDAS
        for (Consulta c : consultaRepo.carregarTodos()) {
            if (c.getStatus() == StatusConsulta.CONCLUIDA) {
                double base = c.getMedico().getCustoBaseConsulta();
                double diff = Math.max(0, base - c.getPrecoFinal());
                economia += diff;
            }
        }

        // economia em INTERNAÇÕES: baseline (dias * custoBaseDia) - valor cobrado (calcularCustoTotal)
        for (Internacao i : internacaoRepo.carregarTodos()) {
            if (i.getSaida() == null) continue; // só completas
            long dias = Math.max(1, Duration.between(i.getEntrada(), i.getSaida()).toDays());
            double baseline = dias * i.getCustoBaseDia();
            double cobrado  = i.calcularCustoTotal(); // já considera gratuidades <7d no seu modelo
            double diff = Math.max(0, baseline - cobrado);
            economia += diff;
        }

        return new EstatPlanos(basico, plus, esp, nenhum, economia);
    }
}
