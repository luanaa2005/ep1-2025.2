// persistir em CSV serve para guardar dados em arquivos de texto
// dessa forma, quando o programa fechar, os dados não somem.

package repo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * CSVUtil
 *
 * Funções utilitárias para trabalhar com arquivos CSV usando
 * ponto-e-vírgula (;) como separador.
 *
 * Por que ";"?
 * - Evita conflito com vírgula usada como separador decimal (pt-BR).
 * - Dispensa aspas e parsing complexo.
 *
 * Convenções do projeto:
 * - Cada arquivo CSV terá uma linha por registro.
 * - Colunas separadas por ";" (ponto-e-vírgula).
 * - Podemos ter cabeçalho (ex.: "cpf;nome;idade;plano").
 */
public class CSVUtil {

    /**
     * Lê todas as linhas de um arquivo de texto (UTF-8).
     *
     * @param caminhoArquivo caminho relativo ou absoluto (ex.: "data/pacientes.csv")
     * @return lista de linhas; se o arquivo não existir, retorna lista vazia
     * @throws IOException em erros de I/O (permissão, disco cheio, etc.)
     */
    public static List<String> lerLinhas(String caminhoArquivo) throws IOException {
        // Constrói um Path a partir da String (independente de SO)
        Path path = Paths.get(caminhoArquivo);

        // Se o arquivo ainda não existe, devolve vazio (útil na 1ª execução)
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        // Lê todas as linhas em UTF-8 e retorna
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    /**
     * Escreve TODAS as linhas no arquivo (UTF-8), sobrescrevendo o conteúdo anterior.
     * Cria a(s) pasta(s) pai automaticamente se não existirem (ex.: "data/").
     *
     * @param caminhoArquivo caminho do arquivo (ex.: "data/medicos.csv")
     * @param linhas conteúdo a ser gravado, uma String por linha
     * @throws IOException em erros de I/O
     */
    public static void escreverLinhas(String caminhoArquivo, List<String> linhas) throws IOException {
        Path path = Paths.get(caminhoArquivo);

        // Garante que a pasta pai exista (ex.: cria "data/" se não existir)
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        // Escreve as linhas em UTF-8:
        // - CREATE: cria o arquivo se não existir
        // - TRUNCATE_EXISTING: apaga conteúdo anterior
        Files.write(
            path,
            linhas,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /**
     * Divide uma linha CSV em campos usando ';' como separador.
     * O sufixo "-1" no split preserva campos vazios no final.
     *
     * Ex.: "a;b;c;" -> ["a","b","c",""]
     *
     * @param linha linha completa do CSV
     * @return lista de campos (já recortados)
     */
    public static List<String> dividirLinha(String linha) {
        // Converte o array retornado por split em List imutável
        // (se quiser mutável: new ArrayList<>(Arrays.asList(...)))
        return Arrays.asList(linha.split(";", -1));
    }

    /**
     * Junta campos em uma linha CSV usando ';' como separador.
     * Não usamos aspas neste projeto (por isso: evite ';' dentro dos campos).
     *
     * @param campos lista de valores dos campos
     * @return única String representando a linha do CSV
     */
    public static String juntarCampos(List<String> campos) {
        return String.join(";", campos);
    }

    /* -------------------------------------------------------------
       COMO USAR: 

       // Escrever:
       List<String> linhas = new ArrayList<>();
       linhas.add("cpf;nome;idade;plano"); // cabeçalho (opcional)
       linhas.add(CSVUtil.juntarCampos(List.of("12345678900","Luana","21","BASICO")));
       CSVUtil.escreverLinhas("data/pacientes.csv", linhas);

       // Ler:
       List<String> lidas = CSVUtil.lerLinhas("data/pacientes.csv");
       // Pular cabeçalho se estiver presente:
       int i = (lidas.size() > 0 && lidas.get(0).startsWith("cpf;")) ? 1 : 0;
       for (; i < lidas.size(); i++) {
           List<String> cols = CSVUtil.dividirLinha(lidas.get(i));
           // cols.get(0) = cpf, cols.get(1) = nome, ...
       }
       ------------------------------------------------------------- */
}
