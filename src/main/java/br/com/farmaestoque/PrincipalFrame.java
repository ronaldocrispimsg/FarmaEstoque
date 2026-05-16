package br.com.farmaestoque;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class PrincipalFrame extends JFrame {

    public PrincipalFrame() {
        setTitle("FarmaEstoque - Sistema de Estoque");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new JLabel("FarmaEstoque", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel painel = new JPanel(new GridLayout(2, 2, 8, 8));
        painel.add(new JButton("Produtos"));
        painel.add(new JButton("Fornecedores"));
        painel.add(new JButton("Estoque"));
        painel.add(new JButton("Sair"));
        add(painel, BorderLayout.CENTER);

        setVisible(true);
    }
}
