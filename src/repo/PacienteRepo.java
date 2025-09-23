// PacienteRepo.java é a ponte entre o modelo Paciente e o arquivo CSV.
// Permite carregar e salvar pacientes.

// PacienteRepo.java — "ponte" entre o modelo Paciente e o arquivo CSV.
// Responsável por CARREGAR do CSV para a memória e SALVAR da memória para o CSV.
// Padrão: separador ';' e primeira linha como cabeçalho: "cpf;nome;idade;plano"

package repo;

import model.Paciente;     // entidade de domínio (nome, cpf, idade, plano)
import model.PlanoBasico;  // implementação de plano (por enquanto só este)

import java.util.*;        // List, ArrayList, Collections, Optional, Stream
import java.io.IOException; // IOException = exceções de entrada/saída de arquivo

public class PacienteRepo {

    // 'private final' = a REFERÊNCIA não muda depois do construtor (boa prática)
    private final String caminhoArquivo;  // ex.: "data/pacientes.csv"

    // Lista que mantém todos os pacientes em memória enquanto o programa roda
    private final List<Paciente> pacientes;

    // Construtor recebe o caminho do CSV e já carrega os dados
    // 'throws IOException' = declara que PODE lançar erro de arquivo; quem chama decide tratar
    public PacienteRepo(String caminhoArquivo) throws IOException {
        this.caminhoArquivo = caminhoArquivo; // guarda o caminho para usar em ler/gravar
        this.pacientes = new ArrayList<>();   // começa com lista vazia
        carregarDoArquivo();                  // carrega o CSV (se existir) para a lista
    }

    // Carrega pacientes do arquivo CSV para a lista em memória
    // Regra do formato: cpf;nome;idade;plano (plano vazio = sem plano)
    private void carregarDoArquivo() throws IOException {
        List<String> linhas = CSVUtil.lerLinhas(caminhoArquivo); // lê todas as linhas (ou [] se o arquivo não existir)
        pacientes.clear();                                       // limpa a lista para evitar duplicar dados

        if (linhas.isEmpty()) return;                            // nada para carregar

        // Se a primeira linha for um cabeçalho ("cpf;..."), pulamos ela
        int i = linhas.get(0).startsWith("cpf;") ? 1 : 0;

        // Percorre cada linha útil do arquivo
        for (; i < linhas.size(); i++) {
            String linha = linhas.get(i).trim();    // remove espaços extras
            if (linha.isEmpty()) continue;          // ignora linhas em branco

            // Divide pelos ';' PRESERVANDO campos vazios (ex.: "a;b;c;" -> ["a","b","c",""])
            List<String> partes = CSVUtil.dividirLinha(linha);

            // Conferimos se há ao menos cpf;nome;idade
            if (partes.size() < 3) continue;        // se faltar dado essencial, pula a linha

            String cpf       = partes.get(0);       // coluna 0 = CPF
            String nome      = partes.get(1);       // coluna 1 = Nome
            int idade        = Integer.parseInt(partes.get(2)); // coluna 2 = Idade (string -> int)
            String tipoPlano = (partes.size() >= 4) ? partes.get(3) : ""; // coluna 3 = Plano (pode estar vazia)

            // Cria o paciente com os 3 campos básicos (como modelamos)
            Paciente paciente = new Paciente(nome, cpf, idade);

            // Se houver plano no CSV, aplica (por enquanto só BASICO)
            if (tipoPlano != null && !tipoPlano.isBlank()) {
                if (tipoPlano.equalsIgnoreCase("BASICO")) {
                    paciente.setPlano(new PlanoBasico()); // paciente com plano básico
                }
                // FUTURO: PLUS, ESPECIAL, etc. (fazer if/else aqui quando criar)
            }

            // Adiciona na lista em memória
            pacientes.add(paciente);
        }
    }

    // Salva a lista de pacientes no arquivo CSV
    // Padrão adotado: primeira linha é o cabeçalho e plano vazio = sem plano
    public void salvarNoArquivo() throws IOException {
        List<String> linhas = new ArrayList<>();            // acumulador de linhas do arquivo
        linhas.add("cpf;nome;idade;plano");                 // cabeçalho (ajuda a ler/depurar)

        // Para cada paciente da lista, montar uma linha "cpf;nome;idade;plano"
        for (Paciente p : pacientes) {
            // Se não tiver plano, salvamos campo vazio ""; se tiver PlanoBasico, salvamos "BASICO"
            String tipoPlano = (p.getPlano() == null) ? "" : "BASICO"; // por enquanto só reconhecemos o básico

            // Junta os campos com ';' usando o util (evita bugs com split)
            String linha = CSVUtil.juntarCampos(List.of(
                    p.getCpf(),
                    p.getNome(),
                    String.valueOf(p.getIdade()),
                    tipoPlano
            ));

            linhas.add(linha); // adiciona ao arquivo a ser gravado
        }

        CSVUtil.escreverLinhas(caminhoArquivo, linhas); // grava tudo no disco (cria pasta 'data/' se precisar)
    }

    // Adiciona um novo paciente na lista e já salva no arquivo
    public void adicionar(Paciente paciente) throws IOException {
        if (paciente == null) throw new IllegalArgumentException("Paciente nulo"); // defesa contra erro de programação

        // Verifica duplicidade por CPF (regra natural: um CPF = um paciente)
        boolean cpfJaExiste = pacientes.stream().anyMatch(p -> p.getCpf().equals(paciente.getCpf()));
        if (cpfJaExiste) throw new IllegalArgumentException("CPF já cadastrado: " + paciente.getCpf());

        pacientes.add(paciente); // adiciona na memória
        salvarNoArquivo();       // persiste no CSV logo em seguida (simples e seguro para este trabalho)
    }

    // Lista todos os pacientes (como uma visão IMUTÁVEL)
    // Collections.unmodifiableList = impede que quem recebe consiga fazer add/remove nessa lista
    public List<Paciente> listarTodos() {
        return Collections.unmodifiableList(pacientes); // ainda reflete mudanças internas, mas não permite modificar de fora
    }

    // Busca um paciente pelo CPF (varre a lista em memória)
    public Optional<Paciente> buscarPorCpf(String cpf) {
        if (cpf == null) return Optional.empty(); // defesa simples
        return pacientes.stream()
                .filter(p -> p.getCpf().equals(cpf)) // compara CPF
                .findFirst();                        // devolve o primeiro que bater (ou Optional.empty)
    }
}