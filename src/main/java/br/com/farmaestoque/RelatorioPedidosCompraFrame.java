package br.com.farmaestoque;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.time.format.DateTimeFormatter; // usa java.time
import java.util.Objects;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class RelatorioPedidosCompraFrame extends JFrame {
    private JTextArea textoArea;

    public RelatorioPedidosCompraFrame() {
        setTitle("Relatório de Pedidos");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Título
        JLabel titulo = new JLabel("📋 Relatório de Pedidos", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        // Área de texto com rolagem
        textoArea = new JTextArea();
        textoArea.setEditable(false);
        textoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textoArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Histórico de Pedidos de Compra"));
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton exportarBtn = new JButton("📤 Exportar PDF");
        JButton limparBtn   = new JButton("🧹 Limpar Histórico");
        painelBotoes.add(exportarBtn);
        painelBotoes.add(limparBtn);
        add(painelBotoes, BorderLayout.SOUTH);

        exportarBtn.addActionListener((ActionEvent e) -> exportarPDF());
        limparBtn.addActionListener((ActionEvent e) -> limparHistorico());

        carregarPedidos();
        setVisible(true);
    }

    private void carregarPedidos() {
        List<PedidoCompra> pedidos = PedidoCompraService.listarPedidos();
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (pedidos == null || pedidos.isEmpty()) {
            sb.append("Nenhum pedido registrado.");
        } else {
            for (PedidoCompra s : pedidos) {
                Fornecedor c = s.getFornecedor();
                String responsavel = (c != null && c.getResponsavel() != null) ? c.getResponsavel() : "";
                String marca   = (c != null && c.getRazaoSocial() != null)   ? c.getRazaoSocial()   : "";
                String segmento  = (c != null && c.getSegmento() != null)  ? c.getSegmento()  : "";
                String cnpj   = (c != null && c.getCnpj() != null)   ? c.getCnpj()   : "";
                String dataStr = (s.getData() != null) ? s.getData().format(fmt) : "Data não registrada";
                String desc    = Objects.toString(s.getDescricao(), "");
                String numeroLote = String.valueOf(s.getNumeroLote());
                String valor   = String.format("R$ %.2f", s.getValor());

                sb.append("Responsável: ").append(responsavel).append("\n")
                  .append("🏭 Fornecedor: ").append(marca).append(" ").append(segmento).append(" (").append(cnpj).append(")").append("\n")
                  .append("📅 Data: ").append(dataStr).append("\n")
                  .append("📦 Nº Lote/Pedido: ").append(numeroLote).append("\n")
                  .append("Descrição: ").append(desc).append("\n")
                  .append("💰 Valor: ").append(valor).append("\n")
                  .append("────────────────────────────\n");
            }
        }

        textoArea.setText(sb.toString());
    }

    private void exportarPDF() {
        String conteudoRelatorio = textoArea.getText();
        if (conteudoRelatorio == null || conteudoRelatorio.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há conteúdo para exportar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar relatório de pedidos de compra");
        fileChooser.setSelectedFile(new File("relatorio_pedidos_compra.pdf"));

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = fileChooser.getSelectedFile();
        if (!arquivo.getName().toLowerCase().endsWith(".pdf")) {
            arquivo = new File(arquivo.getParentFile(), arquivo.getName() + ".pdf");
        }

        Document document = new Document();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(arquivo);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            com.itextpdf.text.Font tituloFont = criarFonte(16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font textoFont = criarFonte(11, com.itextpdf.text.Font.NORMAL);

            Paragraph titulo = new Paragraph("Relatório de Pedidos de Compra - FarmaEstoque", tituloFont);
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph conteudo = new Paragraph(conteudoRelatorio, textoFont);
            conteudo.setLeading(14);
            document.add(conteudo);

            document.close();
            outputStream.close();
            outputStream = null;

            if (!arquivo.exists() || arquivo.length() == 0) {
                if (arquivo.exists() && arquivo.length() == 0) {
                    arquivo.delete();
                }
                JOptionPane.showMessageDialog(this, "O PDF foi gerado vazio. Verifique o conteúdo do relatório.");
                return;
            }

            JOptionPane.showMessageDialog(this, "PDF salvo com sucesso em:\n" + arquivo.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Não foi possível exportar o relatório em PDF.");
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private com.itextpdf.text.Font criarFonte(int tamanho, int estilo) throws Exception {
        String[] fontes = {
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf",
            "C:\\Windows\\Fonts\\arial.ttf",
            "/System/Library/Fonts/Supplemental/Arial Unicode.ttf"
        };

        for (String caminho : fontes) {
            File fonteArquivo = new File(caminho);
            if (fonteArquivo.exists()) {
                BaseFont baseFont = BaseFont.createFont(caminho, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                return new com.itextpdf.text.Font(baseFont, tamanho, estilo);
            }
        }

        return new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, tamanho, estilo);
    }

    private void limparHistorico() {
        // 1) exige usuário logado
        Usuario logado = UsuarioLogado.getUsuario();
        if (logado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado. Faça login novamente.");
            return;
        }

        // (opcional) valida perfil ADMIN, se existir getPerfil()
        try {
            java.lang.reflect.Method m = logado.getClass().getMethod("getPerfil");
            Object perfil = m.invoke(logado);
            if (perfil == null || !"ADMIN".equalsIgnoreCase(perfil.toString())) {
                JOptionPane.showMessageDialog(this, "Apenas administradores podem limpar o histórico.");
                return;
            }
        } catch (NoSuchMethodException ignore) {
            // sem campo de perfil, ignora
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 2) prompt de senha mostrando quem confirma
        JPasswordField senhaField = new JPasswordField();
        JPanel p = new JPanel(new GridLayout(2, 1, 6, 6));
        p.add(new JLabel("Confirmar como: " + logado.getLogin()));
        p.add(senhaField);

        int result = JOptionPane.showConfirmDialog(
                this, p, "Digite sua senha para confirmar", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String senhaDigitada = new String(senhaField.getPassword());
        java.util.Arrays.fill(senhaField.getPassword(), '\0');

        // 3) recarrega o usuário do banco (garante usar o hash mais recente)
        Usuario usuarioBanco = buscarUsuarioPorLogin(logado.getLogin());
        if (usuarioBanco == null || usuarioBanco.getSenha() == null || usuarioBanco.getSenha().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não foi possível validar suas credenciais.");
            return;
        }
        String hashBanco = usuarioBanco.getSenha();

        // DEBUG opcional
        String shaCalc = SegurancaUtil.sha256Hex(senhaDigitada);
        System.out.println("Auth(pedidos) -> login=" + usuarioBanco.getLogin()
                + ", hashLen=" + (hashBanco == null ? 0 : hashBanco.length())
                + ", hashPrefix=" + (hashBanco == null ? "<null>" :
                    (hashBanco.length() >= 7 ? hashBanco.substring(0, 7) : hashBanco)));
        System.out.println("Auth(pedidos) calc -> shaCalc=" + shaCalc);

        // 4) valida senha (SHA-256 hex)
        boolean ok = SegurancaUtil.verificarSenha(senhaDigitada, hashBanco);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "❌ Senha incorreta.");
            return;
        }

        // 5) executa limpeza
        try {
            PedidoCompraService.limparPedidos();
            textoArea.setText("");
            JOptionPane.showMessageDialog(this, "✅ Histórico de pedidos limpo.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Falha ao limpar o histórico.");
        }
    }

    /** Busca o usuário por login para validar a senha com o hash atual do banco. */
    private Usuario buscarUsuarioPorLogin(String login) {
        if (login == null || login.isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> q = session.createQuery(
                "FROM Usuario u WHERE u.login = :login", Usuario.class);
            q.setParameter("login", login);
            return q.uniqueResult();
        }
    }
}
