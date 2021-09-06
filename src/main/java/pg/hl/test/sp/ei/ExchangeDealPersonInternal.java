package pg.hl.test.sp.ei;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import pg.hl.test.hb.jpa.ExchangeDeal;

@Getter
@Setter
@Accessors(chain = true)
public class ExchangeDealPersonInternal {
    private ExchangeDeal exchangeDeal;
    private Integer personId;
    private String comment;
}
