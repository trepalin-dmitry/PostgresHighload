package pg.hl.jpa;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@EqualsAndHashCode
public class ExchangeDealPersonId implements Serializable {
    private ExchangeDeal exchangeDeal;
    private UUID personGUId;

    public ExchangeDealPersonId(){
    }

    public ExchangeDealPersonId(ExchangeDeal exchangeDeal, UUID personGUId) {
        this();
        this.exchangeDeal = exchangeDeal;
        this.personGUId = personGUId;
    }
}

