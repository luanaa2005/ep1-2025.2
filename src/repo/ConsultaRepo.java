// src/repo/ConsultaRepo.java
package repo;

import model.*;
import java.util.*;
import java.io.IOException;
import java.time.LocalDateTime;

public class ConsultaRepo {
    private final String caminhoArquivo;      // ex.: "data/consultas.csv"
    private final PacienteRepo pacienteRepo;  // para buscar Paciente por CPF
    private final MedicoRepo   medicoRepo;    // para buscar Médico por CRM

    public ConsultaRepo(String caminhoArquivo, PacienteRepo pacRepo, MedicoRepo medRepo) {
        this.caminhoArquivo = caminhoArquivo;
        this.pacienteRepo   = pacRepo;
        this.medicoRepo     = medRepo;
    }

    // SALVAR: grava todas as consultas no CSV
    // Formato combinado:
    // id;cpfPaciente;crmMedico;dataHoraISO;local;status;diagnostico;prescricao;precoFinal
    public void salvarTodos(List<Consulta> consultas) throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;cpfPaciente;crmMedico;dataHoraISO;local;status;diagnostico;prescricao;precoFinal");
        for (Consulta c : consultas) {
            linhas.add(CSVUtil.juntarCampos(List.of(
                c.getId(),
                c.getPaciente().getCpf(),
                c.getMedico().getCrm(),
                c.getDataHora().toString(),
                c.getLocal(),
                c.getStatus().name(),
                c.getDiagnostico() == null ? "" : c.getDiagnostico(),
                c.getPrescricao()  == null ? "" : c.getPrescricao(),
                String.valueOf(c.getPrecoFinal())
            )));
        }
        CSVUtil.escreverLinhas(caminhoArquivo, linhas);
    }

    // CARREGAR: lê do CSV e reconstrói objetos Consulta
    // Observação: para simplificar, ignoramos o "id" salvo e
    // criamos a Consulta com um novo id. Mantemos o STATUS chamando
    // concluir()/cancelar() conforme o arquivo.
    public List<Consulta> carregarTodos() throws IOException {
        List<String> linhas = CSVUtil.lerLinhas(caminhoArquivo);
        List<Consulta> resultado = new ArrayList<>();
        if (linhas.isEmpty()) return resultado;

        int i = linhas.get(0).startsWith("id;") ? 1 : 0;
        for (; i < linhas.size(); i++) {
            String linha = linhas.get(i).trim();
            if (linha.isEmpty()) continue;

            List<String> cols = CSVUtil.dividirLinha(linha);
            if (cols.size() < 9) continue; // precisa ter todas as 9 colunas

            // String idIgnorado = cols.get(0);          // vamos ignorar o id salvo
            String cpf = cols.get(1);
            String crm = cols.get(2);
            String dataHoraIso = cols.get(3);
            String local = cols.get(4);
            String statusTxt = cols.get(5);
            String diag = cols.get(6);
            String presc = cols.get(7);
            String precoTxt = cols.get(8);

            // buscar paciente / médico
            Optional<Paciente> op = pacienteRepo.buscarPorCpf(cpf);
            Optional<Medico>   om = medicoRepo.buscarPorCrm(crm);
            if (op.isEmpty() || om.isEmpty()) {
                // se não achar paciente/médico, pula a linha (evita NPE)
                continue;
            }

            LocalDateTime dh;
            try { dh = LocalDateTime.parse(dataHoraIso); }
            catch (Exception e) { continue; }

            double preco;
            try { preco = Double.parseDouble(precoTxt.replace(',', '.')); }
            catch (Exception e) { continue; }

            // cria consulta AGENDADA
            Consulta c = new Consulta(op.get(), om.get(), dh, local, preco);

            // ajusta status conforme CSV
            StatusConsulta st;
            try { st = StatusConsulta.fromString(statusTxt); }
            catch (Exception e) { st = StatusConsulta.AGENDADA; }

            if (st == StatusConsulta.CONCLUIDA) {
                c.concluir(diag, presc);
            } else if (st == StatusConsulta.CANCELADA) {
                c.cancelar();
            }
            // (Se st == AGENDADA, deixa como está)

            resultado.add(c);
        }
        return resultado;
    }
}
