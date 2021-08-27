package pg.hl.jpa;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@EqualsAndHashCode
public class ExchangeDealStatusId implements Serializable {
    private ExchangeDeal exchangeDeal;
    private int index;

    public ExchangeDealStatusId() {
    }

    public ExchangeDealStatusId(ExchangeDeal exchangeDeal, int index) {
        this();
        this.exchangeDeal = exchangeDeal;
        this.index = index;
    }
}
