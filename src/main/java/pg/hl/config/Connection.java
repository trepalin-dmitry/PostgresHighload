package pg.hl.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Connection {
    private String jdbcUrl;
    private String user;
    private String password;
}

