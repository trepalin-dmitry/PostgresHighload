package pg.hl.test.hb.common;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

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

