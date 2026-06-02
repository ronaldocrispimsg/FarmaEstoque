package br.com.farmaestoque;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class VenderProdutoFrame extends JFrame {

    private JTextField nomeField;
    private JTextField quantidadeField;
    private JTextField marcaField;
    private JTextField compradorField;

    public VenderProdutoFrame() {
        setTitle("Vender Produto");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel nomeLabel = new JLabel("Nome do Medicamento:");
        nomeLabel.setBounds(20, 20, 120, 25);
        nomeField = new JTextField();
        nomeField.setBounds(140, 20, 200, 25);

        JLabel marcaLabel = new JLabel("Marca:");
        marcaLabel.setBounds(20, 60, 120, 25);
        marcaField = new JTextField();
        marcaField.setBounds(140, 60, 200, 25);

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setBounds(20, 100, 120, 25);
        quantidadeField = new JTextField();
        quantidadeField.setBounds(140, 100, 200, 25);

        JLabel compradorLabel = new JLabel("Farmácia/Cliente Destino:");
        compradorLabel.setBounds(20, 140, 120, 25);
        compradorField = new JTextField();
        compradorField.setBounds(140, 140, 200, 25);

        JButton venderBtn = new JButton("Vender");
        venderBtn.setBounds(140, 180, 120, 30);

        add(nomeLabel);
        add(nomeField);
        add(marcaLabel);
        add(marcaField);
        add(quantidadeLabel);
        add(quantidadeField);
        add(compradorLabel);
        add(compradorField);
        add(venderBtn);

        venderBtn.addActionListener((ActionEvent e) -> venderProduto());

        setVisible(true);
    }

    private void venderProduto() {
        String nome = nomeField.getText().trim();
        String marca = marcaField.getText().trim();
        String quantidadeStr = quantidadeField.getText().trim();
        String comprador = compradorField.getText().trim();

        if (nome.isEmpty() || marca.isEmpty() || quantidadeStr.isEmpty() || comprador.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try {
            int quantidadeVendida = Integer.parseInt(quantidadeStr);
            List<Produto> produtos = ProdutoService.buscarProdutosParaVenda(nome, marca);

            if (produtos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum resultado encontrado para: " + nome + " / " + marca);
                return;
            }

            Produto produto = selecionarProduto(produtos);
            if (produto == null) {
                return;
            }

            if (produto.getQuantidade() < quantidadeVendida) {
                JOptionPane.showMessageDialog(this, "Estoque insuficiente para venda.");
                return;
            }

            ProdutoService.diminuirQuantidade(produto, quantidadeVendida);
            double valorTotal = produto.getPreco() * quantidadeVendida;
            Venda venda = new Venda(produto, quantidadeVendida, comprador, valorTotal);
            VendaService.salvarVenda(venda);

            JOptionPane.showMessageDialog(this, "Venda registrada e estoque atualizado com sucesso!");
            limparCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        marcaField.setText("");
        quantidadeField.setText("");
        compradorField.setText("");
    }

    private Produto selecionarProduto(List<Produto> produtos) {
        if (produtos.size() == 1) {
            Produto produto = produtos.get(0);
            nomeField.setText(produto.getNome());
            marcaField.setText(produto.getMarca());
            return produto;
        }

        String[] opcoes = new String[produtos.size()];
        for (int i = 0; i < produtos.size(); i++) {
            Produto p = produtos.get(i);
            opcoes[i] = p.getNome() + " | " + p.getMarca() + " | Estoque: " + p.getQuantidade();
        }

        String selecionado = (String) JOptionPane.showInputDialog(
            this,
            "Foram encontrados vários medicamentos. Selecione um:",
            "Selecionar medicamento",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcoes,
            opcoes[0]
        );

        if (selecionado == null) {
            return null;
        }

        for (int i = 0; i < opcoes.length; i++) {
            if (opcoes[i].equals(selecionado)) {
                Produto produto = produtos.get(i);
                nomeField.setText(produto.getNome());
                marcaField.setText(produto.getMarca());
                return produto;
            }
        }

        return null;
    }
}
