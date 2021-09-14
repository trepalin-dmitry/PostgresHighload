package pg.hl.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class RunArgument {
    @ToString.Exclude
    private final Object dealsPackage;
}
