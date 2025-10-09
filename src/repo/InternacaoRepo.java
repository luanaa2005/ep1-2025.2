package repo;

import model.Internacao;
import java.util.*;
import java.io.IOException;

public class InternacaoRepo {

    private final String caminhoArquivo;

    public InternacaoRepo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;   
    }

    
    public void salvarTodos(List<Internacao> internacoes) throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;cpfPaciente;crmMedico;quarto;entradaISO;saidaISO;custoBaseDia;custoTotal"); 

        for (Internacao i : internacoes) {        
            String saidaISO = (i.getSaida() == null) ? "" : i.getSaida().toString();

           
            String linha = String.join(";",
                i.getId(),
                i.getPaciente().getCpf(),
                i.getMedicoResponsavel().getCrm(),
                i.getQuarto(),
                i.getEntrada().toString(),
                saidaISO,
                String.valueOf(i.getCustoBaseDia()),
                String.valueOf(i.calcularCustoTotal())
            );
            linhas.add(linha);
        }

        CSVUtil.escreverLinhas(caminhoArquivo, linhas); // CSVUtil está no mesmo package repo; não precisa importar.
    }

    // Top 1 médico por nº de consultas CONCLUÍDAS.
    // Retorna Optional com um “resultado simples” (crm, nome, quantidade).
    public Optional<TopMedico> medicoQueMaisAtendeu() throws Exception {
        // TODO: carregar consultas; filtrar CONCLUIDA; agrupar por CRM; pegar o maior;
        // depois, com o CRM, buscar o médico no MedicoRepo para pegar o nome.
        return Optional.empty();
    }

    // “DTO” simples só pra devolver info organizada (Java 17 suporta record).
    public static record TopMedico(String crm, String nome, long quantidade) {}


    // TODO: carregar do CSV de verdade.
    // Por enquanto devolvemos lista vazia só p/ RelatorioService compilar.
    public List<Internacao> carregarTodos() {
        return new ArrayList<>();
    }

}
