// paciente e medico sao pessoas e compartilham atributos basicos

// a classe pessoa é marcada como abstract porque nao se pode criar 
//diretamente uma "pessoa", apenas subclasses (paciente e medico)

package model; 

public abstract class Pessoa { // public- a classe pode ser vista e usada por qualquer pacote

    private String nome; // private- apenas a propria classe pode acessar esses campos
    private String cpf;
    private int idade;


    // construtor (para inicializar)

    public Pessoa(String nome, String cpf, int idade){
        this.nome = nome;
        this.cpf = cpf;
        this.idade = idade;
    }

    public String getNome(){return nome;}
    public void setNome(String nome){this.nome=nome;}

    // Getter/Setter (acessores/mutadores) são a “porta controlada” para o campo privado. 
    //public → qualquer código pode ler (get) e alterar (set) o nome, 
    //mas passando por aqui você pode validar (ex.: não aceitar vazio).
    //Isso é encapsulamento na prática: campo private + métodos public 
    //que controlam o acesso.

    public String getCpf(){return cpf;}
    public void setCpf(String cpf){this.cpf= cpf;}

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }
}


