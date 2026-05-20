package br.com.farmaestoque;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class PrincipalFrame extends JFrame {

    public PrincipalFrame() {
        setTitle("FarmaEstoque - Sistema de Gestão de Estoque Farmacêutico");
        setSize(520, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Título no topo
        JLabel titulo = new JLabel("💊 FarmaEstoque", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        // Painel dos botões
        JPanel painel = new JPanel(new GridLayout(7, 2, 10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Menu Principal"));
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 15, 15, 15),
            BorderFactory.createTitledBorder("Menu Principal")
        ));

        JButton cadastrarMedicamentoBtn = new JButton("💊 Cadastrar Medicamento");
        JButton cadastrarFornecedorBtn  = new JButton("🏭 Cadastrar Fornecedor");
        JButton cadastrarPedidoBtn      = new JButton("📋 Registrar Pedido");
        JButton listarMedicamentosBtn   = new JButton("📦 Listar Medicamentos");
        JButton listarFornecedoresBtn   = new JButton("🏢 Listar Fornecedores");
        JButton buscarMedicamentoBtn    = new JButton("🔍 Buscar Medicamento");
        JButton buscarFornecedorBtn     = new JButton("🔍 Buscar Fornecedor");
        JButton excluirMedicamentoBtn   = new JButton("❌ Excluir Medicamento");
        JButton excluirFornecedorBtn    = new JButton("❌ Excluir Fornecedor");
        JButton listarPedidosBtn        = new JButton("🧾 Listar Pedidos");
        JButton importarMedicamentosBtn = new JButton("📄 Importar Medicamentos CSV");
        JButton venderMedicamentoBtn    = new JButton("🛒 Vender Produto");
        JButton relatoriosBtn           = new JButton("📑 Relatórios");
        JButton sairBtn                 = new JButton("⛔ Sair");

        painel.add(cadastrarMedicamentoBtn);
        painel.add(cadastrarFornecedorBtn);
        painel.add(cadastrarPedidoBtn);
        painel.add(importarMedicamentosBtn);
        painel.add(listarMedicamentosBtn);
        painel.add(listarFornecedoresBtn);
        painel.add(buscarMedicamentoBtn);
        painel.add(buscarFornecedorBtn);
        painel.add(excluirMedicamentoBtn);
        painel.add(excluirFornecedorBtn);
        painel.add(listarPedidosBtn);
        painel.add(venderMedicamentoBtn);
        painel.add(relatoriosBtn);
        painel.add(sairBtn);

        add(painel, BorderLayout.CENTER);

        // Ações
        cadastrarMedicamentoBtn.addActionListener(e -> new CadastrarProdutoFrame());
        cadastrarFornecedorBtn.addActionListener(e -> new CadastroFornecedorFrame());
        cadastrarPedidoBtn.addActionListener(e -> new CadastroPedidoCompraFrame());
        listarMedicamentosBtn.addActionListener(e -> new ListarProdutosFrame());
        listarFornecedoresBtn.addActionListener(e -> new ListarFornecedoresFrame());
        buscarMedicamentoBtn.addActionListener(e -> new BuscarProdutoFrame());
        buscarFornecedorBtn.addActionListener(e -> new BuscarFornecedorFrame());
        excluirMedicamentoBtn.addActionListener(e -> new ExcluirProdutoFrame());
        excluirFornecedorBtn.addActionListener(e -> new ExcluirFornecedorFrame());
        listarPedidosBtn.addActionListener(e -> new ListarPedidosCompraFrame());
        venderMedicamentoBtn.addActionListener(e -> new VenderProdutoFrame());
        relatoriosBtn.addActionListener(e -> new RelatorioFrame());
        sairBtn.addActionListener(e -> System.exit(0));

        importarMedicamentosBtn.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecione o arquivo CSV");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos CSV", "csv"));
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    ImportarProdutosCSV.ResultadoImportacao resultadoImportacao =
                        new ImportarProdutosCSV().importarProdutos(selectedFile);

                    if (resultadoImportacao.getImportados() == 0) {
                        JOptionPane.showMessageDialog(this, criarMensagemImportacao(resultadoImportacao));
                    } else {
                        JOptionPane.showMessageDialog(this, criarMensagemImportacao(resultadoImportacao));
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao importar medicamentos do arquivo.");
                }
            }
        });

        setVisible(true);
    }

    private String criarMensagemImportacao(ImportarProdutosCSV.ResultadoImportacao resultado) {
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Produtos importados: ").append(resultado.getImportados()).append("\n");
        mensagem.append("Linhas ignoradas: ").append(resultado.getIgnorados());

        if (!resultado.getErros().isEmpty()) {
            mensagem.append("\n\nLinhas com erro:");
            for (String erro : resultado.getErros()) {
                mensagem.append("\n").append(erro);
            }
        }

        if (resultado.getImportados() == 0) {
            mensagem.append("\n\nFormato esperado:\n");
            mensagem.append("nome,preco,marca,principioAtivo,descricao,quantidade\n");
            mensagem.append("Dipirona,9.90,EMS,Dipirona,Analgésico,50");
        }

        return mensagem.toString();
    }
}
