package pg.hl.test.hb.jpa;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "exchangeDealsStatuses")
@IdClass(ExchangeDealStatus.ExchangeDealStatusId.class)
public class ExchangeDealStatus {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchangeDealId", nullable = false)
    @JsonIgnore
    private ExchangeDeal exchangeDeal;

    @Id
    @Column(nullable = false)
    private Integer index;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "typeId", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("typeId")
    private ExchangeDealStatusType type;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column
    private String comment;

    @EqualsAndHashCode
    public static class ExchangeDealStatusId implements Serializable {
        private ExchangeDeal exchangeDeal;
        private int index;

        public ExchangeDealStatusId() {
        }

        @SuppressWarnings("unused")
        public ExchangeDealStatusId(ExchangeDeal exchangeDeal, int index) {
            this();
            this.exchangeDeal = exchangeDeal;
            this.index = index;
        }
    }
}


