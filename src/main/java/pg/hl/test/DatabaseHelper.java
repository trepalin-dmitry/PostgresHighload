package pg.hl.test;

import pg.hl.test.hb.common.ExchangeDealStatusType;
import pg.hl.test.hb.common.ExchangeDealType;
import pg.hl.test.hb.common.Person;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public interface DatabaseHelper {
    Collection<UUID> findDealsGUIds(int dealsSize);
    Collection<ExchangeDealType> findDealsTypes(int maxValue);
    Collection<Person> findPersons(int maxValue);
    Collection<ExchangeDealStatusType> findStatusesTypes(int maxValue);
    <TypeSource> void save(Collection<TypeSource> sourceItems);
    void close() throws SQLException;
}
