package pg.hl.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class AbstractDataObjectWithGuidAndRevision extends AbstractDataObject {
    /**
     * GUID объекта
     */
    private UUID guid;

    /**
     * Версия объекта
     */
    private int revision = 0;
}