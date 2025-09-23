package model;

public class Paciente extends Pessoa {

    private PlanoSaude plano;

    // Construtor do Paciente: repassa nome/cpf/idade para a Mãe (Pessoa)
    public Paciente(String nome, String cpf, int idade){

        super(nome, cpf, idade);
    }

    // Getter/Setter do plano (encapsulamento)
    public PlanoSaude getPlano(){return plano;}
    public void setPlano(PlanoSaude plano){this.plano = plano;}
}


// paciente herda de pessoa