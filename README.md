# 🏥 Trabalho Prático – Sistema de Gerenciamento Hospitalar  

### 🎯 Objetivo  
Implementar um *Sistema de Gerenciamento Hospitalar* em *Java, aplicando conceitos avançados de **Programação Orientada a Objetos (POO), com foco em **herança, polimorfismo, encapsulamento, persistência de dados* e *regras de negócio mais complexas*.  

---
## Descrição do Projeto

Desenvolvimento de um sistema de gerenciamento hospitalar utilizando os conceitos de orientação a objetos (herança, polimorfismo e encapsulamento) e persistência de dados em arquivos.

## Dados do Aluno

- **Nome completo:** Luana Carvalho de Almeida
- **Matrícula:** 242004840
- **Curso:** Engenharias
- **Turma:** [Preencher aqui]

---

## Instruções para Compilação e Execução

1. **Compilação:**  
   [Descrever aqui como compilar o projeto. Exemplo: `javac Main.java` ou o script usado]

2. **Execução:**  
   JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8" java -cp out MainMenu


3. **Estrutura de Pastas:**  
   .
   ├─ src/
   │  ├─ model/          # Entidades (Paciente, Medico, Consulta, Internacao, Planos…)
   │  ├─ repo/           # Repositórios CSV (PacienteRepo, MedicoRepo, ConsultaRepo, InternacaoRepo)
   │  └─ service/        # Regras de negócio (AgendamentoService, InternacaoService, RelatorioService)
   ├─ data/              # Arquivos .csv persistidos em runtime
   ├─ MainMenu.java      # Ponto de entrada (menu em linha de comando)
   └─ out/               # Saída da compilação (gerada pelo comando acima)


3. **Versão do JAVA utilizada:**  
   [Descrever aqui como versão do JAVA utilizada no projeto. Sugestão: `java 21`]

---

## Vídeo de Demonstração

- [Inserir o link para o vídeo no YouTube/Drive aqui]

---

## Prints da Execução

1. Menu Principal:  
   ![Inserir Print 1](caminho/do/print1.png)

2. Cadastro de Médico:  
   ![Inserir Print 2](caminho/do/print2.png)

3. Relatório de ?:  
   ![Inserir Print 3](caminho/do/print3.png)

---

---

## Observações (Extras ou Dificuldades)

- [Espaço para o aluno comentar qualquer funcionalidade extra que implementou, dificuldades enfrentadas, ou considerações importantes.]

---

## Contato

- luana.comfort@gmail.com

---

### 🖥️ Descrição do Sistema  

O sistema deve simular o funcionamento de um hospital com cadastro de *pacientes, médicos, especialidades, consultas e internações*.  

1. *Cadastro de Pacientes*  
   - Pacientes comuns e pacientes especiais (ex: com plano de saúde).  
   - Cada paciente deve ter: nome, CPF, idade, histórico de consultas e internações.  

2. *Cadastro de Médicos*  
   - Médicos podem ter especialidades (ex: cardiologia, pediatria, ortopedia).  
   - Cada médico deve ter: nome, CRM, especialidade, custo da consulta e agenda de horários.  

3. *Agendamento de Consultas*  
   - Um paciente pode agendar uma consulta com um médico disponível.  
   - Consultas devem registrar: paciente, médico, data/hora, local, status (agendada, concluída, cancelada).  
   - Pacientes especiais (plano de saúde) podem ter *vantagens*, como desconto.  
   - Duas consultas não podem estar agendadas com o mesmo médico na mesma hora, ou no mesmo local e hora

4. *Consultas e Diagnósticos*  
   - Ao concluir uma consulta, o médico pode registrar *diagnóstico* e/ou *prescrição de medicamentos*.  
   - Cada consulta deve ser registrada no *histórico do paciente*.  

5. *Internações*  
   - Pacientes podem ser internados.  
   - Registrar: paciente, médico responsável, data de entrada, data de saída (se já liberado), quarto e custo da internação.  
   - Deve existir controle de *ocupação dos quartos* (não permitir duas internações no mesmo quarto simultaneamente).  
   - Internações devem poder ser canceladas, quando isso ocorrer, o sistema deve ser atualizado automaticamente.

6. *Planos de saúde*    
   -  Planos de saude podem ser cadastrados.
   -  Cada plano pode oferecer *descontos* para *especializações* diferentes, com possibilidade de descontos variados.
   -  Um paciente que tenha o plano de saúde deve ter o desconto aplicado.
   -  Deve existir a possibilidade de um plano *especial* que torna internação de menos de uma semana de duração gratuita.
   -  Pacientes com 60+ anos de idade devem ter descontos diferentes.

7. *Relatórios*  
   - Pacientes cadastrados (com histórico de consultas e internações).  
   - Médicos cadastrados (com agenda e número de consultas realizadas).  
   - Consultas futuras e passadas (com filtros por paciente, médico ou especialidade).  
   - Pacientes internados no momento (com tempo de internação).  
   - Estatísticas gerais (ex: médico que mais atendeu, especialidade mais procurada).  
   - Quantidade de pessoas em um determinado plano de saúde e quanto aquele plano *economizou* das pessoas que o usam.  


---

### ⚙️ Requisitos Técnicos  
- O sistema deve ser implementado em *Java*.  
- Interface via *terminal (linha de comando)*.  
- Os dados devem ser persistidos em *arquivos* (.txt ou .csv).  
- Deve existir *menu interativo*, permitindo navegar entre as opções principais.  

---

### 📊 Critérios de Avaliação  

1. *Modos da Aplicação (1,5)* → Cadastro de pacientes, médicos, planos de saúde, consultas e internações.  
2. *Armazenamento em arquivo (1,0)* → Dados persistidos corretamente, leitura e escrita funcional.  
3. *Herança (1,0)* → Ex.: Paciente e PacienteEspecial, Consulta e ConsultaEspecial, Médico e subclasses por especialidade.  
4. *Polimorfismo (1,0)* → Ex.: regras diferentes para agendamento, preços de consultas.
5. *Encapsulamento (1,0)* → Atributos privados, getters e setters adequados.  
6. *Modelagem (1,0)* → Estrutura de classes clara, bem planejada e com relacionamentos consistentes.  
7. *Execução (0,5)* → Sistema compila, roda sem erros e possui menus funcionais.  
8. *Qualidade do Código (1,0)* → Código limpo, organizado, nomes adequados e boas práticas.  
9. *Repositório (1,0)* → Uso adequado de versionamento, commits frequentes com mensagens claras.  
10. *README (1,0)* → Vídeo curto (máx. 5 min) demonstrando as funcionalidades + prints de execução + explicação da modelagem.  

🔹 *Total = 10 pontos*  
🔹 *Pontuação extra (até 1,5)* → Melhorias relevantes, como:  
- Sistema de triagem automática com fila de prioridade.  
- Estatísticas avançadas (tempo médio de internação, taxa de ocupação por especialidade).  
- Exportação de relatórios em formato .csv ou .pdf.  
- Implementação de testes unitários para classes principais.  
- Menu visual.


## 📅 Cronograma de Desenvolvimento

### Dia 1 – Estrutura e Classes Base

✅ Criar pastas: /model, /service, /repo, /view, /util 

✅ Implementar Pessoa (abstract)

✅ Implementar Paciente

✅ Implementar Medico

✅ Criar enums Especialidade e StatusConsulta

✅ Definir interface PlanoSaude


### Dia 2 – Persistência (CSV)

✅ Implementar PacienteRepo (cadastrar, listar, salvar/carregar CSV)

✅ Implementar MedicoRepo

✅ Testar gravação/carregamento em pacientes.csv e medicos.csv

### Dia 3 – Consultas (Agendamento - Parte 1)
✅ Criar classe Consulta

✅ Criar AgendamentoService.agendar()

✅ Validar disponibilidade do médico

✅ Validar conflito de horário/local

### Dia 4 – Consultas (Parte 2)
✅ Implementar concluir consulta (diagnóstico, prescrição)

✅ Implementar cancelar consulta (liberar agenda)

✅ Persistir em consultas.csv

### Dia 5 – Internações
✅ Criar classe Internacao

✅ Criar InternacaoService (internar, alta, cancelar)

✅ Implementar regra de quarto ocupado (não permitir duplicado)

✅ Persistência em internacoes.csv

### Dia 6 – Planos de Saúde e Regras Especiais
✅ Implementar PlanoBasico

✅ Implementar PlanoPlus

✅ Implementar PlanoEspecial (internação <7 dias gratuita, desconto para 60+)

✅ Integrar desconto no cálculo de consultas e internações

### Dia 7 – Relatórios e Revisão Final
☐ Relatório de pacientes (com histórico)

☐ Relatório de médicos (agenda e nº consultas)

☐ Relatório de consultas futuras/passadas (com filtros)

☐ Relatório de internados no momento (tempo de internação)

☐ Estatísticas (médico que mais atendeu, especialidade mais procurada, economia por plano)

☐ Revisar menu principal e testes manuais