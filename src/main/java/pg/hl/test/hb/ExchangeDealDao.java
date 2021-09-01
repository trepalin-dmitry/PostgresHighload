package pg.hl.test.hb;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import pg.hl.jpa.ExchangeDeal;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.io.Closeable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExchangeDealDao implements Closeable {
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
        try {
            consumer.accept(session);
            transaction.commit();
        } catch (Exception exception) {
            transaction.rollback();
            throw exception;
        }
    }

    public List<ExchangeDeal> find(long size) {
        var users = new ArrayList<ExchangeDeal>();
        for (Object o : session.createQuery("From ExchangeDeal").list()) {
            users.add((ExchangeDeal) o);
            if (users.size() >= size) {
                break;
            }
        }
        return users;
    }

    public void saveOrUpdate(ExchangeDeal exchangeDeal) {
        sessionDoWithTransaction(session -> session.saveOrUpdate(exchangeDeal));
    }

    public void saveOrUpdate(Collection<ExchangeDeal> exchangeDeals) {
        try {
            saveOrUpdateInternal(exchangeDeals, new HashSet<>());
        } catch (ConstraintViolationException e) {
            // Получаем имеющиеся элементы
            var uuids = exchangeDeals.stream().map(ExchangeDeal::getGuid).collect(Collectors.toList());
            Query query = session.createQuery("SELECT e.guid FROM ExchangeDeal e where e.guid in (:guids)").setParameterList("guids", uuids);
            //noinspection unchecked
            var list = (List<UUID>)query.getResultList();
            var set = new HashSet<>(list);
            saveOrUpdateInternal(exchangeDeals, set);
        }
    }

    public void saveOrUpdateInternal(Collection<ExchangeDeal> exchangeDeals, Set<UUID> existsKeys) throws ConstraintViolationException {
        var reference = new AtomicReference<ConstraintViolationException>();

        sessionDoWithTransaction(session -> {
            for (ExchangeDeal exchangeDeal : exchangeDeals) {
                if (existsKeys.contains(exchangeDeal.getGuid())) {
                    session.update(exchangeDeal);
                } else {
                    session.persist(exchangeDeal);
                }
            }

            try {
                session.flush();
            } catch (PersistenceException persistenceException) {
                var cause = persistenceException.getCause();
                if (cause instanceof ConstraintViolationException) {
                    reference.set((ConstraintViolationException) cause);
                }
            }
        });

        var exception = reference.get();
        if (exception != null) {
            throw exception;
        }
    }

    @Getter
    @Setter
    private static class GuidKey{
        public UUID guid;
    }
}
