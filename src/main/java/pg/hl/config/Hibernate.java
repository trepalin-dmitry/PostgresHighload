package pg.hl.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pg.hl.test.hb.Hbm2DdlAuto;

@Getter
@Setter
@ToString
public class Hibernate {
    private Hbm2DdlAuto hbm2DdlAuto;
    private Boolean showSql;
}
