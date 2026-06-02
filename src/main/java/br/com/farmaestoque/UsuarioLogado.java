package br.com.farmaestoque;

public class UsuarioLogado {
    private static Usuario usuario;

    public static void setUsuario(Usuario u) {
        usuario = u;
    }

    public static Usuario getUsuario() {
        return usuario;
    }
}
