package br.com.farmaestoque;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BuscarProdutoFrame extends JFrame {

    private JTextField nomeField;
    private JTextArea resultadoArea;

    public BuscarProdutoFrame() {
        setTitle("🔎 Buscar Medicamento");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel de entrada (campo + botão)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel nomeLabel = new JLabel("Digite nome, fabricante, princípio ativo ou descrição:");
        nomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        nomeField = new JTextField(20);
        nomeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JButton buscarBtn = new JButton("Buscar 🔍");
        buscarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(nomeLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(nomeField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(buscarBtn);

        add(inputPanel, BorderLayout.NORTH);

        // Área de resultado
        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Resultados"));
        add(scrollPane, BorderLayout.CENTER);

        buscarBtn.addActionListener(e -> buscarProduto());

        setVisible(true);
    }

    private void buscarProduto() {
        String nome = nomeField.getText().trim();
        if (nome.isBlank()) {
            JOptionPane.showMessageDialog(this, "Digite um termo para buscar.");
            return;
        }

        List<Produto> produtos = ProdutoService.buscarProdutoPorNome(nome);

        StringBuilder sb = new StringBuilder();
        if (produtos.isEmpty()) {
            sb.append("Nenhum resultado encontrado para: ").append(nome);
        } else {
            sb.append("Medicamentos encontrados:\n\n");
            for (Produto produto : produtos) {
                sb.append("🛒 ").append(produto.getNome()).append("\n")
                  .append("Fabricante: ").append(produto.getMarca()).append("\n")
                  .append("Preço: R$ ").append(String.format("%.2f", produto.getPreco())).append("\n")
                  .append("Quantidade: ").append(produto.getQuantidade()).append("\n")
                  .append("Princípio ativo: ").append(produto.getPrincipioAtivo()).append("\n")
                  .append("Descrição: ").append(produto.getDescricao()).append("\n")
                  .append("----------------------------------------------------\n");
            }
        }

        resultadoArea.setText(sb.toString());
    }
}
