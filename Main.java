import repo.PacienteRepo;
import model.PlanoPlus;
import model.PlanoEspecial;
import repo.MedicoRepo;
import repo.ConsultaRepo;
import repo.InternacaoRepo;

import service.AgendamentoService;
import service.InternacaoService;
import service.RelatorioService;
import model.Paciente;
import model.Medico;
import model.Especialidade;
import model.Consulta;
import model.Internacao;

import java.time.LocalDateTime;

public class Main {
  public static void main(String[] args) throws Exception {
    // ===== Repositórios =====
    PacienteRepo pacRepo = new PacienteRepo("data/pacientes.csv");
    MedicoRepo   medRepo = new MedicoRepo("data/medicos.csv");
    // ConsultaRepo que reconstroi objetos a partir do CSV
    ConsultaRepo conRepo = new ConsultaRepo("data/consultas.csv", pacRepo, medRepo);

    // ===== Dados de teste (não duplica) =====
    String cpfTeste = "99988877766";
    if (pacRepo.buscarPorCpf(cpfTeste).isEmpty()) {
      pacRepo.adicionar(new Paciente("Paciente Teste", cpfTeste, 30));
    }
    if (medRepo.buscarPorCrm("CRM-TEST-1").isEmpty()) {
      medRepo.cadastrarMedico(
        new Medico("Dra. Ana", "12312312300", 40,
                   "CRM-TEST-1", Especialidade.CARDIOLOGIA, 300.0)
      );
    }
    if (medRepo.buscarPorCrm("CRM-TEST-2").isEmpty()) {
      medRepo.cadastrarMedico(
        new Medico("Dr. Bruno", "98798798700", 45,
                   "CRM-TEST-2", Especialidade.GERAL, 250.0)
      );
    }

    // ===== Dia 3/4 – Consultas =====
    AgendamentoService svc = new AgendamentoService(pacRepo, medRepo, conRepo);
    LocalDateTime dh = LocalDateTime.now()
        .plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

    System.out.println("\n--- Caso 1: agendar (SUCESSO) ---");
    Consulta c1 = svc.agendar(cpfTeste, "CRM-TEST-1", dh, "Sala 1");
    System.out.println("OK -> id=" + c1.getId() + " | preço=" + c1.getPrecoFinal());

    System.out.println("\n--- Caso 2: conflito de MÉDICO ---");
    try {
      svc.agendar(cpfTeste, "CRM-TEST-1", dh, "Sala 2");
      System.out.println("ERRO: deveria ter dado conflito de médico.");
    } catch (Exception e) {
      System.out.println("Esperado (médico): " + e.getMessage());
    }

    System.out.println("\n--- Caso 3: conflito de LOCAL ---");
    try {
      svc.agendar(cpfTeste, "CRM-TEST-2", dh, "Sala 1");
      System.out.println("ERRO: deveria ter dado conflito de local.");
    } catch (Exception e) {
      System.out.println("Esperado (local): " + e.getMessage());
    }

    System.out.println("\n--- Caso 4: concluir e tentar cancelar ---");
    svc.concluir(c1.getId(), "Gripe", "Dipirona 500mg");
    System.out.println("Concluída com sucesso.");
    try {
      svc.cancelar(c1.getId());
      System.out.println("ERRO: não era para cancelar concluída.");
    } catch (Exception e) {
      System.out.println("Esperado (cancelar concluída): " + e.getMessage());
    }
    System.out.println("\n→ Abra o arquivo data/consultas.csv para ver os dados persistidos.");

    System.out.println("\n===== Testes de PlanoSaude (Consultas) =====");

    // paciente sênior para ver +5%
    String cpfSenior = "55544433322";
    if (pacRepo.buscarPorCpf(cpfSenior).isEmpty()) {
        pacRepo.adicionar(new Paciente("Paciente Senior", cpfSenior, 65));
    }

    // PlanoPlus no paciente de teste (30 anos) -> CARDIOLOGIA (base 300)
    pacRepo.buscarPorCpf(cpfTeste).ifPresent(p -> {
        p.setPlano(new PlanoPlus());
        try { pacRepo.salvarNoArquivo(); } catch (Exception ignored) {}
    });

    LocalDateTime dhPlus = LocalDateTime.now()
            .plusDays(2).withHour(9).withMinute(0).withSecond(0).withNano(0);

    Consulta cPlus = svc.agendar(cpfTeste, "CRM-TEST-1", dhPlus, "Sala 3");
    System.out.println("PlanoPlus (30 anos, CARDIO) -> preço = " + cPlus.getPrecoFinal()
            + " (esperado ~ 240.0)");

    // PlanoPlus em paciente 65+ (CARDIO) -> 25% -> ~225
    pacRepo.buscarPorCpf(cpfSenior).ifPresent(p -> {
        p.setPlano(new PlanoPlus());
        try { pacRepo.salvarNoArquivo(); } catch (Exception ignored) {}
    });

    Consulta cPlusSenior = svc.agendar(cpfSenior, "CRM-TEST-1", dhPlus.plusHours(1), "Sala 4");
    System.out.println("PlanoPlus (65 anos, CARDIO) -> preço = " + cPlusSenior.getPrecoFinal()
            + " (esperado ~ 225.0)");

    // ===== Dia 5 – Internações =====
    System.out.println("\n===== Dia 5 - Internações =====");
    InternacaoRepo intRepo = new InternacaoRepo("data/internacoes.csv");
    InternacaoService intSvc = new InternacaoService(pacRepo, medRepo, intRepo);

    // ===== Dia 7 – Relatórios =====
    RelatorioService rel = new RelatorioService(pacRepo, medRepo, conRepo, intRepo);

    // Histórico por paciente (use um CPF que você tem no CSV)
    var histCons = rel.historicoConsultasDoPaciente("99988877766");
    System.out.println("Histórico de CONSULTAS do paciente 99988877766: " + histCons.size());

    var histInt = rel.historicoInternacoesDoPaciente("99988877766");
    System.out.println("Histórico de INTERNAÇÕES do paciente 99988877766: " + histInt.size());

    // 1) Consultas FUTURAS (sem filtro)
    var futuras = rel.consultasFuturas(null, null, null);
    System.out.println("\n===== Relatórios =====");
    System.out.println("Consultas FUTURAS (todas): " + futuras.size());

    // 1a) Futuras filtrando pelo CRM do cardiologista
    var futurasCardio = rel.consultasFuturas(null, "CRM-TEST-1", null);
    System.out.println("Futuras (CRM-TEST-1): " + futurasCardio.size());

    // 1b) Futuras filtrando por especialidade GERAL
    var futurasGeral = rel.consultasFuturas(null, null, Especialidade.GERAL);
    System.out.println("Futuras (GERAL): " + futurasGeral.size());

    // 2) Consultas PASSADAS (sem filtro)
    var passadas = rel.consultasPassadas(null, null, null);
    System.out.println("Consultas PASSADAS (todas): " + passadas.size());

    System.out.println("\n===== Teste PlanoEspecial (Internação <7 dias grátis) =====");
    String cpfPlanoEsp = "22211100099";
    if (pacRepo.buscarPorCpf(cpfPlanoEsp).isEmpty()) {
        pacRepo.adicionar(new Paciente("Paciente Especial", cpfPlanoEsp, 40));
    }
    pacRepo.buscarPorCpf(cpfPlanoEsp).ifPresent(p -> {
        p.setPlano(new PlanoEspecial());
        try { pacRepo.salvarNoArquivo(); } catch (Exception ignored) {}
    });

    LocalDateTime inicioEsp = LocalDateTime.now().withSecond(0).withNano(0);
    // quarto 103 para não conflitar com seus testes (101/102)
    Internacao iEsp = intSvc.internar(cpfPlanoEsp, "CRM-TEST-1", "103", inicioEsp, 300.0);
    intSvc.alta(iEsp.getId(), inicioEsp.plusDays(3)); // < 7 dias
    System.out.println("PlanoEspecial: custo (3 dias) = " + iEsp.calcularCustoTotal()
            + " (esperado 0.0)");

    LocalDateTime agora = LocalDateTime.now().withSecond(0).withNano(0);

    // 1) Internar (OK)
    Internacao i1 = intSvc.internar(cpfTeste, "CRM-TEST-1", "101", agora, 200.0);
    System.out.println("Internado -> id=" + i1.getId() + ", quarto=101");

    // 2A) Tentar internar MESMO paciente em OUTRO quarto, SEM alta -> deve barrar por "paciente já ativo"
    try {
        intSvc.internar(cpfTeste, "CRM-TEST-2", "102", agora.plusHours(1), 220.0);
        System.out.println("ERRO: deveria barrar paciente com internação ativa.");
    } catch (Exception e) {
        System.out.println("Esperado (paciente já ativo): " + e.getMessage());
    }

    // 2) Quarto ocupado (ERRO esperado)
    try {
      intSvc.internar(cpfTeste, "CRM-TEST-2", "101", agora.plusHours(2), 220.0);
      System.out.println("ERRO: deveria acusar quarto ocupado.");
    } catch (Exception e) {
      System.out.println("Esperado (quarto ocupado): " + e.getMessage());
    }

    // 3) Alta amanhã 08:00
    LocalDateTime amanha8 = agora.plusDays(1).withHour(8).withMinute(0);
    intSvc.alta(i1.getId(), amanha8);
    System.out.println("Alta OK. Custo total = " + i1.calcularCustoTotal());

    // 3A) Depois da alta, MESMO paciente pode internar de novo em outro quarto -> deve funcionar
    Internacao i2 = intSvc.internar(cpfTeste, "CRM-TEST-2", "102", amanha8.plusHours(1), 220.0);
    System.out.println("Internado novamente -> id=" + i2.getId() + ", quarto=102 (após alta)");

    // 4) Cancelar após alta (ERRO esperado)
    try {
      intSvc.cancelar(i1.getId());
      System.out.println("ERRO: não era para cancelar após alta.");
    } catch (Exception e) {
      System.out.println("Esperado (cancelar após alta): " + e.getMessage());
    }

    // ===== Estatísticas extras (Dia 7) =====
    System.out.println("\n===== Estatísticas =====");

    // 1) Médico que mais atendeu (CONCLUÍDAS)
    rel.medicoQueMaisAtendeu().ifPresentOrElse(
        t -> System.out.println("Top médico: " + t.nome() + " (" + t.crm() + "), consultas concluídas = " + t.quantidade()),
        () -> System.out.println("Top médico: ainda não há consultas concluídas.")
    );

    // 2) Especialidade mais procurada (CONCLUÍDAS)
    rel.especialidadeMaisProcurada().ifPresentOrElse(
        e -> System.out.println("Especialidade mais procurada: " + e.especialidade() + " (" + e.quantidade() + ")"),
        () -> System.out.println("Especialidade mais procurada: ainda sem dados.")
    );

    // 3) Internados no momento
    var internadosAgora = rel.pacientesInternadosAgora();
    System.out.println("Internados agora: " + internadosAgora.size());
    internadosAgora.forEach(i ->
        System.out.println("- " + i.nome() + " (CPF " + i.cpf() + "), quarto " + i.quarto() + ", " + i.horas() + "h internado(s)")
    );

    // 4) Planos: adesão e economia total
    var planos = rel.estatisticaPlanos();
    System.out.printf("Planos — Básico:%d  Plus:%d  Especial:%d  Nenhum:%d  | Economia total: %.2f%n",
            planos.basico(), planos.plus(), planos.especial(), planos.nenhum(), planos.economiaTotal());

    System.out.println("→ Veja data/internacoes.csv");
  }
}
