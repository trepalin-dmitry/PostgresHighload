package pg.hl.test.hb.jpa;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "exchangeDealsPersons")
@IdClass(ExchangeDealPerson.ExchangeDealPersonId.class)
public class ExchangeDealPerson {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchangeDealId", nullable = false)
    @JsonIgnore
    private ExchangeDeal exchangeDeal;

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
        private ExchangeDeal exchangeDeal;
        private Person person;

        public ExchangeDealPersonId(){
        }

        @SuppressWarnings("unused")
        public ExchangeDealPersonId(ExchangeDeal exchangeDeal, Person person) {
            this();
            this.exchangeDeal = exchangeDeal;
            this.person = person;
        }
    }
}