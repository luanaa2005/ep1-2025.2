// MedicoRepo.java — repositório para GUARDAR/CARREGAR médicos em CSV
// Formato combinado: crm;nome;cpf;idade;especialidade;custoBase
// (separador ';' para evitar conflito com vírgula decimal em pt-BR)

package repo;

import model.Especialidade; // necessário para converter texto -> enum
import model.Medico;        // necessário para instanciar Médicos

import java.util.*;         // List, ArrayList, Collections, Optional, Stream
import java.io.IOException; // ; <- ATENÇÃO: faltava ponto-e-vírgula no seu import

public class MedicoRepo {

    // 'private final' = a REFERÊNCIA não muda depois do construtor (boa prática)
    private final String caminhoArquivo;  // ex.: "data/medicos.csv"

    // Lista que mantém todos os médicos em memória enquanto o programa roda
    private final List<Medico> medicos;

    // Construtor recebe o caminho do CSV e já carrega os dados
    // 'throws IOException' = declara que PODE lançar erro de arquivo; quem chama decide tratar
    public MedicoRepo(String caminhoArquivo) throws IOException {
        this.caminhoArquivo = caminhoArquivo; // guarda o caminho para usar em ler/gravar
        this.medicos = new ArrayList<>();     // começa com lista vazia
        carregarDoArquivo();                  // carrega o CSV (se existir) para a lista
    }

    // Carrega médicos do arquivo CSV para a lista em memória
    // Regra do formato: crm;nome;cpf;idade;especialidade;custoBase
    private void carregarDoArquivo() throws IOException {
        List<String> linhas = CSVUtil.lerLinhas(caminhoArquivo); // lê todas as linhas (ou [] se o arquivo não existir)
        medicos.clear();                                         // limpa a lista para evitar duplicar dados
        if (linhas.isEmpty()) return;                            // nada para carregar

        // Se a primeira linha for um cabeçalho ("crm;..."), pulamos ela
        int i = linhas.get(0).startsWith("crm;") ? 1 : 0;

        // Percorre cada linha útil do arquivo
        for (; i < linhas.size(); i++) {
            String linha = linhas.get(i).trim(); // remove espaços extras
            if (linha.isEmpty()) continue;       // ignora linhas em branco

            // Divide pelos ';' PRESERVANDO campos vazios (ex.: "a;b;c;" -> ["a","b","c",""])
            List<String> cols = CSVUtil.dividirLinha(linha);

            // Conferimos se há TODAS as 6 colunas necessárias
            if (cols.size() < 6) continue; // se faltar dado essencial, pula a linha

            // Extrai colunas na ordem combinada
            String crm      = cols.get(0); // 0 = CRM
            String nome     = cols.get(1); // 1 = Nome
            String cpf      = cols.get(2); // 2 = CPF
            int idade       = Integer.parseInt(cols.get(3)); // 3 = Idade (string -> int)

            // 4 = Especialidade (pode vir "CARDIOLOGIA" ou "Cardiologia")
            String espTxt   = cols.get(4);
            // usamos fromString para aceitar tanto o name() quanto o rótulo amigável
            Especialidade esp;
            try {
                esp = Especialidade.fromString(espTxt);
            } catch (IllegalArgumentException e) {
                // se o valor estiver inválido, ignoramos esta linha (poderia também logar/avisar)
                continue;
            }

            // 5 = custoBase (ATENÇÃO: decimal deve usar PONTO; se vier com vírgula, trocamos)
            String custoTxt = cols.get(5);
            double custoBase = Double.parseDouble(custoTxt.replace(',', '.'));

            // Cria o médico com o CONSTRUTOR COMPLETO que modelamos:
            // Medico(String nome, String cpf, int idade, String crm, Especialidade esp, double custoBaseConsulta)
            Medico medico = new Medico(nome, cpf, idade, crm, esp, custoBase);

            // Adiciona na lista em memória
            medicos.add(medico);
        }
    }

    // Salva a lista de médicos em memória no arquivo CSV
    // Regra do formato: crm;nome;cpf;idade;especialidade;custoBase
    private void salvarNoArquivo() throws IOException {
        List<String> linhas = new ArrayList<>();

        // Adiciona o cabeçalho (facilita leitura/depuração manual do arquivo)
        linhas.add("crm;nome;cpf;idade;especialidade;custoBase");

        // Adiciona cada médico como uma linha no formato correto
        for (Medico m : medicos) {
            // Especialidade: preferimos salvar com name() (ex.: "CARDIOLOGIA") para ler fácil com valueOf/ fromString
            linhas.add(CSVUtil.juntarCampos(List.of(
                m.getCrm(),                                // crm
                m.getNome(),                               // nome
                m.getCpf(),                                // cpf
                String.valueOf(m.getIdade()),              // idade
                m.getEspecialidade().name(),               // especialidade (NAME, não rótulo)
                String.valueOf(m.getCustoBaseConsulta())   // custoBase (com ponto)
            )));
        }

        // Usa o CSVUtil para escrever todas as linhas no arquivo (cria 'data/' se faltar)
        CSVUtil.escreverLinhas(caminhoArquivo, linhas);
    }

    // Cadastra um novo médico (se o CRM não existir); retorna true se cadastrado, false se CRM já existe
    public boolean cadastrarMedico(Medico medico) throws IOException {
        if (medico == null) throw new IllegalArgumentException("Médico nulo");

        // Verifica se já existe médico com esse CRM (ignorando maiúsculas/minúsculas)
        boolean existe = medicos.stream()
                .anyMatch(m -> m.getCrm().equalsIgnoreCase(medico.getCrm()));
        if (existe) return false; // já existe, não cadastra

        // Adiciona na lista e salva no arquivo
        medicos.add(medico);
        salvarNoArquivo();
        return true; // cadastrado com sucesso
    }

    // Retorna uma lista imutável (read-only) com todos os médicos
    public List<Medico> listarMedicos() {
        return Collections.unmodifiableList(medicos);
    }

    // Busca um médico pelo CRM (ignora maiúsculas/minúsculas); retorna Optional vazio se não achar
    public Optional<Medico> buscarPorCrm(String crm) {
        if (crm == null) return Optional.empty();
        return medicos.stream()
            .filter(m -> m.getCrm().equalsIgnoreCase(crm))
            .findFirst();
    }
}
