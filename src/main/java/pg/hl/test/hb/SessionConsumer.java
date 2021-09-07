package pg.hl.test.hb;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface SessionConsumer {
    void accept(Session session) throws HibernateException;
}
