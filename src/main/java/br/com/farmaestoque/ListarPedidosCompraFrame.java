package br.com.farmaestoque;

import javax.swing.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListarPedidosCompraFrame extends JFrame {

    private JTextField cnpjField;
    private JTextArea resultadoArea;
    private JButton gerarPdfBtn;

    private Fornecedor fornecedorAtual;
    private List<PedidoCompra> pedidosAtual;

    public ListarPedidosCompraFrame() {
        setTitle("🔍 Consulta de Pedidos por Fornecedor");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Painel superior com campo de busca
        JPanel painelBusca = new JPanel();
        painelBusca.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JLabel cnpjLabel = new JLabel("Fornecedor/CNPJ:");
        cnpjLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        cnpjField = new JTextField(10);
        JButton buscarBtn = new JButton("🔍 Buscar");

        buscarBtn.addActionListener((ActionEvent e) -> listarPedidos());

        painelBusca.add(cnpjLabel);
        painelBusca.add(cnpjField);
        painelBusca.add(buscarBtn);

        add(painelBusca, BorderLayout.NORTH);

        // Área de resultado
        resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultadoArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Detalhes dos Pedidos"));

        add(scrollPane, BorderLayout.CENTER);

        // Botão de exportar
        gerarPdfBtn = new JButton("📄 Gerar PDF");
        gerarPdfBtn.setEnabled(false);
        gerarPdfBtn.addActionListener(ev -> {
            if (fornecedorAtual != null && pedidosAtual != null) {
                RelatorioPDF.gerarRelatorioPedidos(fornecedorAtual, pedidosAtual);
                JOptionPane.showMessageDialog(this, "✅ PDF gerado com sucesso!");
            }
        });

        JPanel painelInferior = new JPanel();
        painelInferior.add(gerarPdfBtn);
        add(painelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void listarPedidos() {
        String cnpj = cnpjField.getText().trim();

        if (cnpj.isBlank()) {
            JOptionPane.showMessageDialog(this, "Digite um termo para buscar.");
            return;
        }

        Fornecedor fornecedor = selecionarFornecedor(FornecedorService.buscarFornecedoresPorTermo(cnpj));

        if (fornecedor == null) {
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<PedidoCompra> query = session.createQuery("FROM PedidoCompra WHERE fornecedor.id = :fornecedorId", PedidoCompra.class);
            query.setParameter("fornecedorId", fornecedor.getId());
            List<PedidoCompra> pedidos = query.list();

            session.getTransaction().commit();

            if (pedidos.isEmpty()) {
                resultadoArea.setText("Nenhum pedido encontrado para o fornecedor " + fornecedor.getCnpj());
                gerarPdfBtn.setEnabled(false);
            } else {
                StringBuilder sb = new StringBuilder();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                double total = 0;

                sb.append("Pedidos do fornecedor ").append(fornecedor.getCnpj().toUpperCase()).append(":\n\n");
                for (PedidoCompra pedido : pedidos) {
                    sb.append("• ").append(pedido.getDescricao())
                      .append(" | 💰 R$ ").append(String.format("%.2f", pedido.getValor()))
                      .append(" | 📅 ").append(pedido.getData().format(formatter))
                      .append(" | Nº lote/pedido: ").append(pedido.getNumeroLote())
                      .append("\n");
                    total += pedido.getValor();
                }

                sb.append("\n💵 TOTAL: R$ ").append(String.format("%.2f", total));

                resultadoArea.setText(sb.toString());

                fornecedorAtual = fornecedor;
                pedidosAtual = pedidos;
                gerarPdfBtn.setEnabled(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar pedidos.");
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
