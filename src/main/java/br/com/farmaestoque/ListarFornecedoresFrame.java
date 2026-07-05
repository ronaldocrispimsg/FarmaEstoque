package br.com.farmaestoque;

import javax.swing.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.awt.*;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class ListarFornecedoresFrame extends JFrame {

    public ListarFornecedoresFrame() {
        setTitle("Lista de Fornecedores Cadastrados");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Fornecedores no Sistema", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(titulo, BorderLayout.NORTH);

        JPanel painelLista = new JPanel();
        painelLista.setLayout(new BoxLayout(painelLista, BoxLayout.Y_AXIS));
        painelLista.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(painelLista);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Detalhes dos Fornecedores"));
        add(scrollPane, BorderLayout.CENTER);

        List<Fornecedor> fornecedores = FornecedorService.listarFornecedores();

        // Ordena os fornecedores pelo segmento, ignorando maiúsculas/minúsculas
        Collections.sort(fornecedores, Comparator.comparing(c -> c.getSegmento().toLowerCase()));

        if (fornecedores.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum fornecedor encontrado.");
            vazio.setFont(new Font("SansSerif", Font.ITALIC, 16));
            painelLista.add(vazio);
        } else {
            for (Fornecedor fornecedor : fornecedores) {
                String info = "<html>"
                            + "<b>CNPJ:</b> " + fornecedor.getCnpj() + " | "
                            + "<b>Segmento:</b> " + fornecedor.getSegmento() + " | "
                            + "<b>Razão Social:</b> " + fornecedor.getRazaoSocial() + " | "
                            + "<b>Responsável:</b> " + fornecedor.getResponsavel()
                            + "</html>";
                JLabel label = new JLabel(info);
                label.setFont(new Font("SansSerif", Font.PLAIN, 14));
                label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                painelLista.add(label);
            }
        }

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ListarFornecedoresFrame::new);
    }
}
