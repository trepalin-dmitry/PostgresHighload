package pg.hl.test.hb.identity;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import pg.hl.test.hb.common.ExchangeDealStatusType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "exchangeDealsStatusesIdentity")
@IdClass(ExchangeDealStatusIdentity.ExchangeDealStatusId.class)
@ToString(onlyExplicitlyIncluded = true)
public class ExchangeDealStatusIdentity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchangeDealId", nullable = false)
    @JsonIgnore
    private ExchangeDealIdentity exchangeDeal;

    @Id
    @Column(nullable = false)
    @ToString.Include
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
        private ExchangeDealIdentity exchangeDeal;
        private int index;

        public ExchangeDealStatusId() {
        }

        @SuppressWarnings("unused")
        public ExchangeDealStatusId(ExchangeDealIdentity exchangeDeal, int index) {
            this();
            this.exchangeDeal = exchangeDeal;
            this.index = index;
        }
    }
}


