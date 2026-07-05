package br.com.farmaestoque;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pedidos")
public class PedidoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;   // descrição do pedido
    private double valor;       // valor do pedido
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(nullable = false)
    private int numeroLote;

    public PedidoCompra() {}

    public PedidoCompra(String descricao, double valor, LocalDate data, int numeroLote, Fornecedor fornecedor) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.numeroLote = numeroLote;
        this.fornecedor = fornecedor;
    }

    public Long getId() { return id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }
    public int getNumeroLote() { return numeroLote; }
    public void setNumeroLote(int numeroLote) { this.numeroLote = numeroLote; }
}
