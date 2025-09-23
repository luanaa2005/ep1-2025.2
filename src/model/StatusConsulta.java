package model;

public enum StatusConsulta {
    AGENDADA("Agendada"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String rotulo;

    StatusConsulta(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        return rotulo;
    }

    public static StatusConsulta fromString(String texto) {
        if (texto == null) throw new IllegalArgumentException("Texto nulo");
        String t = texto.trim().toUpperCase();
        for (StatusConsulta s : values()) {
            if (s.name().equals(t) || s.rotulo.toUpperCase().equals(t)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + texto);
    }
}
