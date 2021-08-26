package pg.hl.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "exchangeDealsPersons")
@IdClass(ExchangeDealPersonId.class)
public class ExchangeDealPerson {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchangeDealId", columnDefinition="uuid")
    private ExchangeDeal exchangeDeal;

    @Id
    @Column(columnDefinition = "uuid")
    private UUID personGUId;

    @Column
    private String comment;
}