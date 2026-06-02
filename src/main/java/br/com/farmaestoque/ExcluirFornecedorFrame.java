package br.com.farmaestoque;

import javax.swing.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.io.File;
import java.util.List;

public class ExcluirFornecedorFrame extends JFrame {

    private JTextField cnpjField;

    public ExcluirFornecedorFrame() {
        setTitle("Excluir Fornecedor");
        setSize(350, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel cnpjLabel = new JLabel("Fornecedor/CNPJ:");
        cnpjLabel.setBounds(20, 20, 120, 25);
        cnpjField = new JTextField();
        cnpjField.setBounds(140, 20, 150, 25);

        JButton excluirBtn = new JButton("Excluir");
        excluirBtn.setBounds(110, 60, 120, 30);

        add(cnpjLabel);
        add(cnpjField);
        add(excluirBtn);

        excluirBtn.addActionListener(e -> excluirFornecedor());

        setVisible(true);
    }

    private void excluirFornecedor() {
        String termoFornecedor = cnpjField.getText().trim();
        if (termoFornecedor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um termo para buscar.");
            return;
        }

        // exige usuário logado
        Usuario usuarioLogado = UsuarioLogado.getUsuario();
        if (usuarioLogado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado. Faça login novamente.");
            return;
        }

        if (!usuarioLogado.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Apenas administradores podem excluir fornecedores.");
            return;
        }

        Fornecedor fornecedor = selecionarFornecedor(FornecedorService.buscarFornecedoresPorTermo(termoFornecedor));
        if (fornecedor == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir o fornecedor:\n" +
            "CNPJ: " + fornecedor.getCnpj() + "\nSegmento: " + fornecedor.getSegmento() + "\nResponsável: " + fornecedor.getResponsavel(),
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        // pede a senha do PRÓPRIO usuário logado e mostra quem está confirmando
        JPasswordField passwordField = new JPasswordField();
        JPanel p = new JPanel(new java.awt.GridLayout(2, 1, 6, 6));
        p.add(new JLabel("Confirmar como: " + usuarioLogado.getLogin()));
        p.add(passwordField);
        int result = JOptionPane.showConfirmDialog(
            this, p, "Digite sua senha (administrador):", JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) return;

        char[] senhaChars = passwordField.getPassword();
        String senhaDigitada = new String(senhaChars);
        java.util.Arrays.fill(senhaChars, '\0');

        // recarrega o usuário do banco e verifica com SHA-256
        Usuario usuarioParaValidar = buscarUsuarioPorLogin(usuarioLogado.getLogin());
        if (usuarioParaValidar == null || usuarioParaValidar.getSenha() == null || usuarioParaValidar.getSenha().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não foi possível carregar seus dados de autenticação.");
            return;
        }

        String hashBanco = usuarioParaValidar.getSenha();

        boolean ok = SegurancaUtil.verificarSenha(senhaDigitada, hashBanco);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Senha incorreta.");
            return;
        }

        // senha válida → executa exclusão + relatório
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Fornecedor fornecedorManaged = session.get(Fornecedor.class, fornecedor.getId());
            if (fornecedorManaged == null) {
                JOptionPane.showMessageDialog(this, "Fornecedor não encontrado na base de dados.");
                session.getTransaction().rollback();
                return;
            }

            Query<PedidoCompra> query = session.createQuery(
                "FROM PedidoCompra s WHERE s.fornecedor.id = :fornecedorId", PedidoCompra.class
            );
            query.setParameter("fornecedorId", fornecedorManaged.getId());
            List<PedidoCompra> pedidos = query.list();

            File pdf = RelatorioPDF.gerarRelatorioPedidos(fornecedorManaged, pedidos);
            System.out.println("Relatório salvo em: " + pdf.getAbsolutePath());

            for (PedidoCompra s : pedidos) {
                session.delete(s);
            }

            session.delete(fornecedorManaged);
            session.getTransaction().commit();

            JOptionPane.showMessageDialog(this, "Fornecedor e pedidos excluídos com sucesso.");
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao excluir o fornecedor e pedidos.");
        }
    }

    private Usuario buscarUsuarioPorLogin(String login) {
        if (login == null || login.isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> q = session.createQuery(
                "FROM Usuario u WHERE u.login = :login", Usuario.class);
            q.setParameter("login", login);
            return q.uniqueResult();
        }
    }

    private Fornecedor selecionarFornecedor(List<Fornecedor> fornecedores) {
        if (fornecedores == null || fornecedores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum resultado encontrado para: " + cnpjField.getText().trim());
            return null;
        }

        if (fornecedores.size() == 1) {
            Fornecedor fornecedor = fornecedores.get(0);
            cnpjField.setText(fornecedor.getCnpj());
            return fornecedor;
        }

        String[] opcoes = fornecedores.stream()
            .map(f -> f.getCnpj() + " | " + f.getRazaoSocial() + " | " + f.getResponsavel())
            .toArray(String[]::new);

        String selecionado = (String) JOptionPane.showInputDialog(
            this,
            "Selecione o fornecedor:",
            "Resultados encontrados",
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
