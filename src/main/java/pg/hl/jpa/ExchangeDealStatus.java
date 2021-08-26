package pg.hl.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "exchangeDealsStatuses")
@IdClass(ExchangeDealStatusId.class)
public class ExchangeDealStatus {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchangeDealId", columnDefinition = "uuid")
    private ExchangeDeal exchangeDeal;

    @Id
    @Column()
    private int index;

    @Column()
    private String statusTypeCode;

    @Column()
    private LocalDateTime statusDateTime;

    @Column()
    private String comment;
}
