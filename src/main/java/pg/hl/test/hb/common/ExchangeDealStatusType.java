package pg.hl.test.hb.common;

import lombok.Getter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Accessors(chain = true)
@Table(name = "exchangeDealsStatusesTypes")
public class ExchangeDealStatusType {
    @Id
    @Column
    private Character id;

    @Column(unique = true)
    private String code;

    @Column
    private String name;
}

