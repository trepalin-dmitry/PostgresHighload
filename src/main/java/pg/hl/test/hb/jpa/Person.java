package pg.hl.test.hb.jpa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Setter(value = AccessLevel.NONE)
    private Integer id;

    @Column(columnDefinition = "uuid", unique = true)
    private UUID guid;

    @Column
    private String name;
}
