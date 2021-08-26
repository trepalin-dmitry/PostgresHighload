package pg.hl.test.hb.simple;

import pg.hl.jpa.ExchangeDeal;

import java.io.Closeable;
import java.util.List;

public class ExchangeDealService implements Closeable {
    private final ExchangeDealDao exchangeDealDao;

    public ExchangeDealService() {
        exchangeDealDao = new ExchangeDealDao();
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
