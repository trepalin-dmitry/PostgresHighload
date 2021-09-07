package pg.hl.test.hb;

import org.hibernate.HibernateException;

public interface SaveConsumer<T> {
    void accept(SaveConsumerArgument<T> argument) throws HibernateException;
}

