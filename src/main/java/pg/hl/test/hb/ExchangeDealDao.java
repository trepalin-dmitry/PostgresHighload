package pg.hl.test.hb;

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

    protected void saveOrUpdateInternalEach(Collection<ExchangeDeal> exchangeDeals) {
        sessionDoWithTransaction(session -> {
            for (ExchangeDeal deal : exchangeDeals) {
                session.saveOrUpdate(deal);
            }
        });
    }

    public void saveOrUpdate(Collection<ExchangeDeal> exchangeDeals, SaveStrategy useBatchMode) {
        switch (useBatchMode) {
            case BatchHandleException:
                try {
                    saveOrUpdateInternalBatch(exchangeDeals, false);
                } catch (ConstraintViolationException e) {
                    saveOrUpdateInternalBatch(exchangeDeals, true);
                }
                break;
            case BatchCheckExistsBefore:
                saveOrUpdateInternalBatch(exchangeDeals, true);
                break;
            case Each:
                saveOrUpdateInternalEach(exchangeDeals);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + useBatchMode);
        }
    }

    protected void saveOrUpdateInternalBatch(Collection<ExchangeDeal> exchangeDeals, boolean checkExists) throws ConstraintViolationException {
        var existsKeys = new HashSet<UUID>();

        if (checkExists) {
            // Получаем имеющиеся элементы
            var uuids = exchangeDeals.stream().map(ExchangeDeal::getGuid).collect(Collectors.toList());
            Query query = session.createQuery("SELECT e.guid FROM ExchangeDeal e where e.guid in (:guids)").setParameterList("guids", uuids);
            //noinspection unchecked
            var list = (List<UUID>) query.getResultList();
            existsKeys.addAll(list);
        }

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
}
