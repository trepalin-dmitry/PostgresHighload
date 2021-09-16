package pg.hl.test.hb.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Accessors(chain = true)
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "exchangeDealsStatusesTypes")
public class ExchangeDealStatusType {
    @Id
    @Column
    @ToString.Include
    private Character id;

    @Column(unique = true)
    private String code;

    @Column
    private String name;
}

