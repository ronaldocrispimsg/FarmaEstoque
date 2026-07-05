package br.com.farmaestoque;

import javax.swing.*;
import org.hibernate.Session;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CadastroPedidoCompraFrame extends JFrame {

    private JTextField cnpjField;
    private JTextField descricaoField;
    private JTextField valorField;
    private JTextField dataField;
    private JTextField numeroLoteField;

    public CadastroPedidoCompraFrame() {
        setTitle("Registrar Pedido de Compra");
        setSize(380, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel cnpjLabel = new JLabel("CNPJ do Fornecedor:");
        cnpjLabel.setBounds(20, 20, 150, 25);
        cnpjField = new JTextField();
        cnpjField.setBounds(170, 20, 160, 25);

        JLabel descricaoLabel = new JLabel("Descrição do Pedido:");
        descricaoLabel.setBounds(20, 60, 150, 25);
        descricaoField = new JTextField();
        descricaoField.setBounds(170, 60, 160, 25);

        JLabel valorLabel = new JLabel("Valor Total (R$):");
        valorLabel.setBounds(20, 100, 150, 25);
        valorField = new JTextField();
        valorField.setBounds(170, 100, 160, 25);

        JLabel dataLabel = new JLabel("Data (dd/MM/yyyy):");
        dataLabel.setBounds(20, 140, 150, 25);
        dataField = new JTextField();
        dataField.setBounds(170, 140, 160, 25);

        JLabel numeroLoteLabel = new JLabel("Nº do Lote/Pedido:");
        numeroLoteLabel.setBounds(20, 180, 150, 25);
        numeroLoteField = new JTextField();
        numeroLoteField.setBounds(170, 180, 160, 25);

        JButton cadastrarBtn = new JButton("Registrar Pedido");
        cadastrarBtn.setBounds(110, 235, 150, 30);

        add(cnpjLabel); add(cnpjField);
        add(descricaoLabel); add(descricaoField);
        add(valorLabel); add(valorField);
        add(dataLabel); add(dataField);
        add(numeroLoteLabel); add(numeroLoteField);
        add(cadastrarBtn);

        cadastrarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarPedido();
            }
        });

        setVisible(true);
    }

    private void cadastrarPedido() {
        String cnpj = cnpjField.getText().trim();
        String descricao = descricaoField.getText();
        double valor;
        int numeroLote;
        LocalDate data;

        try {
            valor = Double.parseDouble(valorField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido.");
            return;
        }

        try {
            numeroLote = Integer.parseInt(numeroLoteField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de lote/pedido inválido.");
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            data = LocalDate.parse(dataField.getText(), formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.");
            return;
        }

        if (cnpj.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um termo para buscar o fornecedor.");
            return;
        }

        Fornecedor fornecedor = selecionarFornecedor(FornecedorService.buscarFornecedoresPorTermo(cnpj));

        if (fornecedor == null) {
            return;
        }

        PedidoCompra pedido = new PedidoCompra(descricao, valor, data, numeroLote, fornecedor);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(pedido);
            session.getTransaction().commit();

            int opcao = JOptionPane.showConfirmDialog(
                this,
                "Pedido registrado com sucesso!\nDeseja registrar outro pedido para este fornecedor?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
            );

            if (opcao == JOptionPane.YES_OPTION) {
                descricaoField.setText("");
                valorField.setText("");
                dataField.setText("");
                numeroLoteField.setText("");
                descricaoField.requestFocus();
            } else {
                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao registrar pedido.");
        }
    }

    private Fornecedor selecionarFornecedor(List<Fornecedor> fornecedores) {
        if (fornecedores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum resultado encontrado para: " + cnpjField.getText().trim());
            return null;
        }

        if (fornecedores.size() == 1) {
            Fornecedor fornecedor = fornecedores.get(0);
            cnpjField.setText(fornecedor.getCnpj());
            return fornecedor;
        }

        String[] opcoes = new String[fornecedores.size()];
        for (int i = 0; i < fornecedores.size(); i++) {
            Fornecedor f = fornecedores.get(i);
            opcoes[i] = f.getCnpj() + " | " + f.getRazaoSocial() + " | " + f.getResponsavel();
        }

        String selecionado = (String) JOptionPane.showInputDialog(
            this,
            "Foram encontrados vários fornecedores. Selecione um:",
            "Selecionar fornecedor",
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
                Fornecedor fornecedor = fornecedores.get(i);
                cnpjField.setText(fornecedor.getCnpj());
                return fornecedor;
            }
        }

        return null;
    }
}
