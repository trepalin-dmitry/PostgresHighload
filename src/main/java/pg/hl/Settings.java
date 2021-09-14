package pg.hl;

import pg.hl.test.hb.Hbm2DdlAuto;

public final class Settings {
    public final static class ConnectionSettings {
        public final static String JDBC_URL = "jdbc:postgresql://localhost:5432/postgresHighLoad";
        public final static String USER = "postgres";
        public final static String PASSWORD = "postgres";
    }

    public final static class Hibernate{
        public final static Hbm2DdlAuto HBM_2_DDL_AUTO = Hbm2DdlAuto.Validate;
        public final static Boolean SHOW_SQL = false;
    }

    public static final class BenchmarkConstants {
        public static boolean Test = true;

        public final static int FORK_WARMUPS = 0;
        public final static int FORK_VALUE = 1;
        public final static int WARMUP_ITERATIONS = 0;
        public final static int MEASUREMENT_ITERATIONS = 1;

        public static final Integer EXCHANGE_DEALS_PERSONS_SIZE = 5;
        public static final Integer EXCHANGE_DEALS_STATUSES_SIZE = 5;
    }
}
