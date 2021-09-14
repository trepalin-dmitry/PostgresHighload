package pg.hl.test.ei;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class ExchangeDealStatusInternal {
    private Integer index;
    private Character typeId;
    private LocalDateTime dateTime;
    private String comment;
}
