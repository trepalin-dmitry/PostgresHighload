package pg.hl.test.hb.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Setter(value = AccessLevel.NONE)
    @ToString.Include
    private Integer id;

    @Column(columnDefinition = "uuid", unique = true)
    @ToString.Include
    private UUID guid;

    @Column
    private String name;
}
