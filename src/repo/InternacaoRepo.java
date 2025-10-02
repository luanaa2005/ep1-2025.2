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
}
