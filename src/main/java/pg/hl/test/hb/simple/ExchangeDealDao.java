package pg.hl.test.hb.simple;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pg.hl.jpa.ExchangeDeal;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public  class ExchangeDealDao implements Closeable {
    private final Session session;

    public ExchangeDealDao(Session session) {
        this.session = session;
    }

    @Override
    public void close() {
        session.close();
    }

    private void sessionDoWithTransaction(Consumer<Session> consumer) {
        Transaction transaction = session.beginTransaction();
        consumer.accept(session);
        transaction.commit();
    }

    public List<ExchangeDeal> findAll() {
        var users = new ArrayList<ExchangeDeal>();
        for (Object o : session.createQuery("From ExchangeDeal").list()) {
            users.add((ExchangeDeal) o);
        }
        return users;
    }

    public void saveOrUpdate(ExchangeDeal exchangeDeal) {
        sessionDoWithTransaction(session -> session.saveOrUpdate(exchangeDeal));
    }

    public void delete(ExchangeDeal exchangeDeal) {
        sessionDoWithTransaction(session -> session.delete(exchangeDeal));
    }
}
