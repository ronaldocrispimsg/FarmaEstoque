package br.com.farmaestoque;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.hibernate.Session;

public class CadastroFornecedorFrame extends JFrame {

    private JTextField cnpjField;
    private JTextField razaoSocialField;
    private JTextField segmentoField;
    private JTextField anoField;
    private JTextField clienteField;

    public CadastroFornecedorFrame() {
        setTitle("Cadastrar Fornecedor");
        setSize(340, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel cnpjLabel = new JLabel("CNPJ:");
        cnpjLabel.setBounds(20, 20, 110, 25);
        cnpjField = new JTextField();
        cnpjField.setBounds(130, 20, 170, 25);

        JLabel razaoSocialLabel = new JLabel("Razão Social:");
        razaoSocialLabel.setBounds(20, 60, 110, 25);
        razaoSocialField = new JTextField();
        razaoSocialField.setBounds(130, 60, 170, 25);

        JLabel segmentoLabel = new JLabel("Segmento:");
        segmentoLabel.setBounds(20, 100, 110, 25);
        segmentoField = new JTextField();
        segmentoField.setBounds(130, 100, 170, 25);

        JLabel anoLabel = new JLabel("Ano de Cadastro:");
        anoLabel.setBounds(20, 140, 110, 25);
        anoField = new JTextField();
        anoField.setBounds(130, 140, 170, 25);

        JLabel clienteLabel = new JLabel("Responsável:");
        clienteLabel.setBounds(20, 180, 110, 25);
        clienteField = new JTextField();
        clienteField.setBounds(130, 180, 170, 25);

        JButton cadastrarBtn = new JButton("Cadastrar");
        cadastrarBtn.setBounds(100, 230, 130, 30);

        add(cnpjLabel); add(cnpjField);
        add(razaoSocialLabel); add(razaoSocialField);
        add(segmentoLabel); add(segmentoField);
        add(anoLabel); add(anoField);
        add(clienteLabel); add(clienteField);
        add(cadastrarBtn);

        cadastrarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarFornecedor();
            }
        });

        setVisible(true);
    }

    private void cadastrarFornecedor() {
        String cnpj = cnpjField.getText();
        String razaoSocial = razaoSocialField.getText();
        String segmento = segmentoField.getText();
        String responsavel = clienteField.getText();
        int anoCadastro;

        try {
            anoCadastro = Integer.parseInt(anoField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ano inválido!");
            return;
        }

        Fornecedor fornecedor = new Fornecedor(cnpj, razaoSocial, segmento, anoCadastro, responsavel);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(fornecedor);
            session.getTransaction().commit();
            JOptionPane.showMessageDialog(this, "Fornecedor cadastrado com sucesso!");
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar o fornecedor.");
        }
    }
}
