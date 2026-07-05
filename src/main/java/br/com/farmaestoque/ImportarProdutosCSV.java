package br.com.farmaestoque;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ImportarProdutosCSV {

    public ResultadoImportacao importarProdutos(File file) throws IOException {
        if (file == null) {
            return new ResultadoImportacao(0, 0, List.of());
        }

        String extensao = obterExtensao(file);
        if (!"csv".equals(extensao)) {
            throw new IllegalArgumentException("Formato inválido. Selecione um arquivo .csv.");
        }

        List<String> linhas = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        List<String> erros = new ArrayList<>();

        int importados = 0;
        int ignorados = 0;
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            for (int i = 0; i < linhas.size(); i++) {
                int numeroLinha = i + 1;
                String linha = linhas.get(i).trim();

                if (linha.isEmpty()) {
                    ignorados++;
                    continue;
                }

                if (i == 0 && ehCabecalho(linha)) {
                    continue;
                }

                String[] campos = linha.split(",", -1);
                if (campos.length != 6) {
                    ignorados++;
                    erros.add("Linha " + numeroLinha + ": esperado 6 campos.");
                    continue;
                }

                String nome = campos[0].trim();
                String precoTexto = campos[1].trim();
                String marca = campos[2].trim();
                String principioAtivo = campos[3].trim();
                String descricao = campos[4].trim();
                String quantidadeTexto = campos[5].trim();

                if (nome.isEmpty()) {
                    ignorados++;
                    erros.add("Linha " + numeroLinha + ": nome obrigatório.");
                    continue;
                }

                double preco;
                try {
                    preco = Double.parseDouble(precoTexto);
                } catch (NumberFormatException ex) {
                    ignorados++;
                    erros.add("Linha " + numeroLinha + ": preço decimal inválido.");
                    continue;
                }

                int quantidade;
                try {
                    quantidade = Integer.parseInt(quantidadeTexto);
                } catch (NumberFormatException ex) {
                    ignorados++;
                    erros.add("Linha " + numeroLinha + ": quantidade inteira inválida.");
                    continue;
                }

                Produto produto = new Produto(nome, preco, marca, principioAtivo, descricao, quantidade);
                session.save(produto);
                importados++;
            }

            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }

        return new ResultadoImportacao(importados, ignorados, erros);
    }

    private String obterExtensao(File file) {
        String nome = file.getName();
        int ponto = nome.lastIndexOf('.');
        if (ponto < 0 || ponto == nome.length() - 1) {
            return "";
        }
        return nome.substring(ponto + 1).toLowerCase();
    }

    private boolean ehCabecalho(String linha) {
        return "nome,preco,marca,principioativo,descricao,quantidade"
            .equals(linha.replace(" ", "").toLowerCase());
    }

    public static void main(String[] args) {
        System.out.println("Use a importação pela tela principal do FarmaEstoque.");
    }

    public static class ResultadoImportacao {
        private final int importados;
        private final int ignorados;
        private final List<String> erros;

        public ResultadoImportacao(int importados, int ignorados, List<String> erros) {
            this.importados = importados;
            this.ignorados = ignorados;
            this.erros = erros;
        }

        public int getImportados() {
            return importados;
        }

        public int getIgnorados() {
            return ignorados;
        }

        public List<String> getErros() {
            return erros;
        }
    }
}
