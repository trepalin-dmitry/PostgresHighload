package pg.hl.test.hb.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "persons")
public class Person {
    private static final String SEQUENCE_NAME = "persons_id_seq";

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
