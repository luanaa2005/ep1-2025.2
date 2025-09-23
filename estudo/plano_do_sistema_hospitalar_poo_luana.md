# 🏥 Sistema de Gerenciamento Hospitalar – Plano de Implementação (Java/POO)


## Conceitos-chave (tradução rápida)
- **Encapsulamento:** atributos `private` + `getters/setters` + métodos que protegem invariantes (ex.: impedir agendar consulta com conflito).  
- **Herança:** `PacienteEspecial extends Paciente`; `Consulta` (base) e, se desejar, `ConsultaConvencional` vs `ConsultaPlano`.  
- **Polimorfismo:** cálculo de **preço** e **descontos** variando por tipo de paciente/plano/especialidade usando **interface/strategy** (ex.: `CalculadoraDescontos`).  
- **Persistência:** salvar/ler dados em `.csv`/`.txt` (ex.: `pacientes.csv`) através de classes de **repositório**.

---

## Escopo do Sistema (regras de negócio)
1. **Pacientes**  
   - Comuns e Especiais (têm Plano).  
   - Campos: `nome`, `cpf`, `idade`. Histórico: consultas e internações.
2. **Médicos**  
   - Campos: `nome`, `crm`, `especialidade`, `custoBaseConsulta`, `agenda` (datas/horas livres/ocupadas).  
3. **Consultas**  
   - `paciente`, `medico`, `dataHora`, `local`, `status {AGENDADA, CONCLUIDA, CANCELADA}`, `diagnostico`, `prescricao`.  
   - **Conflitos proibidos:** mesmo médico **na mesma hora**; e **mesmo local e hora** (independente do médico).  
   - Paciente com plano: **desconto** por especialidade; **60+ anos**: descontos diferenciados.  
4. **Internações**  
   - `paciente`, `medicoResponsavel`, `dataEntrada`, `dataSaida?`, `quarto`, `custo`.  
   - Controle de ocupação de `quarto` por intervalo de tempo.  
   - Cancelamento deve liberar quarto/atualizar estado.  
   - Plano especial: internação **< 7 dias gratuita**.  
5. **Planos de Saúde**  
   - Tabelar **descontos por especialidade**; tratar **idosos (60+)**.  
   - **Relatório**: quantas pessoas usam o plano e **quanto economizaram**.
6. **Relatórios** (mínimos)  
   - Pacientes (com histórico), Médicos (agenda + nº consultas), Consultas futuras/passadas (filtros), Pacientes internados agora (tempo de internação), Estatísticas (médico que mais atendeu, especialidade mais procurada), Planos (adesão + economia total).

---

## Modelagem (UML simplificada – base)
```
Pessoa (abstract)
  - nome:String
  - cpf:String
  - idade:int

Paciente extends Pessoa
  - plano: PlanoSaude? (nullable)
  + getHistoricoConsultas(): List<Consulta>
  + getHistoricoInternacoes(): List<Internacao>

Medico extends Pessoa
  - crm:String
  - especialidade: Especialidade (enum)
  - custoBaseConsulta: double
  - agenda: Agenda
  + disponivel(LocalDateTime, String local): boolean

PlanoSaude (interface)
  + double aplicarDesconto(Especialidade esp, int idade, double precoBase)
  + boolean internaçãoGratuitaAte7Dias()

PlanoBasico implements PlanoSaude
PlanoPlus implements PlanoSaude (ex.: descontos melhores)
PlanoSenior implements PlanoSaude (foco 60+)

Consulta
  - id:String
  - paciente:Paciente
  - medico:Medico
  - dataHora:LocalDateTime
  - local:String
  - status: StatusConsulta (enum)
  - diagnostico:String
  - prescricao:String
  + double calcularPreco()

Internacao
  - id:String
  - paciente:Paciente
  - medicoResponsavel:Medico
  - dataEntrada:LocalDateTime
  - dataSaida:LocalDateTime?
  - quarto:String
  - custoBaseDia: double
  + double calcularCustoTotal()

Agenda
  - reservas: Map<LocalDateTime, String local>
  + reservar(LocalDateTime, String): boolean
  + liberar(LocalDateTime)

Repositorios (persistência)
  PacienteRepo, MedicoRepo, ConsultaRepo, InternacaoRepo, PlanoRepo
  -> salvarCSV(), carregarCSV(), buscarPorId(), etc.

Serviços (regras)
  AgendamentoService
  InternacaoService
  RelatorioService
  PlanoService
```

### Observações de design
- **Encapsule** listas internas (de histórico/agenda) retornando cópias imutáveis ou só métodos de consulta.
- **Polimorfismo** pelo `PlanoSaude`: cada plano calcula desconto de forma diferente.
- **Validações** nos *services* (ex.: proibir conflito de horário/local; verificar quarto livre).  
- **IDs**: gere `UUID` para consultas/internações.

---

## Estrutura de Pastas (sugestão Maven-free, simples)
```
/src
  /model        (entidades e enums)
  /service      (regras de negócio)
  /repo         (persistência CSV)
  /view         (menus CLI)
  /util         (helpers: datas, validação, CSV)
Main.java       (ponto de entrada + loop de menu)
/data           (arquivos .csv gerados)
README.md
```

---

## Persistência em Arquivos (CSV) – formatos sugeridos
- `pacientes.csv`: `cpf;nome;idade;tipoPlano`  
- `medicos.csv`: `crm;nome;cpf;idade;especialidade;custoBase`  
- `consultas.csv`: `id;cpfPaciente;crmMedico;dataHoraISO;local;status;diagnostico;prescricao;precoFinal`  
- `internacoes.csv`: `id;cpfPaciente;crmMedico;dataEntradaISO;dataSaidaISO;quarto;custoTotal`  
- `planos.csv`: `nomePlano;descontosPorEspecialidadeJSON;gratuitaAte7Dias:boolean;regrasIdosoJSON`

> **Dica:** Use `LocalDateTime.parse(isoString)` / `toString()` para datas; padronize `;` como separador; escape `,;` em textos.

---

## Fluxo de Menu (CLI) – roteiro
```
[1] Pacientes  -> cadastrar | listar | detalhar | vincular plano
[2] Médicos    -> cadastrar | listar | agenda | custo base
[3] Consultas  -> agendar | concluir | cancelar | listar (filtros)
[4] Internações-> internar | alta | cancelar | listar atuais
[5] Planos     -> cadastrar | listar | estatísticas
[6] Relatórios -> pacientes | médicos | consultas | internados | estatísticas
[0] Sair       -> salvar e encerrar
```

**Agendar consulta (núcleo):**
1) escolher paciente e médico  
2) informar `dataHora` e `local`  
3) `AgendamentoService` valida:  
   - médico livre nesse horário?  
   - não existe consulta nesse `local` + `dataHora`?  
   - aplica desconto via `paciente.getPlano().aplicarDesconto(...)` (se houver)  
4) grava consulta em `consultas.csv` e bloqueia agenda do médico.

**Internação:**
- Verificar quarto livre no intervalo; se `plano.internaçãoGratuitaAte7Dias()` e duração < 7 dias → custo = 0.

---

## Esqueleto de Assinaturas (para você completar)
> _Só assinaturas (sem implementação completa), para guiar seu código._

```java
// model/Pessoa.java
public abstract class Pessoa {
  private String nome, cpf; private int idade;
  // getters/setters + validações básicas
}

// model/Paciente.java
public class Paciente extends Pessoa {
  private PlanoSaude plano; // opcional
  // listas de ids: consultas, internações (ou busca por repositório)
}

// model/Medico.java
public class Medico extends Pessoa {
  private String crm; private Especialidade especialidade; private double custoBaseConsulta;
  private Agenda agenda;
  public boolean disponivel(LocalDateTime dh, String local) { /* ... */ }
}

// model/PlanoSaude.java
public interface PlanoSaude {
  double aplicarDesconto(Especialidade esp, int idade, double precoBase);
  boolean internacaoGratuitaAte7Dias();
}

// model/PlanoPlus.java (exemplo)
public class PlanoPlus implements PlanoSaude { /* ... */ }

// model/Consulta.java
public class Consulta {
  private String id, local; private Paciente paciente; private Medico medico;
  private LocalDateTime dataHora; private StatusConsulta status;
  private String diagnostico, prescricao; private double precoFinal;
  public double calcularPreco() { /* usa medico.custoBase + descontos */ }
}

// model/Internacao.java
public class Internacao {
  private String id, quarto; private Paciente paciente; private Medico medicoResponsavel;
  private LocalDateTime entrada, saida; private double custoBaseDia;
  public double calcularCustoTotal() { /* dias * custoBaseDia, considerar plano */ }
}

// service/AgendamentoService.java
public class AgendamentoService {
  public Consulta agendar(String cpfPac, String crmMed, LocalDateTime dh, String local) { /* valida e cria */ }
  public void concluir(String idConsulta, String diag, String presc) { /* ... */ }
  public void cancelar(String idConsulta) { /* libera agenda */ }
}

// service/InternacaoService.java
public class InternacaoService {
  public Internacao internar(String cpfPac, String crm, String quarto, LocalDateTime entrada) { /* ... */ }
  public void alta(String idInternacao, LocalDateTime saida) { /* ... */ }
  public void cancelar(String idInternacao) { /* liberar quarto */ }
}

// repo/CSVUtil.java -> lerLinha(), escreverLinha(), splitSeguro()
// repo/<Entidade>Repo.java -> carregar(), salvar(), buscar(), listar()

// view/Menu.java -> Scanner, laços, prints e chamadas aos services/repos
```

---

## Relatórios – como calcular
- **Consultas futuras/passadas:** filtrar por `dataHora` `isAfter(now)` / `isBefore(now)` e combinar filtros por paciente/médico/especialidade.  
- **Médico que mais atendeu:** `groupBy(medico).count().max()`.  
- **Especialidade mais procurada:** `groupBy(consulta.medico.especialidade)`.  
- **Internados no momento:** internações com `saida == null` (ou `saida > now`). Calcular **tempo** com `Duration.between(entrada, now)`.  
- **Economia por plano:** somar `(precoSemPlano - precoFinalComPlano)` nas consultas + redução em internações.

---

## Roteiro de Entrega (passo a passo enxuto)
**Dia 1:** Modelagem final (esta), criar pacotes, enums e entidades vazias + repos vazios.  
**Dia 2:** Repositórios CSV (carregar/salvar) para Paciente e Médico.  
**Dia 3:** Agenda do médico + `AgendamentoService.agendar()` com validações.  
**Dia 4:** Concluir/Cancelar consulta + persistência de Consultas.  
**Dia 5:** Internação (internar/alta/cancelar) + verificação de quarto.  
**Dia 6:** Planos (3 estratégias) + descontos (consulta e internação).  
**Dia 7:** Relatórios + README + prints + vídeo (≤5min).

---

## README – modelo
### Instruções de Compilação
```
javac --release 21 -d out $(find src -name "*.java")
```
### Execução
```
java -cp out Main
```
### Estrutura de Pastas
- ver seção 4 acima.

### Versão do Java
```
java -version
```
(esperado algo como `openjdk 21`)

### Vídeo de Demonstração
Link: _[colar YouTube/Drive]_  

### Prints pedidos
1. **Menu Principal** – após carregar dados.  
2. **Cadastro de Médico** – fluxo completo.  
3. **Relatório** – (ex.: consultas futuras filtradas por especialidade).

### Observações (extras/dificuldades)
- _Ex.: implementação de PlanoSenior; tratamento de datas; validações._

---

## 11) Como cada critério será atendido
- **Modos (1,5):** menus 1–6 cobrindo cadastros/consultas/internações/planos.  
- **Arquivos (1,0):** `*.csv` com repositórios e `salvar()` no sair.  
- **Herança (1,0):** `Pessoa`→`Paciente`/`Medico`; `PacienteEspecial` opcional.  
- **Polimorfismo (1,0):** `PlanoSaude` (estratégias de desconto).  
- **Encapsulamento (1,0):** atributos `private` + validações.  
- **Modelagem (1,0):** estrutura e serviços conforme seções 3–6.  
- **Execução (0,5):** `Main` com loop CLI robusto.  
- **Qualidade (1,0):** nomes claros, pacotes, exceções customizadas.  
- **Repositório (1,0):** commits pequenos e frequentes, mensagens claras.  
- **README (1,0):** vídeo + prints + explicação da modelagem.

---

## 12) Pontos extras (ideias simples)
- **Triagem com prioridade:** `Queue` priorizando idosos/emergência.  
- **Estatísticas avançadas:** tempo médio de internação por esp.; taxa de ocupação de quartos.  
- **Exportar relatórios:** gerar `.csv` em `/data/relatorios/`.  
- **Testes unitários:** `RelatorioService`, `AgendamentoService` (valida conflitos).  
- **Menu visual:** moldura, cores ANSI, confirmação por `S/N`.

---

## 13) Dicas finais
- Comece **pelas entidades e repositórios**; deixe interface bonita para o final.  
- Faça **validações nos services** (e lance exceções).  
- Salve **sempre** antes de sair; trate `IOException`.  
- Use `UUID.randomUUID().toString()` para ids.  
- Escreva `TODO:` no código onde faltar algo e registre nos commits.

> Quando quiser, posso transformar este plano em **stubs de código** (arquivos `.java` vazios com assinaturas) para você completar e já compilar.

