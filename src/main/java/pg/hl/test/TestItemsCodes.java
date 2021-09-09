package pg.hl.test;

public final class TestItemsCodes {
    public final static String StoredProcedure = "Хранимая процедура";

    public static final class Hibernate {
        public static final class C3p0 {
            public static final class Min {
                public final static String Before = "Hibernate - C3p0 - (5) - C предварительной проверкой наличия ";
                public final static String OnException = "Hibernate - C3p0 - (5) - C проверкой наличия при исключении";
            }

            public static final class Max {
                public final static String Before = "Hibernate - C3p0 - (30) - C предварительной проверкой наличия ";
                public final static String OnException = "Hibernate - C3p0 - (30) - C проверкой наличия при исключении";
            }
        }

        public static final class Hikari {
            public static final class Min {
                public final static String Before = "Hibernate - Hikari - (5) - C предварительной проверкой наличия ";
                public final static String OnException = "Hibernate - Hikari - (5) - C проверкой наличия при исключении";
            }

            public static final class Max {
                public final static String Before = "Hibernate - Hikari - (30) - C предварительной проверкой наличия ";
                public final static String OnException = "Hibernate - Hikari - (30) - C проверкой наличия при исключении";
            }
        }
    }
}
