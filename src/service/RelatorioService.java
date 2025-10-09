package service;

import model.*;             // Consulta, Especialidade, StatusConsulta, etc.
import repo.*;              // PacienteRepo, MedicoRepo, ConsultaRepo, InternacaoRepo
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioService {

    // Vamos ler direto dos repositórios pra incluir o que está no CSV
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
    // Filtros:
    // - cpfPaciente (pode ser null/blank -> ignora)
    // - crmMedico  (pode ser null/blank -> ignora)
    // - especialidade (pode ser null -> ignora)
    public List<Consulta> consultasFuturas(String cpfPaciente, String crmMedico, Especialidade esp) throws Exception {
        // lê TODAS as consultas do CSV (garante que relatórios enxergam dados persistidos)
        List<Consulta> todas = consultaRepo.carregarTodos();

        LocalDateTime agora = LocalDateTime.now();

        // stream -> filtra -> ordena -> coleta
        return todas.stream()
                // só FUTURAS
                .filter(c -> c.getDataHora().isAfter(agora))
                // ignora CANCELADAS (normal em relatórios)
                .filter(c -> c.getStatus() != StatusConsulta.CANCELADA)
                // filtro opcional por paciente
                .filter(c -> cpfPaciente == null || cpfPaciente.isBlank() ||
                             c.getPaciente().getCpf().equals(cpfPaciente))
                // filtro opcional por médico
                .filter(c -> crmMedico == null || crmMedico.isBlank() ||
                             c.getMedico().getCrm().equalsIgnoreCase(crmMedico))
                // filtro opcional por especialidade do médico
                .filter(c -> esp == null || c.getMedico().getEspecialidade() == esp)
                // ordena por data/hora ASC (mais próximas primeiro)
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .collect(Collectors.toList());
    }

    // ========== CONSULTAS PASSADAS ==========
    // Igual ao de futuras, mas:
    // - usa isBefore(agora)
    // - ignora CANCELADAS
    // - ordena por data/hora DESC (mais recentes primeiro)
    public List<Consulta> consultasPassadas(String cpfPaciente,
                                            String crmMedico,
                                            Especialidade esp) throws Exception {
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
                .sorted(Comparator.comparing(Consulta::getDataHora).reversed())
                .collect(java.util.stream.Collectors.toList());
       }

    // Histórico de CONSULTAS de um paciente (ordenado por data)
        public List<Consulta> historicoConsultasDoPaciente(String cpf) throws Exception {
        if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("CPF vazio");
        List<Consulta> todas = consultaRepo.carregarTodos(); // lê do CSV
        return todas.stream()
                .filter(c -> c.getPaciente().getCpf().equals(cpf))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .toList();
        }

        // Histórico de INTERNAÇÕES de um paciente (ordenado pela entrada)
        public List<Internacao> historicoInternacoesDoPaciente(String cpf) throws Exception {
        if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("CPF vazio");
        List<Internacao> todas = internacaoRepo.carregarTodos(); // lê do CSV
        return todas.stream()
                .filter(i -> i.getPaciente().getCpf().equals(cpf))
                .sorted(Comparator.comparing(Internacao::getEntrada))
                .toList();
        }


    // ===== 3) MÉDICO QUE MAIS ATENDEU (em consultas CONCLUÍDAS) =====
    // dica: agrupar por CRM do médico (String) e contar; pegar o maior
    // TODO: implementar

    // ===== 4) ESPECIALIDADE MAIS PROCURADA =====
    // dica: agrupar por c.getMedico().getEspecialidade() em CONCLUÍDAS; pegar o maior
    // TODO: implementar

    // ===== 5) PACIENTES INTERNADOS NO MOMENTO (com tempo) =====
    // dica: filtrar internacoes com saida == null; Duration.between(entrada, now)
    // TODO: implementar

    // ===== 6) PLANOS: quantidades por tipo e economia total =====
    // dica: contar tipos em pacientes.csv; economia = (custoBase - precoFinal) + gratuidades <7d
    // TODO: implementar
}
