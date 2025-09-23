import repo.MedicoRepo;
import repo.PacienteRepo;
import model.Medico;
import model.Paciente;
import model.Especialidade;
import model.PlanoBasico;

public class Main {
  public static void main(String[] args) throws Exception {
    // TESTE: Médicos
    MedicoRepo medRepo = new MedicoRepo("data/medicos.csv");
    System.out.println("[Medicos] antes: " + medRepo.listarMedicos().size());
    boolean ok1 = medRepo.cadastrarMedico(
      new Medico("Ana Souza", "11122233344", 40, "CRM-DF-12345", Especialidade.CARDIOLOGIA, 350.0)
    );
    System.out.println("Cadastrou Ana? " + ok1);
    System.out.println("[Medicos] depois: " + medRepo.listarMedicos().size());

    // TESTE: Pacientes
    PacienteRepo pacRepo = new PacienteRepo("data/pacientes.csv");
    System.out.println("[Pacientes] antes: " + pacRepo.listarTodos().size());

    // cria um paciente (mesmo CPF do teste anterior, pra exercitar a checagem)
    Paciente p = new Paciente("Luana", "12345678900", 21);
    p.setPlano(new PlanoBasico()); // opcional

    // só adiciona se ainda não existir
    if (pacRepo.buscarPorCpf(p.getCpf()).isEmpty()) {
      pacRepo.adicionar(p);
      System.out.println("[Pacientes] adicionado agora.");
    } else {
      System.out.println("[Pacientes] já existia, não vou adicionar.");
    }

    System.out.println("[Pacientes] depois: " + pacRepo.listarTodos().size());
  }
}

