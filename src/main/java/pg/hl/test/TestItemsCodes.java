package pg.hl.test;

public final class TestItemsCodes {
    public final static String StoredProcedure = "Хранимая процедура";

    public final class Hibernate {
        public final class C3p0 {
            public final class Each {
                public final static String Before = "Hibernate - C3p0 - Поэлементно - C предварительной проверкой наличия ";
                public final static String OnException = "Hibernate - C3p0 - Поэлементно - C проверкой наличия при исключении";
            }

            public final class Batch {
                public final static String Before = "Hibernate - C3p0 - Пакетно с предварительной проверкой наличия ";
                public final static String OnException = "Hibernate - C3p0 - Пакетно с проверкой наличия при исключении";
            }
        }

        public final class Hikari {
            public final class Each {
                public final static String Before = "Hibernate - Hikari - Поэлементно - C предварительной проверкой наличия ";
                public final static String OnException = "Hibernate - Hikari - Поэлементно - C проверкой наличия при исключении";
            }

            public final class Batch {
                public final static String Before = "Hibernate - Hikari - Пакетно с предварительной проверкой наличия ";
                public final static String OnException = "Hibernate - Hikari - Пакетно с проверкой наличия при исключении";
            }
        }
    }
}
