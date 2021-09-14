package pg.hl.test.sp.ei.multi;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ExchangeDealPersonInternal {
    private Integer personId;
    private String comment;
}
