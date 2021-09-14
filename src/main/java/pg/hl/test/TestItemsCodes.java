package pg.hl.test;

public final class TestItemsCodes {
    public final static class StoredProcedure {
        public final static String Json = "Хранимая процедура - JSON";
        public final static String Bulk = "Хранимая процедура - PgBulkInsert";
    }

    public static final class Hibernate {
        public static final class Min {
            public final static String Before = "Hibernate - (Пакет 5 шт.) - C предварительной проверкой наличия ";
            public final static String OnException = "Hibernate - (Пакет 5 шт.) - C проверкой наличия при исключении";
        }

        public static final class Max {
            public final static String Before = "Hibernate - (Пакет 30 шт.) - C предварительной проверкой наличия ";
            public final static String OnException = "Hibernate - (Пакет 30 шт.) - C проверкой наличия при исключении";
        }
    }
}
