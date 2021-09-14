package pg.hl.test.edc;

import pg.hl.test.DatabaseHelper;
import pg.hl.test.hb.common.Person;

import java.util.Collection;
import java.util.UUID;

public class PersonsExistsDataItem extends ExistsDataItem<Person, Integer, UUID, Object> {

    public PersonsExistsDataItem(DatabaseHelper defaultTestItem, int size) {
        super(defaultTestItem, size, Person.class, UUID.class);
    }

    @Override
    protected UUID[] createCodesArray(int size) {
        return new UUID[size];
    }

    @Override
    protected Collection<Person> getSource(DatabaseHelper databaseHelper) {
        return databaseHelper.findPersons(Integer.MAX_VALUE);
    }

    @Override
    protected Integer getId(Person person) {
        return person.getId();
    }

    @Override
    protected UUID getCode(Person person) {
        return person.getGuid();
    }

    @Override
    protected Object createInitArgument() {
        return null;
    }

    @Override
    protected void modifyNew(Person person, Object o) {
    }
}

