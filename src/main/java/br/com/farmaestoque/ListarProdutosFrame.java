package br.com.farmaestoque;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class ListarProdutosFrame extends JFrame {

    public ListarProdutosFrame() {
        setTitle("📦 Medicamentos Cadastrados");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Título
        JLabel titulo = new JLabel("🛒 Lista de Medicamentos no Estoque", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 10));
        add(titulo, BorderLayout.NORTH);

        // Área de texto
        JTextArea resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Fonte monoespaçada para alinhar melhor
        resultadoArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Medicamentos"));
        add(scrollPane, BorderLayout.CENTER);

        // Busca e exibição dos produtos
        List<Produto> produtos = ProdutoService.listarProdutos();

        // Ordena os produtos em ordem alfabética (ignorando maiúsculas/minúsculas)
        Collections.sort(produtos, Comparator.comparing(p -> p.getNome().toLowerCase()));

        StringBuilder sb = new StringBuilder();

        if (produtos.isEmpty()) {
            sb.append("Nenhum medicamento encontrado.");
        } else {
            for (Produto produto : produtos) {
                sb.append("Medicamento: ").append(produto.getNome())
                  .append(" | Preço: R$ ").append(String.format("%.2f", produto.getPreco()))
                  .append(" | Fabricante: ").append(produto.getMarca())
                  .append(" | Princípio ativo: ").append(produto.getPrincipioAtivo())
                  .append(" | Descrição: ").append(produto.getDescricao())
                  .append(" | Quantidade: ").append(produto.getQuantidade())
                  .append("\n");
            }
        }

        resultadoArea.setText(sb.toString());

        setVisible(true);
    }
}
