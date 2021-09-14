package pg.hl.dto.multi;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode()
@ToString
public class ExchangeDealPersonSource {
    private UUID personGUId;
    private String comment;
}


