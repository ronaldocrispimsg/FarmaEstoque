package br.com.farmaestoque;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioPDF {

    public static File gerarRelatorioPedidos(Fornecedor fornecedor, List<PedidoCompra> pedidos) {
        Document document = new Document();
        File file = new File("relatorio_pedidos_" + fornecedor.getCnpj() + ".pdf");

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font textoFont = new Font(Font.FontFamily.HELVETICA, 12);

            Paragraph titulo = new Paragraph("Relatório de Pedidos - FarmaEstoque", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph dadosFornecedor = new Paragraph(
                    "Responsável: " + fornecedor.getResponsavel() + "\n" +
                    "CNPJ: " + fornecedor.getCnpj() + "\n" +
                    "Razão Social: " + fornecedor.getRazaoSocial() + "\n" +
                    "Segmento: " + fornecedor.getSegmento() + "\n" +
                    "Ano: " + fornecedor.getAnoCadastro(), textoFont);
            dadosFornecedor.setSpacingAfter(15);
            document.add(dadosFornecedor);

            PdfPTable tabela = new PdfPTable(4);
            tabela.setWidthPercentage(100);

            tabela.addCell(new PdfPCell(new Phrase("Descrição", textoFont)));
            tabela.addCell(new PdfPCell(new Phrase("Valor (R$)", textoFont)));
            tabela.addCell(new PdfPCell(new Phrase("Data", textoFont)));
            tabela.addCell(new PdfPCell(new Phrase("Nº lote/pedido", textoFont)));

            double total = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (PedidoCompra s : pedidos) {
                tabela.addCell(new PdfPCell(new Phrase(s.getDescricao(), textoFont)));
                tabela.addCell(new PdfPCell(new Phrase(String.format("%.2f", s.getValor()), textoFont)));
                tabela.addCell(new PdfPCell(new Phrase(s.getData().format(formatter), textoFont)));
                tabela.addCell(new PdfPCell(new Phrase(String.valueOf(s.getNumeroLote()), textoFont)));
                total += s.getValor();
            }

            document.add(tabela);

            Paragraph totalParagrafo = new Paragraph("\nTOTAL: R$ " + String.format("%.2f", total), textoFont);
            totalParagrafo.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagrafo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return file;
    }
} 
