import java.time.LocalDateTime;
import model.Consulta;
import model.Especialidade;
import model.Medico;
import model.Paciente;
import repo.MedicoRepo;
import repo.PacienteRepo;
import service.AgendamentoService;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1) Repositórios apontando para seus CSVs
        PacienteRepo pacRepo = new PacienteRepo("data/pacientes.csv");
        MedicoRepo   medRepo = new MedicoRepo("data/medicos.csv");

        // 2) Garantir que há 1 paciente e 2 médicos de teste (sem duplicar)
        String cpfTeste = "99988877766";
        if (pacRepo.buscarPorCpf(cpfTeste).isEmpty()) {
            // Se sua classe Paciente tiver construtor com plano:
            pacRepo.adicionar(new Paciente("Paciente Teste", cpfTeste, 30));
            // Se o seu Paciente tiver só (nome, cpf, idade), troque pela versão sem plano.
        }

        if (medRepo.buscarPorCrm("CRM-TEST-1").isEmpty()) {
            medRepo.cadastrarMedico(
                new Medico("Dra. Ana", "12312312300", 40, "CRM-TEST-1", Especialidade.CARDIOLOGIA, 300.0)
            );
        }
        if (medRepo.buscarPorCrm("CRM-TEST-2").isEmpty()) {
            medRepo.cadastrarMedico(
                new Medico("Dr. Bruno", "98798798700", 45, "CRM-TEST-2", Especialidade.GERAL, 250.0)
            );
        }

        // 3) Service de agendamento (versão do Dia 3, só com repos)
        AgendamentoService svc = new AgendamentoService(pacRepo, medRepo);

        // Horário de teste: amanhã às 10:00
        LocalDateTime dh = LocalDateTime.now().plusDays(1)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);

        // --- Caso 1: SUCESSO ---
        System.out.println("\n--- Caso 1: sucesso ---");
        Consulta c1 = svc.agendar(cpfTeste, "CRM-TEST-1", dh, "Sala 1");
        System.out.println("OK -> id=" + c1.getId() + " | preco=" + c1.getPrecoFinal());

        // --- Caso 2: CONFLITO DE MÉDICO (mesmo médico + mesma hora, outro local) ---
        System.out.println("\n--- Caso 2: conflito de MÉDICO ---");
        try {
            svc.agendar(cpfTeste, "CRM-TEST-1", dh, "Sala 2");
            System.out.println("ERRO: deveria ter dado conflito de médico e não deu.");
        } catch (Exception e) {
            System.out.println("Esperado (médico): " + e.getMessage());
        }

        // --- Caso 3: CONFLITO DE LOCAL (outro médico + mesma hora + MESMO local) ---
        System.out.println("\n--- Caso 3: conflito de LOCAL ---");
        try {
            svc.agendar(cpfTeste, "CRM-TEST-2", dh, "Sala 1");
            System.out.println("ERRO: deveria ter dado conflito de local e não deu.");
        } catch (Exception e) {
            System.out.println("Esperado (local): " + e.getMessage());
        }
    }
}
