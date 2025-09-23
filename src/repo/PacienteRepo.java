// PacienyteRepo.java é a ponte entre o modelo Paciente e o arquivo CSV.
// Permite carregar e salvar pacientes.

package repo;

import model.Paciente; // entidade Paciente
import model.PlanoBasico; // plano de saúde básico

import java.util.*; // essanimportação é para List, ArrayList, Optional
import java.nio.file.*; // para Paths
import java.io.IOException; // para IOException

public class PacienteRepo {

    // private final é responsavel por imutabilidade
    private final String caminhoArquivo; // caminho do arquivo CSV
    private final List<Paciente> pacientes; // lista em memória

    public PacienteRepo(String caminhoArquivo) throws IOException { // throws é o responsavel por tratar exceções
        this.caminhoArquivo = caminhoArquivo; // essa linha inicializa o atributo caminhoArquivo
        this.pacientes = new ArrayList<>(); // inicializa a lista vazia
        carregarDoArquivo(); // carrega pacientes do CSV ao criar o repositório
    }

    // Carrega pacientes do arquivo CSV para a lista em memória
    private void carregarDoArquivo() throws IOException { // essa linha é responsavel por tratar exceções
        List<String> linhas = CSVUtil.lerLinhas(caminhoArquivo); // lê todas as linhas do arquivo
        for (String linha : linhas) { // para cada linha
            String[] partes = linha.split(";"); // separa por ";"
            if (partes.length >= 4) { // espera 4 partes: cpf;nome;idade;tipoPlano
                String cpf = partes[0]; // 0: cpf
                String nome = partes[1]; // 1: nome
                int idade = Integer.parseInt(partes[2]); // 2: idade
                String tipoPlano = partes[3]; // 3: tipoPlano (BASICO ou NENHUM)

                Paciente paciente;
                if (tipoPlano.equalsIgnoreCase("BASICO")) { // equalsIgnoreCase é para ignorar maiusculas e minusculas
                    paciente = new Paciente(nome, cpf, idade, new PlanoBasico()); // essa linha cria um novo paciente com plano basico
                } else {
                    paciente = new Paciente(nome, cpf, idade, null); // essa linha cria um novo paciente sem plano
                }
                pacientes.add(paciente); // adiciona o paciente à lista
            }
        }
    }

    // Salva a lista de pacientes no arquivo CSV
    public void salvarNoArquivo() throws IOException { // essa linha é responsavel por tratar exceções
        List<String> linhas = new ArrayList<>(); // cria uma nova lista de linhas
        for (Paciente p : pacientes) { // para cada paciente na lista
            String tipoPlano = (p.getPlano() != null) ? "BASICO" : "NENHUM"; // verifica se o paciente tem plano
            // Cria a linha no formato: cpf;nome;idade;tipoPlano
            String linha = String.join(";", p.getCpf(), p.getNome(), String.valueOf(p.getIdade()), tipoPlano);
            linhas.add(linha);
        }
        CSVUtil.escreverLinhas(caminhoArquivo, linhas); // escreve todas as linhas no arquivo
    }

    // Adiciona um novo paciente
    public void adicionar(Paciente paciente) throws IOException {
        pacientes.add(paciente); // adiciona à lista
        salvarNoArquivo(); // salva após adicionar
    }

    // Lista todos os pacientes
    public List<Paciente> listarTodos() { // devolve uma cópia imutável da lista
        return Collections.unmodifiableList(pacientes); // Collections.unmodifiableList é para tornar a lista imutável
    }

    // Busca um paciente pelo CPF
    public Optional<Paciente> buscarPorCpf(String cpf) {
        return pacientes.stream() // usa stream para buscar
                .filter(p -> p.getCpf().equals(cpf)) // filtra pelo cpf
                .findFirst(); // devolve o primeiro encontrado, ou Optional.empty() se não achar
    }
}