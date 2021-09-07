package pg.hl.test.hb.sequence;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import pg.hl.test.hb.common.Person;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "exchangeDealsPersonsSequence")
@IdClass(ExchangeDealPersonSequence.ExchangeDealPersonId.class)
public class ExchangeDealPersonSequence {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchangeDealId", nullable = false)
    @JsonIgnore
    private ExchangeDealSequence exchangeDeal;

    @Id
    @ManyToOne
    @JoinColumn(name = "personId", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("personId")
    private Person person;

    @Column
    private String comment;

    @EqualsAndHashCode
    @ToString
    public static class ExchangeDealPersonId implements Serializable {
        private ExchangeDealSequence exchangeDeal;
        private Person person;

        public ExchangeDealPersonId() {
        }

        @SuppressWarnings("unused")
        public ExchangeDealPersonId(ExchangeDealSequence exchangeDeal, Person person) {
            this();
            this.exchangeDeal = exchangeDeal;
            this.person = person;
        }
    }
}

