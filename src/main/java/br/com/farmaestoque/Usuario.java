package br.com.farmaestoque;

import javax.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    public static final String PERFIL_USUARIO = "USUARIO";
    public static final String PERFIL_ADMIN = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    // mapeia o atributo 'senha' da classe para a coluna 'senhaHash' do banco
    @Column(name = "senhaHash", nullable = false)
    private String senha;

    @Column(nullable = false, columnDefinition = "varchar(20) default 'USUARIO'")
    private String perfil = PERFIL_USUARIO;

    public Usuario() {}

    public Usuario(String login, String senha) {
        this.login = login;
        this.senha = senha;
        this.perfil = PERFIL_USUARIO;
    }

    public Usuario(String login, String senha, String perfil) {
        this.login = login;
        this.senha = senha;
        setPerfil(perfil);
    }

    // Getters e Setters
    public Long getId() { 
        return id; 
    }

    public String getLogin() { 
        return login; 
    }

    public void setLogin(String login) { 
        this.login = login; 
    }

    public String getSenha() { 
        return senha; 
    }

    public void setSenha(String senha) { 
        this.senha = senha; 
    }

    public String getPerfil() {
        return perfil == null || perfil.isBlank() ? PERFIL_USUARIO : perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil == null || perfil.isBlank()
                ? PERFIL_USUARIO
                : perfil.trim().toUpperCase();
    }

    public boolean isAdmin() {
        return PERFIL_ADMIN.equalsIgnoreCase(getPerfil());
    }

    @PrePersist
    @PreUpdate
    private void normalizarPerfil() {
        setPerfil(perfil);
    }
}
