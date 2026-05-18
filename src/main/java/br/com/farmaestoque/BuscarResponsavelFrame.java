package br.com.farmaestoque;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class BuscarResponsavelFrame extends JFrame {

    private JTextField responsavelField;
    private JTextArea resultadoArea;

    public BuscarResponsavelFrame() {
        setTitle("Buscar Fornecedores por Responsável");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel responsavelLabel = new JLabel("Nome do Responsável:");
        responsavelLabel.setBounds(20, 20, 120, 25);
        responsavelField = new JTextField();
        responsavelField.setBounds(150, 20, 200, 25);

        JButton buscarBtn = new JButton("Buscar");
        buscarBtn.setBounds(370, 20, 90, 25);

        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBounds(20, 60, 440, 280);

        add(responsavelLabel);
        add(responsavelField);
        add(buscarBtn);
        add(scrollPane);

        buscarBtn.addActionListener((ActionEvent e) -> buscarFornecedores());

        setVisible(true);
    }

    private void buscarFornecedores() {
        String nomeResponsavel = responsavelField.getText().trim();

        if (nomeResponsavel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um termo para buscar.");
            return;
        }

        try {
            List<Fornecedor> fornecedores = FornecedorService.buscarFornecedoresPorResponsavel(nomeResponsavel);

            if (fornecedores.isEmpty()) {
                resultadoArea.setText("Nenhum resultado encontrado para: " + nomeResponsavel);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Fornecedores encontrados para ").append(nomeResponsavel).append(":\n\n");

                for (Fornecedor fornecedor : fornecedores) {
                    sb.append("• CNPJ: ").append(fornecedor.getCnpj())
                      .append(" | Razão Social: ").append(fornecedor.getRazaoSocial())
                      .append(" | Segmento: ").append(fornecedor.getSegmento())
                      .append(" | Ano Cadastro: ").append(fornecedor.getAnoCadastro())
                      .append("\n");
                }

                resultadoArea.setText(sb.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar fornecedores.");
        }
    }
}
