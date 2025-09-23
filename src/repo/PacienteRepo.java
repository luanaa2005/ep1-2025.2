// PacienyteRepo.java é a ponte entre o modelo Paciente e o arquivo CSV.
// Permite carregar e salvar pacientes.

package repo;

import model.Paciente; // entidade Paciente
import model.PlanoBasico; // plano de saúde básico

import java.util.*; // essanimportação é para List, ArrayList, Optional
import java.nio.file.*; // para Paths
import java.io.IOException; // para IOException

public class PacienteRepo {

    private final String caminhoArquivo; // ex.: "data/pacientes.csv"

    public PacienteRepo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    // Carrega todos os pacientes do arquivo CSV
    public List<Paciente> carregarTodos() throws IOException {
        List<String> linhas = CSVUtil.lerLinhas(caminhoArquivo);
        List<Paciente> pacientes = new ArrayList<>();

        for (String linha : linhas) {
            String[] colunas = linha.split(";");
            if (colunas.length >= 4) {
                String cpf = colunas[0];
                String nome = colunas[1];
                int idade = Integer.parseInt(colunas[2]);
                String tipoPlano = colunas[3];

                // Por enquanto, só temos PlanoBasico
                Paciente paciente = new Paciente(nome, cpf, idade, new PlanoBasico());
                pacientes.add(paciente);
            }
        }
        return pacientes;
    }

    // Salva todos os pacientes no arquivo CSV (sobrescreve)
    public void salvarTodos(List<Paciente> pacientes) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (Paciente p : pacientes) {
            String linha = String.join(";", p.getCpf(), p.getNome(),
                    String.valueOf(p.getIdade()), "BASICO" /* tipoPlano */);
            linhas.add(linha);
        }
        CSVUtil.escreverLinhas(caminhoArquivo, linhas);
    }

    // Busca um paciente pelo CPF
    public Optional<Paciente> buscarPorCpf(String cpf) throws IOException {
        List<Paciente> pacientes = carregarTodos();
        return pacientes.stream()
                .filter(p -> p.getCpf().equals(cpf))
                .findFirst();
    }
}
