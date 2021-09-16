package pg.hl.test.hb.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import pg.hl.test.hb.EntitiesUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Accessors(chain = true)
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Setter(value = AccessLevel.NONE)
    @ToString.Include
    private int id;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime finishDateTime;

    @Column(nullable = false, length = 10485760)
    private String configuration;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @ToString.Exclude
    private List<ResultItem> items = new ArrayList<>();

    public Result addItemsAll(Collection<ResultItem> items) {
        EntitiesUtils.addAll(this, items, this.items, ResultItem::setResult);
        return this;
    }
}
