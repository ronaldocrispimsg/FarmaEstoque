package br.com.farmaestoque;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.time.LocalDate;

public class FornecedorService {

    public static List<Fornecedor> listarFornecedores() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Fornecedor> query = session.createQuery("FROM Fornecedor WHERE ativo = true", Fornecedor.class);
            List<Fornecedor> fornecedores = query.list();

            session.getTransaction().commit();

            if (fornecedores.isEmpty()) {
                System.out.println("Nenhum fornecedor encontrado.");
            } else {
                System.out.println("Lista de fornecedores:");
                for (Fornecedor fornecedor : fornecedores) {
                    System.out.println(
                        "ID: " + fornecedor.getId() +
                        ", Responsável: " + fornecedor.getResponsavel() +
                        ", CNPJ: " + fornecedor.getCnpj() +
                        ", Razão Social: " + fornecedor.getRazaoSocial() +
                        ", Segmento: " + fornecedor.getSegmento() +
                        ", Ano: " + fornecedor.getAnoCadastro()
                    );
                }
            }

            return fornecedores;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static void salvarFornecedor(String cnpj, String razaoSocial, String segmento, int anoCadastro, String responsavel) {
        Fornecedor fornecedor = new Fornecedor(cnpj, razaoSocial, segmento, anoCadastro, responsavel);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(fornecedor);
            session.getTransaction().commit();
            System.out.println("Fornecedor salvo com sucesso: " + cnpj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void atualizarFornecedor(Long id, String novoCnpj, String novaRazaoSocial, String novoSegmento, int novoAnoCadastro) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Fornecedor fornecedor = session.get(Fornecedor.class, id);
            if (fornecedor != null) {
                fornecedor.setCnpj(novoCnpj);
                fornecedor.setRazaoSocial(novaRazaoSocial);
                fornecedor.setSegmento(novoSegmento);
                fornecedor.setAnoCadastro(novoAnoCadastro);

                session.update(fornecedor);
                session.getTransaction().commit();
                System.out.println("Fornecedor atualizado com sucesso!");
            } else {
                System.out.println("Fornecedor com ID " + id + " não encontrado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removerFornecedor(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Fornecedor fornecedor = session.get(Fornecedor.class, id);
            if (fornecedor != null) {
                fornecedor.setAtivo(false); // Remoção lógica
                session.update(fornecedor);
                session.getTransaction().commit();
                System.out.println("Fornecedor removido com sucesso (removido logicamente).");
            } else {
                System.out.println("Fornecedor com ID " + id + " não encontrado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Fornecedor buscarFornecedorPorCnpj(String cnpj) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Fornecedor> query = session.createQuery(
                "FROM Fornecedor WHERE lower(cnpj) = :cnpj AND ativo = true", Fornecedor.class);
            query.setParameter("cnpj", cnpj.trim().toLowerCase());
            Fornecedor fornecedor = query.uniqueResult();

            session.getTransaction().commit();

            if (fornecedor != null) {
                System.out.println("Fornecedor encontrado: " + fornecedor.getSegmento() + " (" + fornecedor.getCnpj() + ")");
            } else {
                System.out.println("Nenhum fornecedor ativo encontrado com o CNPJ: " + cnpj);
            }

            return fornecedor;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Fornecedor> buscarFornecedoresPorResponsavel(String nomeResponsavel) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Fornecedor> query = session.createQuery(
                "FROM Fornecedor f WHERE f.ativo = true AND (" +
                "lower(f.responsavel) LIKE :termo OR " +
                "lower(f.razaoSocial) LIKE :termo OR " +
                "lower(f.cnpj) LIKE :termo OR " +
                "lower(f.segmento) LIKE :termo)",
                Fornecedor.class);
            query.setParameter("termo", "%" + nomeResponsavel.trim().toLowerCase() + "%");

            List<Fornecedor> fornecedores = query.list();
            session.getTransaction().commit();

            return fornecedores;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static List<Fornecedor> buscarFornecedoresPorTermo(String termoBusca) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<Fornecedor> query = session.createQuery(
                "FROM Fornecedor f WHERE f.ativo = true AND (" +
                "lower(f.responsavel) LIKE :termo OR " +
                "lower(f.razaoSocial) LIKE :termo OR " +
                "lower(f.cnpj) LIKE :termo OR " +
                "lower(f.segmento) LIKE :termo)",
                Fornecedor.class);
            query.setParameter("termo", "%" + termoBusca.trim().toLowerCase() + "%");

            List<Fornecedor> fornecedores = query.list();
            session.getTransaction().commit();
            return fornecedores;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static String buscarPedidosPorData(LocalDate data) {
        StringBuilder resultado = new StringBuilder();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Query<PedidoCompra> query = session.createQuery(
                "FROM PedidoCompra WHERE data = :data", PedidoCompra.class);
            query.setParameter("data", data);

            List<PedidoCompra> pedidos = query.list();
            session.getTransaction().commit();

            if (pedidos.isEmpty()) {
                resultado.append("Nenhum pedido encontrado para a data: ").append(data.toString());
            } else {
                resultado.append("Pedidos encontrados para ").append(data.toString()).append(":\n\n");
                for (PedidoCompra s : pedidos) {
                    resultado.append("Fornecedor: ").append(s.getFornecedor().getCnpj())
                             .append("\nDescrição: ").append(s.getDescricao())
                             .append("\nValor: R$ ").append(String.format("%.2f", s.getValor()))
                             .append("\nNúmero do lote/pedido: ").append(s.getNumeroLote())
                             .append("\n\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultado.append("Erro ao buscar pedidos.");
        }

        return resultado.toString();
    }
}
