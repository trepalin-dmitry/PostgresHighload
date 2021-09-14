package pg.hl.test.hb.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Accessors(chain = true)
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "exchangeDealsTypes")
public class ExchangeDealType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Setter(value = AccessLevel.NONE)
    @ToString.Include
    private Integer id;

    @Column(unique = true)
    private String code;

    @Column
    private String name;
}
