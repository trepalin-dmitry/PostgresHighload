package pg.hl.test.hb.jpa;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
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

