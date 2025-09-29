# Lista de Exercícios P1 - Orientação a Objetos (Java)

1. **Defina o que é Orientação a Objetos (OO)**  
   
   Programação Orientada a Objetos é uma forma de escrever programas pensando em objetos do mundo real.

   Esses obejtos  têm:

   - Atributos → características (ex: uma pessoa tem nome e idade).

    - Métodos → ações que ela pode fazer (ex: uma pessoa pode falar ou andar).

    O objetivo principal é deixar o código mais organizado, fácil de entender e de reaproveitar. Assim, em vez de repetir o mesmo código várias vezes, criam-se objetos que podem ser usados em diferentes partes do programa.


2. **Conceitos básicos**  
   Explique os seguintes conceitos:  

   a) Classe

    Representa um molde ou modelo que descreve características e comportamentos comuns a um conjunto de objetos.

    Exemplo: a classe Carro descreve que todo carro possui cor e modelo, além de ações como acelerar e frear.

   b) Objeto  

    É uma instância de uma classe, ou seja, algo concreto criado a partir do modelo. Cada objeto pode ter valores próprios para os atributos definidos na classe. 
    
    Exemplo: um objeto carro1 pode ser um Corolla azul, enquanto carro2 pode ser um Civic preto.

   c) Atributo  
   Representa as características ou propriedades de um objeto. São variáveis que armazenam informações específicas.
   
   Exemplo: em carro1, o atributo cor possui valor "azul" e o atributo modelo possui valor "Corolla".

   d) Método

   Representa as ações ou comportamentos que um objeto pode executar. São funções associadas a uma classe, responsáveis por manipular os atributos ou realizar operações.
   
   Exemplo: o método acelerar() altera a velocidade do carro.

3. **Conceito de classe e um objeto**  
   O que é uma classe e um objeto? Dê um exemplo simples em Java.

   - Classe: é um modelo que descreve as características (atributos) e comportamentos (métodos) de algo.
   
   - Objeto: é uma instância da classe, ou seja, algo concreto criado a partir desse modelo.

   ```java
    class Carro {
        String cor;
        String modelo;
    }

    public class Main {
        public static void main(String[] args) {
            Carro meuCarro = new Carro();
        }
    }


4. **Associação entre classes**  
   Explique o que é uma associação entre duas classes e cite um exemplo prático.

   Associação é um tipo de relacionamento entre duas classes onde uma faz uso da outra. Nesse caso, os objetos de uma classe estão ligados aos objetos de outra, mas cada classe continua existindo de forma independente. O objetivo é representar que uma entidade tem relação com outra, sem implicar dependência de vida (diferente de composição, por exemplo).

   Exemplo prático: Um sistema simples com as classes Carro e Motorista.

  - Um motorista dirige um carro.

  - Um carro pode ser dirigido por um motorista.


5. **Criação de classe simples**  
   Implemente uma classe `Apartamento` em Java, que tenha os atributos `area`, `quartos`, `andar`, `valorDeCompra`, `vagasDeGaragem` e `temVaranda`, e um método `exibirInfo()` que imprime esses dados no terminal.

   ``` java
    class Apartamento{
        double area;
        int quartos;
        int andar;
        double valorDeCompra;
        int vagasDeGaragem;
        boolean temVaranda;

        void exibirInfo(){
        System.out.println(area);
        System.out.println(quartos);
        System.out.prinln(andar);
        System.out.println(valorDeCompra);
        System.out.println(vagasDeGaragem);
        System.out.println(temVaranda);
        }
    }



6. **Herança**  
   Explique o conceito de herança em OO e implemente duas classes em Java que utilizem herança.

    Herança permite que uma classe (chamada subclasse ou classe filha) reaproveite atributos e métodos de outra classe (chamada superclasse ou classe pai).

    - A superclasse contém as características e comportamentos comuns.

    - A subclasse herda esses elementos e pode adicionar novos atributos/métodos ou sobrescrever os herdados.

Exemplo 1:

 ``` java

    class Carro{
        String cor;
        String marca;
        double valor;
    }

    class Ferrari812SuperFast extends Carro{
        void acelerar(){
            System.out.println("A Ferrari 812 Superfast está acelerando!");
        }
    }
```
Exemplo 2:

``` java
    class Animal {
        String nome;
        int idade;
        boolean vacinado;
        double peso;
    }

    class Cachorro extends Animal {
        void correr(){
        
        }
    }
```



7. **Polimorfismo - Sobrecarga**  
   O que é polimorfismo em Orientação a Objetos? Dê um exemplo prático usando sobrecarga em Java.

   Polimorfismo significa “muitas formas”. Na prática, é a capacidade de um mesmo método ou comportamento assumir diferentes formas dependendo do contexto.

    Existem dois tipos principais em Java:

    - Polimorfismo de sobrecarga (overload) → ocorre quando uma classe possui vários métodos com o mesmo nome, mas com parâmetros diferentes.

    - Polimorfismo de sobrescrita (override) → ocorre quando uma subclasse reescreve um método da superclasse.

    Por exemplo: A ação de “desenhar” para uma criança.

    Se der um lápis, a criança vai desenhar no papel.

    Se der um giz de cera, vai desenhar na lousa.

    Se der um pincel, vai desenhar na tela.

    A ação é sempre a mesma: desenhar.
    Mas a forma como acontece muda dependendo da situação.
    Isso é polimorfismo: a mesma coisa pode acontecer de várias formas.

    ``` java

    class Desenho {
    
        void desenhar(String lapis) {
            System.out.println("Desenhando com " + lapis);
        }

        
        void desenhar(String lapis, String papel) {
            System.out.println("Desenhando com " + lapis + " no " + papel);
        }

        
        void desenhar(String pincel, String tinta, String tela) {
            System.out.println("Desenhando com " + pincel + " e " + tinta + " na " + tela);
        }
    }
    ```

    Isso mostra que a mesma ação (“desenhar”) pode acontecer de formas diferentes, dependendo de como é chamada.




8. **Polimorfismo - Sobrescrita**  
   O que é sobreescrita em Orientação a Objetos? Dê um exemplo prático em Java.

   Existe uma classe geral (superclasse) com um método que representa uma ação comum. Quando uma classe mais específica (subclasse) precisa fazer essa mesma ação, mas de um jeito diferente, ela reescreve o método. Essa reescrita é chamada de sobrescrita (override).
   
   Ou seja: o nome do método é o mesmo, mas o comportamento muda dependendo da classe.

    Exemplo:

    A ação “emitir som”:

    - Todo animal pode emitir som.

    - Mas o som do cachorro é diferente do som do gato.

    Mesmo que o método se chame sempre emitirSom(), cada animal emite do seu jeito.

    Exemplo:

    ``` java

        // Superclasse
    class Animal {
        void emitirSom() {
            System.out.println("O animal emite um som");
        }
    }

    // Subclasse
    class Cachorro extends Animal {
        // Sobrescrita do método emitirSom()
        @Override
        void emitirSom() {
            System.out.println("O cachorro late");
        }
    }

    
    public class Main {
        public static void main(String[] args) {
            Animal a1 = new Animal();
            Animal a2 = new Cachorro();

            a1.emitirSom(); 
            a2.emitirSom(); 
        }
    }
    ```

9. **Encapsulamento**  
   O que é encapsulamento? Reescreva a classe `Apartamento` da questão 5 usando atributos privados e fornecendo métodos getters e setters.

   Encapsulamento consiste em proteger os atributos de uma classe, permitindo que sejam acessados ou modificados apenas por métodos específicos (getters e setters).

   - getters → usados para ler o valor.
   - setters → usados para alterar o valor.
   
    Vantagens:

    - Garante segurança (não deixa qualquer parte do programa alterar diretamente os dados).

    - Dá controle sobre como os atributos podem ser lidos ou alterados.

    - Facilita manutenção e validações (ex.: impedir valores inválidos).

Exemplo:

``` java

        class Apartamento {
        // Atributos privados
        private double area;
        private int quartos;
        private int andar;
        private double valorDeCompra;
        private int vagasDeGaragem;
        private boolean temVaranda;

        // Getters e Setters
        public double getArea() {
            return area;
        }

        public void setArea(double area) {
            this.area = area;
        }

        public int getQuartos() {
            return quartos;
        }

        public void setQuartos(int quartos) {
            this.quartos = quartos;
        }

        public int getAndar() {
            return andar;
        }

        public void setAndar(int andar) {
            this.andar = andar;
        }

        public double getValorDeCompra() {
            return valorDeCompra;
        }

        public void setValorDeCompra(double valorDeCompra) {
            this.valorDeCompra = valorDeCompra;
        }

        public int getVagasDeGaragem() {
            return vagasDeGaragem;
        }

        public void setVagasDeGaragem(int vagasDeGaragem) {
            this.vagasDeGaragem = vagasDeGaragem;
        }

        public boolean isTemVaranda() {
            return temVaranda;
        }

        public void setTemVaranda(boolean temVaranda) {
            this.temVaranda = temVaranda;
        }

        
        public void exibirInfo() {
            System.out.println("Área: " + area);
            System.out.println("Quartos: " + quartos);
            System.out.println("Andar: " + andar);
            System.out.println("Valor de Compra: " + valorDeCompra);
            System.out.println("Vagas de Garagem: " + vagasDeGaragem);
            System.out.println("Tem Varanda: " + temVaranda);
        }
    }
```

10. **Relacionamento de agregação**  

    Explique o que é agregação e crie duas classes em Java que representem esse relacionamento.

    Agregação é um tipo de relacionamento entre classes em Orientação a Objetos em que uma classe é formada por outra, mas ambas podem existir de forma independente.

    Exemplo: uma universidade tem professores.

    - A universidade existe mesmo sem professores cadastrados.
    - O professor também existe fora da universidade.

``` java
    
class Professor {
    private String nome;

    public Professor(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}


class Universidade {
    private String nome;
    private Professor professor; 

    public Universidade(String nome, Professor professor) {
        this.nome = nome;
        this.professor = professor;
    }

    public void exibirInfo() {
        System.out.println("Universidade: " + nome);
        System.out.println("Professor: " + professor.getNome());
    }
}


public class Main {
    public static void main(String[] args) {
        Professor prof = new Professor("Maria Silva");
        Universidade unb = new Universidade("UnB", prof);

        unb.exibirInfo();
    }
}


```
   
11. **Projeto orientado a objetos**  
    Desenhe um pequeno diagrama de classes para representar um sistema de gerenciamento de biblioteca, contendo pelo menos três classes (`Livro`, `Usuario`, `Emprestimo`) e depois implemente essas classes em Java.

```yaml
    Usuario 1 --- * Emprestimo * --- 1 Livro
```

``` java
    

class Livro {
    private String titulo;
    private String autor;
    private boolean disponivel = true;

    public Livro(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public boolean isDisponivel() { return disponivel; }

    public void emprestar() { disponivel = false; }
    public void devolver() { disponivel = true; }
}

class Usuario {
    private String nome;
    private String email;

    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
}

class Emprestimo {
    private Usuario usuario;
    private Livro livro;
   
    public Emprestimo(Usuario usuario, Livro livro) {
        this.usuario = usuario;
        this.livro = livro;
    }

   
    public void exibirResumo() {
        System.out.println("Usuário: " + usuario.getNome());
        System.out.println("Livro: " + livro.getTitulo());
    }
}


```
12. **Projeto orientado a objetos 2**  
    Implemente uma Main em Java que instancie pelo menos um objeto de cada uma das classes (`Livro`, `Usuario`, `Emprestimo`) e em seguida realiza um emprestimo e uma devolução.

``` java
            public class Main {
        public static void main(String[] args) {
            
            Usuario usuario = new Usuario("João", "joao@email.com");

            Livro livro = new Livro("Clean Code", "Robert C. Martin");

            Emprestimo emprestimo = new Emprestimo(usuario, livro);
            livro.emprestar(); 

            System.out.println("=== Empréstimo realizado ===");
            emprestimo.exibirResumo();
            System.out.println("Disponível? " + livro.isDisponivel());

            livro.devolver(); 

            System.out.println("\n=== Livro devolvido ===");
            emprestimo.exibirResumo();
            System.out.println("Disponível? " + livro.isDisponivel());
        }
    }

```