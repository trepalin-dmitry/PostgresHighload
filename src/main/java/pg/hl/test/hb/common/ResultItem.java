package pg.hl.test.hb.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.openjdk.jmh.annotations.Mode;
import pg.hl.test.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "resultsItems")
@ToString(onlyExplicitlyIncluded = true)
public class ResultItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Setter(value = AccessLevel.NONE)
    @ToString.Include
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resultId", nullable = false)
    private Result result;

    @Column(nullable = false)
    private String benchmark;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Mode mode;

    @Column(nullable = false)
    private Long sampleCount;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private Double scoreError;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TestItemKind testItemKind;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ConnectionPoolType connectionPoolType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResolveStrategy resolveStrategy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IdentityStrategy identityStrategy;

    @Column(nullable = false)
    private Integer packageSize;

    @Column(nullable = false)
    private Integer packageSizeExists;
}
