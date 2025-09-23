// enum- lista fechada de opções (constantes) que o Java 
// conhece em tempo de compilação.

// exemplo: status de uma consulta 
//só pode ser “AGENDADA”, “CONCLUIDA” ou “CANCELADA”.

package model;


public enum Especialidade {
    CARDIOLOGIA("Cardiologia"),
    PEDIATRIA("pediatria"),
    GERAL("Clínica Geral");

    //nomes das constantes em CAIXA_ALTA com _ no lugar de espaço.
    //cada enum fica em um arquivo com package model;.
    //rótulo amigável pra mostrar na tela (ex.: “Cardiologia”) 
    //e guardar a constante como CARDIOLOGIA.

    private final String rotulo;

    Especialidade(String rotulo){
        this.rotulo = rotulo;
    }
    
    @Override
    public String toString(){
        return rotulo;
    }

    //Converte texto para enum de forma segura (case-insensitive)
    public static Especialidade fromString(String texto) {
        if (texto == null) throw new IllegalArgumentException("Texto nulo");
        String t = texto.trim().toUpperCase();
        // aceita tanto o nome da constante quanto o rótulo
        for (Especialidade e : values()) {
            if (e.name().equals(t) || e.rotulo.toUpperCase().equals(t)) {
                return e;
            }
        }
        // algumas tolerâncias comuns
        if (t.contains("GERAL")) return GERAL;
        throw new IllegalArgumentException("Especialidade inválida: " + texto);
    }


}