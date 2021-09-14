package pg.hl.test.hb;

import java.util.UUID;

public interface HibernateRootEntity {
    Long getId();

    void setId(Long id);

    UUID getGuid();
}
