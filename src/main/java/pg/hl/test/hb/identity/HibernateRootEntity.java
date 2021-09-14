package pg.hl.test.hb.identity;

import java.util.UUID;

public interface HibernateRootEntity {
    Long getId();

    void setId(Long id);

    UUID getGuid();
}
