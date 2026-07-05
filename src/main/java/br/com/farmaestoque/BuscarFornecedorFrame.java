package br.com.farmaestoque;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class BuscarFornecedorFrame extends JFrame {

    private JComboBox<String> filtroCombo;
    private JTextField campoBusca;
    private JButton buscarBtn;
    private JTextArea resultadoArea;

    public BuscarFornecedorFrame() {
        setTitle("Buscar Fornecedor");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel filtroLabel = new JLabel("Buscar por:");
        filtroLabel.setBounds(20, 20, 80, 25);

        filtroCombo = new JComboBox<>(new String[]{"CNPJ", "Responsável", "Data"});
        filtroCombo.setBounds(100, 20, 150, 25);

        campoBusca = new JTextField();
        campoBusca.setBounds(270, 20, 180, 25);

        buscarBtn = new JButton("Buscar");
        buscarBtn.setBounds(180, 60, 120, 30);

        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBounds(20, 100, 440, 240);

        add(filtroLabel);
        add(filtroCombo);
        add(campoBusca);
        add(buscarBtn);
        add(scrollPane);

        buscarBtn.addActionListener(this::realizarBusca);

        setVisible(true);
    }

    private void realizarBusca(ActionEvent e) {
        String filtro = (String) filtroCombo.getSelectedItem();
        String valor = campoBusca.getText().trim();

        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um termo para buscar.");
            return;
        }

        switch (filtro) {
            case "CNPJ":
                List<Fornecedor> fornecedoresPorCnpj = FornecedorService.buscarFornecedoresPorTermo(valor);
                if (fornecedoresPorCnpj.isEmpty()) {
                    resultadoArea.setText("Nenhum resultado encontrado para: " + valor);
                } else {
                    resultadoArea.setText(formatarFornecedores(fornecedoresPorCnpj));
                }
                break;

            case "Responsável":
                List<Fornecedor> listaFornecedores = FornecedorService.buscarFornecedoresPorResponsavel(valor);
                if (listaFornecedores.isEmpty()) {
                    resultadoArea.setText("Nenhum resultado encontrado para: " + valor);
                } else {
                    resultadoArea.setText(formatarFornecedores(listaFornecedores));
                }
                break;

            case "Data":
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate data = LocalDate.parse(valor, formatter);

                    List<PedidoCompra> pedidos = PedidoCompraService.buscarPedidosPorData(data);
                    if (pedidos == null || pedidos.isEmpty()) {
                        resultadoArea.setText("Nenhum pedido encontrado para a data: " + valor);
                    } else {
                        StringBuilder sb = new StringBuilder("Pedidos registrados na data " + valor + ":\n\n");
                        for (PedidoCompra s : pedidos) {
                              sb .append("\nResponsável: ").append(s.getFornecedor().getResponsavel())
                              .append("\nValor: R$ ").append(String.format("%.2f", s.getValor()))
                              .append("\nCNPJ: ").append(s.getFornecedor().getCnpj())
                              .append("\nNº lote/pedido: ").append(s.getNumeroLote())
                              .append("\nDescrição: ").append(s.getDescricao())
                              .append("\n\n");
                        }
                        resultadoArea.setText(sb.toString());
                    }

                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.");
                }
                break;
        }
    }

    private String formatarFornecedor(Fornecedor fornecedor) {
        return "ID: " + fornecedor.getId() +
                "\nResponsável: " + fornecedor.getResponsavel() +
                "\nCNPJ: " + fornecedor.getCnpj() +
                "\nRazão Social: " + fornecedor.getRazaoSocial() +
                "\nSegmento: " + fornecedor.getSegmento() +
                "\nAno Cadastro: " + fornecedor.getAnoCadastro();
    }

    private String formatarFornecedores(List<Fornecedor> fornecedores) {
        StringBuilder resultado = new StringBuilder("Fornecedores encontrados:\n\n");
        for (Fornecedor fornecedor : fornecedores) {
            resultado.append(formatarFornecedor(fornecedor)).append("\n\n");
        }
        return resultado.toString();
    }
}
