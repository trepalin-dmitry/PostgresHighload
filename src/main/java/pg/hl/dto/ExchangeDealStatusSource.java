package pg.hl.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode()
@ToString
public class ExchangeDealStatusSource {
    private int index;
    private String typeCode;
    private LocalDateTime dateTime;
    private String comment;
}
