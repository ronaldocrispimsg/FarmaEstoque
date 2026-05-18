
package br.com.farmaestoque;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class PedidoCompraService {

    public static List<PedidoCompra> listarPedidos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<PedidoCompra> query = session.createQuery("FROM PedidoCompra", PedidoCompra.class);
            List<PedidoCompra> pedidos = query.list();
            session.getTransaction().commit();
            return pedidos;
        }
    }

    public static void limparPedidos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM PedidoCompra").executeUpdate();
            session.getTransaction().commit();
        }
    }

    public static List<PedidoCompra> buscarPedidosPorData(LocalDate data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<PedidoCompra> query = session.createQuery("FROM PedidoCompra WHERE data = :data", PedidoCompra.class);
            query.setParameter("data", data);
            List<PedidoCompra> resultado = query.list();
            session.getTransaction().commit();
            return resultado;
        }
    }
    
    public static void limparHistorico() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        session.beginTransaction();
        session.createQuery("DELETE FROM PedidoCompra").executeUpdate();
        session.getTransaction().commit();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
    