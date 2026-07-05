package br.com.farmaestoque;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CadastrarProdutoFrame extends JFrame {

    private JTextField nomeField;
    private JTextField precoField;
    private JTextField marcaField;
    private JTextField quantidadeField;
    private JTextArea principioAtivoArea;
    private JTextArea descricaoArea;

    public CadastrarProdutoFrame() {
        setTitle("Cadastrar Medicamento");
        setSize(430, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel nomeLabel = new JLabel("Nome do Medicamento:");
        nomeLabel.setBounds(20, 20, 150, 25);
        nomeField = new JTextField();
        nomeField.setBounds(170, 20, 210, 25);

        JLabel precoLabel = new JLabel("Preço (R$):");
        precoLabel.setBounds(20, 60, 150, 25);
        precoField = new JTextField();
        precoField.setBounds(170, 60, 210, 25);

        JLabel marcaLabel = new JLabel("Fabricante:");
        marcaLabel.setBounds(20, 100, 150, 25);
        marcaField = new JTextField();
        marcaField.setBounds(170, 100, 210, 25);

        JLabel principioAtivoLabel = new JLabel("Princípio Ativo:");
        principioAtivoLabel.setBounds(20, 140, 150, 25);
        principioAtivoArea = new JTextArea();
        principioAtivoArea.setBounds(170, 140, 210, 70);
        principioAtivoArea.setLineWrap(true);
        principioAtivoArea.setWrapStyleWord(true);

        JLabel quantidadeLabel = new JLabel("Quantidade em Estoque:");
        quantidadeLabel.setBounds(20, 225, 150, 25);
        quantidadeField = new JTextField();
        quantidadeField.setBounds(170, 225, 210, 25);

        JLabel descricaoLabel = new JLabel("Dosagem/Apresentação:");
        descricaoLabel.setBounds(20, 265, 150, 25);
        descricaoArea = new JTextArea();
        descricaoArea.setBounds(170, 265, 210, 80);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);

        JButton salvarBtn = new JButton("Salvar");
        salvarBtn.setBounds(155, 370, 120, 30);

        add(nomeLabel); add(nomeField);
        add(precoLabel); add(precoField);
        add(marcaLabel); add(marcaField);
        add(principioAtivoLabel); add(principioAtivoArea);
        add(quantidadeLabel); add(quantidadeField);
        add(descricaoLabel); add(descricaoArea);
        add(salvarBtn);

        salvarBtn.addActionListener((ActionEvent e) -> salvarProduto());

        setVisible(true);
    }

    private void salvarProduto() {
        String nome = nomeField.getText();
        String precoStr = precoField.getText();
        String marca = marcaField.getText();
        String principioAtivo = principioAtivoArea.getText();
        String descricao = descricaoArea.getText();
        String quantidadeStr = quantidadeField.getText();

        if (nome.isBlank() || precoStr.isBlank() || marca.isBlank() || principioAtivo.isBlank() || descricao.isBlank() || quantidadeStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try {
            Double preco = Double.parseDouble(precoStr);
            int quantidade = Integer.parseInt(quantidadeStr);

            Produto existente = ProdutoService.buscarProdutoPorNomeMarca(nome, marca);
            if (existente != null) {
                int novaQuantidade = existente.getQuantidade() + quantidade;
                existente.setQuantidade(novaQuantidade);
                ProdutoService.atualizarProduto(existente);
                JOptionPane.showMessageDialog(this, "Medicamento já existia. Estoque atualizado para " + novaQuantidade + " unidades.");
            } else {
                ProdutoService.salvarProduto(nome, preco, marca, principioAtivo, descricao, quantidade);
                JOptionPane.showMessageDialog(this, "Medicamento cadastrado com sucesso!");
            }

            limparCampos();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço ou quantidade inválidos.");
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        precoField.setText("");
        marcaField.setText("");
        principioAtivoArea.setText("");
        descricaoArea.setText("");
        quantidadeField.setText("");
    }
}
