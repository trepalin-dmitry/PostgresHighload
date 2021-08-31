package pg.hl.test.hb;

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

    @Override
    public void close() {
        exchangeDealDao.close();
    }

    public List<ExchangeDeal> find(long size){
        return exchangeDealDao.find(size);
    }
}
