import repo.PacienteRepo;
import repo.MedicoRepo;
import repo.ConsultaRepo;
import repo.InternacaoRepo;

import service.AgendamentoService;
import service.InternacaoService;
import service.RelatorioService;

import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class MainMenu {

    // ===== Configurações de UI =====
    private static final DateTimeFormatter FMT_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Scanner in = new Scanner(System.in);

    // ===== Custos automáticos por Especialidade =====
    private static final Map<Especialidade, Double> CUSTO_PADRAO = Map.of(
            Especialidade.CARDIOLOGIA, 300.0,
            Especialidade.PEDIATRIA,   200.0,
            Especialidade.GERAL,       250.0
    );

    // ===== Dependências =====
    private final PacienteRepo pacRepo;
    private final MedicoRepo   medRepo;
    private final AgendamentoService ag;
    private final InternacaoService  is;
    private final RelatorioService   rl;

    public MainMenu() throws Exception {
        this.pacRepo = new PacienteRepo("data/pacientes.csv");
        this.medRepo = new MedicoRepo("data/medicos.csv");
        ConsultaRepo   conRepo = new ConsultaRepo("data/consultas.csv", pacRepo, medRepo);
        InternacaoRepo intRepo = new InternacaoRepo("data/internacoes.csv");

        this.ag = new AgendamentoService(pacRepo, medRepo, conRepo);
        this.is = new InternacaoService(pacRepo, medRepo, intRepo);
        this.rl = new RelatorioService(pacRepo, medRepo, conRepo, intRepo);
    }

    public static void main(String[] args) throws Exception {
        new MainMenu().loop();
    }

    private void loop() {
        while (true) {
            clearScreen();
            System.out.println("===== HOSPITAL — MENU =====");
            System.out.println("1) Cadastrar paciente");
            System.out.println("2) Cadastrar médico");
            System.out.println("3) Agendar consulta");
            System.out.println("4) Concluir consulta");
            System.out.println("5) Cancelar consulta");
            System.out.println("6) Internar paciente");
            System.out.println("7) Dar alta");
            System.out.println("8) Cancelar internação");
            System.out.println("9) Relatórios rápidos");
            System.out.println("0) Sair");
            String op = ask("Opção: ");

            try {
                switch (op) {
                    case "1" -> { cadastrarPaciente();  pause(); }
                    case "2" -> { cadastrarMedico();    pause(); }
                    case "3" -> { agendarConsulta();    pause(); }
                    case "4" -> { concluirConsulta();   pause(); }
                    case "5" -> { cancelarConsulta();   pause(); }
                    case "6" -> { internar();           pause(); }
                    case "7" -> { alta();               pause(); }
                    case "8" -> { cancelarInternacao(); pause(); }
                    case "9" -> { relatorios();         pause(); }
                    case "0" -> { System.out.println("Tchau!"); return; }
                    default   -> { System.out.println("Opção inválida."); pause(); }
                }
            } catch (Exception e) {
                System.out.println("ERRO: " + e.getMessage());
                pause();
            }
        }
    }

    // ========= AÇÕES =========

    private void cadastrarPaciente() throws Exception {
        String nome = ask("Nome: ");
        String cpf  = ask("CPF: ");
        int idade   = Integer.parseInt(ask("Idade: "));
        String planoStr = ask("Plano (NENHUM/BASICO/PLUS/ESPECIAL): ").trim().toUpperCase();

        Paciente p = new Paciente(nome, cpf, idade);
        switch (planoStr) {
            case "BASICO"   -> p.setPlano(new PlanoBasico());
            case "PLUS"     -> p.setPlano(new PlanoPlus());
            case "ESPECIAL" -> p.setPlano(new PlanoEspecial());
            default         -> p.setPlano(null);
        }

        if (pacRepo.buscarPorCpf(cpf).isPresent()) {
            System.out.println("Já existe paciente com esse CPF. (não alterei o arquivo)");
        } else {
            pacRepo.adicionar(p);
            System.out.println("Paciente cadastrado.");
        }
    }

    private void cadastrarMedico() throws Exception {
        String nome = ask("Nome: ");
        String cpf  = ask("CPF: ");
        int idade   = Integer.parseInt(ask("Idade: "));
        String crm  = ask("CRM: ");

        System.out.println("Especialidades disponíveis: " + Arrays.toString(Especialidade.values()));
        Especialidade esp = Especialidade.valueOf(
                ask("Digite: CARDIOLOGIA, PEDIATRIA ou GERAL: ").trim().toUpperCase()
        );

        if (medRepo.buscarPorCrm(crm).isPresent()) {
            System.out.println("Já existe médico com esse CRM.");
            return;
        }

        // custo base automático por especialidade
        double custo = CUSTO_PADRAO.getOrDefault(esp, 250.0);
        System.out.println("Custo base definido automaticamente: " + custo);

        medRepo.cadastrarMedico(new Medico(nome, cpf, idade, crm, esp, custo));
        System.out.println("Médico cadastrado.");
    }

    private void agendarConsulta() throws Exception {
        String cpf = ask("CPF do paciente: ");
        String crm = ask("CRM do médico: ");
        LocalDateTime dh = askDateTime("Data/hora (dd/MM/yyyy HH:mm): ");
        String local = ask("Local: ");

        Consulta c = ag.agendar(cpf, crm, dh, local);
        System.out.println("Consulta agendada. ID=" + c.getId() + " | Preço=" + c.getPrecoFinal());
    }

    private void concluirConsulta() {
        String id = ask("ID da consulta: ");
        String diag = ask("Diagnóstico: ");
        String pres = ask("Prescrição: ");
        ag.concluir(id, diag, pres);
        System.out.println("Consulta concluída.");
    }

    private void cancelarConsulta() {
        String id = ask("ID da consulta: ");
        ag.cancelar(id);
        System.out.println("Consulta cancelada.");
    }

    private void internar() {
        String cpf = ask("CPF do paciente: ");
        String crm = ask("CRM do médico responsável: ");
        String quarto = ask("Quarto: ");
        LocalDateTime entrada = askDateTime("Entrada (dd/MM/yyyy HH:mm): ");
        double custoDia = Double.parseDouble(ask("Custo base por dia: "));
        Internacao i = is.internar(cpf, crm, quarto, entrada, custoDia);
        System.out.println("Internado. ID=" + i.getId());
    }

    private void alta() {
        String id = ask("ID da internação: ");
        LocalDateTime saida = askDateTime("Saída (dd/MM/yyyy HH:mm): ");
        is.alta(id, saida);
        System.out.println("Alta realizada.");
    }

    private void cancelarInternacao() {
        String id = ask("ID da internação: ");
        is.cancelar(id);
        System.out.println("Internação cancelada.");
    }

    private void relatorios() throws Exception {
        System.out.println("\n--- Relatórios rápidos ---");
        var futuras  = rl.consultasFuturas(null, null, null);
        var passadas = rl.consultasPassadas(null, null, null);
        System.out.println("Consultas FUTURAS: "  + futuras.size());
        System.out.println("Consultas PASSADAS: " + passadas.size());

        try {
            var internados = rl.pacientesInternadosAgora();
            System.out.println("Internados agora: " + internados.size());
            internados.forEach(i ->
                System.out.println("- " + i.nome() + " (CPF " + i.cpf() + "), quarto " + i.quarto() + ", " + i.horas() + "h")
            );
        } catch (NoSuchMethodError ignored) {
            // ok se você ainda não implementou esse relatório
        }
    }

    // ========= HELPERS =========

    private String ask(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    private LocalDateTime askDateTime(String prompt) {
        while (true) {
            String s = ask(prompt);
            try {
                return LocalDateTime.parse(s, FMT_BR);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use dd/MM/yyyy HH:mm (ex.: 03/10/2025 14:30).");
            }
        }
    }

    private void clearScreen() {
        // ANSI clear (funciona no Git Bash / PowerShell moderno)
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void pause() {
        System.out.print("\n[Enter para voltar ao menu]");
        in.nextLine();
    }
}
