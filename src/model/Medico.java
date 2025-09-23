package model;

public class Medico extends Pessoa {

    private String crm;
    private Especialidade especialidade; // enum
    private double custoBaseConsulta;

    //construtor
    public Medico(String nome, String cpf, int idade, String crm, Especialidade especialidade, double custoBaseConsulta){
        super(nome, cpf, idade);
        this.crm = crm;
        this.especialidade = especialidade;
        this.custoBaseConsulta = custoBaseConsulta;
    }

    //getters, setters
    public String getCrm() {return crm;}
    public void setCrm(String crm) { this.crm = crm;}

    public Especialidade getEspecialidade(){return especialidade;}
    public void setEspecialidade(Especialidade especialidade){this.especialidade = especialidade;}

    public double getCustoBaseConsulta(){return custoBaseConsulta;}
    public void setCustoBaseConsulta(double custoBaseConsulta){this.custoBaseConsulta = custoBaseConsulta;}
}