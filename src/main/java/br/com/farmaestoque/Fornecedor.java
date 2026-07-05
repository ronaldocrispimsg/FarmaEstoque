package br.com.farmaestoque;

import javax.persistence.*;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cnpj;
    private String responsavel;
    private String razaoSocial;
    private String segmento;

    @Column(nullable = false)
    private boolean ativo = true;

    private int anoCadastro;

    public Fornecedor() {}

    public Fornecedor(String cnpj, String razaoSocial, String segmento, int anoCadastro, String responsavel) {
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.segmento = segmento;
        this.anoCadastro = anoCadastro;
        this.responsavel = responsavel;
        this.ativo = true;
    }

    public Long getId() { return id; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getSegmento() { return segmento; }
    public void setSegmento(String segmento) { this.segmento = segmento; }
    public int getAnoCadastro() { return anoCadastro; }
    public void setAnoCadastro(int anoCadastro) { this.anoCadastro = anoCadastro; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
