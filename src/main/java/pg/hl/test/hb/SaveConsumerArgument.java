package pg.hl.test.hb;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class SaveConsumerArgument<T> {
    private Collection<T> items;
    private Boolean checkExist;
}
