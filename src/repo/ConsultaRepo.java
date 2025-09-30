package repo;

import model.Consulta;
import java.util.*;
import java.io.IOException;

public class ConsultaRepo {

    private final String caminhoArquivo;

    public ConsultaRepo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    // Salva TODAS as consultas no CSV (sobrescreve)
    public void salvarTodos(List<Consulta> consultas) throws IOException {
        List<String> linhas = new ArrayList<>();
        // cabeçalho (ajuda a inspecionar depois)
        linhas.add("id;cpfPaciente;crmMedico;dataHoraISO;local;status;diagnostico;prescricao;precoFinal");

        for (Consulta c : consultas) {
            String diag = c.getDiagnostico() == null ? "" : c.getDiagnostico();
            String pres = c.getPrescricao() == null ? "" : c.getPrescricao();

            linhas.add(String.join(";",
                c.getId(),
                c.getPaciente().getCpf(),
                c.getMedico().getCrm(),
                c.getDataHora().toString(),       // ISO-8601
                c.getLocal(),
                c.getStatus().name(),             // AGENDADA/CONCLUIDA/CANCELADA
                diag,
                pres,
                String.valueOf(c.getPrecoFinal())
            ));
        }

        CSVUtil.escreverLinhas(caminhoArquivo, linhas);
    }
}
