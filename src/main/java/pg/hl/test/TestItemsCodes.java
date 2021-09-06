package pg.hl.test;

public final class TestItemsCodes {
    public final static String StoredProcedure = "Хранимая процедура";

    public final class Hibernate {
        public final class C3p0 {
            public final static String Each = "Hibernate - C3p0 - Поэлементно";

            public final class Batch {
                public final static String Check = "Hibernate - C3p0 - Пакетно с предварительной проверкой наличия ";
                public final static String NoCheck = "Hibernate - C3p0 - Пакетно с проверкой наличия при исключении";
            }
        }

        public final class Hikari {
            public final static String Each = "Hibernate - Hikari - Поэлементно";

            public final class Batch {
                public final static String Check = "Hibernate - Hikari - Пакетно с предварительной проверкой наличия ";
                public final static String NoCheck = "Hibernate - Hikari - Пакетно с проверкой наличия при исключении";
            }
        }
    }
}
