package pg.hl.test.hb.simple;

import org.hibernate.Session;
import pg.hl.jpa.ExchangeDeal;

import java.io.Closeable;
import java.util.List;

public class ExchangeDealService implements Closeable {
    private final ExchangeDealDao exchangeDealDao;

    public ExchangeDealService(Session session) {
        exchangeDealDao = new ExchangeDealDao(session);
    }

    public void saveOrUpdateExchangeDeal(ExchangeDeal exchangeDeal) {
        exchangeDealDao.saveOrUpdate(exchangeDeal);
    }

    public void deleteExchangeDeal(ExchangeDeal exchangeDeal) {
        exchangeDealDao.delete(exchangeDeal);
    }

    public List<ExchangeDeal> findAllExchangeDeals() {
        return exchangeDealDao.findAll();
    }

    @Override
    public void close() {
        exchangeDealDao.close();
    }
}
