package pg.hl.test;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum TestItemKind {
    StoredProcedureJson("Хранимая процедура - JSON"),
    StoredProcedureBulk("Хранимая процедура - PgBulkInsert"),
    HibernateMinBefore("Hibernate - (Пакет 5 шт.) - Предварительная проверка"),
    HibernateMinOnException("Hibernate - (Пакет 5 шт.) - Проверка при исключении"),
    HibernateMaxBefore("Hibernate - (Пакет 30 шт.) - Предварительная проверка"),
    HibernateMaxOnException("Hibernate - (Пакет 30 шт.) - Проверка при исключении");

    private String description;
}
