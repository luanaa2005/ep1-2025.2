# Orientação a objetos

Neste arquivo será documentada toda a construção do trabalho proposto, a fim de registrar o aprendizado e compreensão da matéria de orientação a objetos.

## Organização do trabalho

Foi necessário bastante tempo para entender os conceitos teóricos de POO para depois colocar em prática. O primeiro passo foi a elaboração de um cronograma de 7 dias (menos tempo para a execução para dar tempo de resolver futuros problemas ou até mesmo assimilar o conteúdo). Cada dia possui tarefas específicas para a construção do sistema aos poucos.

## Primeiros passos

O primeiro passo foi fazer as pastas e os arquivos.

- Estrutura e Função de Cada Pasta: 
    
    - /src: Modela o mundo real (entidades e enums). Aqui ficam as classes que representam os objetos principais do hospital: Pessoa, Paciente, Medico, Consulta, Internacao, PlanoSaude, etc. Enums como Especialidade e StatusConsulta.São “dados + comportamentos” (encapsulados).
    - /service: Regras de negócio (a lógica do hospital). Aqui entram os serviços que usam os modelos para executar ações: AgendamentoService: agenda, conclui, cancela consultas.InternacaoService: internar, dar alta, cancelar internações.RelatorioService: gera relatórios com filtros, estatísticas. É onde você coloca as regras (ex: “não pode marcar duas consultas no mesmo horário com o mesmo médico”).
    - /repo: Persistência de dados (arquivos). Aqui ficam as classes que salvam e carregam informações do sistema em arquivos .csv ou .txt: PacienteRepo, MedicoRepo, ConsultaRepo, InternacaoRepo. CSVUtil: métodos utilitários para ler e escrever CSV. Ou seja, quando você fecha e abre o programa, o que está no repo é carregado de volta.
    - /view: Interface com o usuário (menu no terminal). Aqui ficam as classes que cuidam de mostrar menus e receber opções digitadas. Menu.java é o controlador principal: mostra opções, chama os services. É a parte que conversa com o usuário, mas não tem regra de negócio nem manipula arquivos diretamente.
    - /util: Ferramentas auxiliares. Funções que podem ser usadas em várias partes do sistema. Exemplos: formatação de datas, validações de CPF, helpers de string. Tudo que não pertence a uma entidade nem a um serviço específico, mas ajuda o sistema.
    - Main.java(na raiz): Ponto de entrada do programa.Tem o public static void main(String[] args). Apenas chama o menu inicial (new Menu().iniciar()). Não contém regra de negócio — só inicia o sistema.

Resumindo:

- model = “as peças do hospital”

- service = “as regras do hospital”

- repo = “o arquivo onde guardamos tudo”

- view = “o que o usuário enxerga e digita”

- util = “caixa de ferramentas”

- Main.java = “botão ligar/desligar do programa”

Após a criação dessas pastas, o próximo passo foi criar as classes dentro de src/model.