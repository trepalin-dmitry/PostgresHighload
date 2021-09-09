package pg.hl.test.hb.sequence.one;

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
@Table(name = "exchangeDealsPersonsSequenceOne")
@IdClass(ExchangeDealPersonSequenceOne.ExchangeDealPersonId.class)
@ToString(onlyExplicitlyIncluded = true)
public class ExchangeDealPersonSequenceOne {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchangeDealId", nullable = false)
    @JsonIgnore
    private ExchangeDealSequenceOne exchangeDeal;

    @Id
    @ManyToOne
    @JoinColumn(name = "personId", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("personId")
    @ToString.Include
    private Person person;

    @Column
    private String comment;

    @EqualsAndHashCode
    @ToString
    public static class ExchangeDealPersonId implements Serializable {
        private ExchangeDealSequenceOne exchangeDeal;
        private Person person;

        public ExchangeDealPersonId() {
        }

        @SuppressWarnings("unused")
        public ExchangeDealPersonId(ExchangeDealSequenceOne exchangeDeal, Person person) {
            this();
            this.exchangeDeal = exchangeDeal;
            this.person = person;
        }
    }
}

