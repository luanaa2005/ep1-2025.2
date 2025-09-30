import repo.PacienteRepo;
import repo.MedicoRepo;
import repo.ConsultaRepo;
import service.AgendamentoService;

import model.Paciente;
import model.Medico;
import model.Especialidade;
import model.Consulta;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1) Repositórios apontando para os CSVs em /data
        PacienteRepo pacRepo = new PacienteRepo("data/pacientes.csv");
        MedicoRepo   medRepo = new MedicoRepo("data/medicos.csv");
        ConsultaRepo conRepo = new ConsultaRepo("data/consultas.csv");

        // 2) Garantir 1 paciente de teste (não duplica se já existir)
        String cpfTeste = "99988877766";
        if (pacRepo.buscarPorCpf(cpfTeste).isEmpty()) {
            // OBS: use o construtor que você tem: (nome, cpf, idade)
            pacRepo.adicionar(new Paciente("Paciente Teste", cpfTeste, 30));
        }

        // 3) Garantir 2 médicos de teste (não duplica)
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

        // 4) Service de agendamento (agora com persistência de consultas)
        AgendamentoService svc = new AgendamentoService(pacRepo, medRepo, conRepo);

        // horário de teste: amanhã às 10:00
        LocalDateTime dh = LocalDateTime.now()
            .plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

        // --------- Caso 1: Agendar (SUCESSO) ----------
        System.out.println("\n--- Caso 1: agendar (SUCESSO) ---");
        Consulta c1 = svc.agendar(cpfTeste, "CRM-TEST-1", dh, "Sala 1");
        System.out.println("OK -> id=" + c1.getId() + " | preço=" + c1.getPrecoFinal());

        // --------- Caso 2: Conflito de MÉDICO ----------
        System.out.println("\n--- Caso 2: conflito de MÉDICO ---");
        try {
            svc.agendar(cpfTeste, "CRM-TEST-1", dh, "Sala 2"); // mesmo médico + mesma hora
            System.out.println("ERRO: deveria ter dado conflito de médico.");
        } catch (Exception e) {
            System.out.println("Esperado (médico): " + e.getMessage());
        }

        // --------- Caso 3: Conflito de LOCAL ----------
        System.out.println("\n--- Caso 3: conflito de LOCAL ---");
        try {
            svc.agendar(cpfTeste, "CRM-TEST-2", dh, "Sala 1"); // outro médico, MESMO local+hora
            System.out.println("ERRO: deveria ter dado conflito de local.");
        } catch (Exception e) {
            System.out.println("Esperado (local): " + e.getMessage());
        }

        // --------- Caso 4: Concluir e tentar cancelar ----------
        System.out.println("\n--- Caso 4: concluir e tentar cancelar ---");
        String id = c1.getId();
        svc.concluir(id, "Gripe", "Dipirona 500mg");
        System.out.println("Concluída com sucesso.");

        try {
            svc.cancelar(id); // não pode cancelar consulta concluída
            System.out.println("ERRO: não era para cancelar uma concluída.");
        } catch (Exception e) {
            System.out.println("Esperado (cancelar concluída): " + e.getMessage());
        }

        System.out.println("\n→ Abra o arquivo data/consultas.csv para ver os dados persistidos.");
    }
}
