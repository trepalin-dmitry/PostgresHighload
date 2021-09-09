package pg.hl.test.hb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import pg.hl.test.CreateTestItemArgument;

@Getter
@AllArgsConstructor
@ToString
public class CreateHibernateTestItemArgument {
    private CreateTestItemArgument parentArgument;
    private ConnectionPoolType connectionPoolType;
    private BatchSize saveStrategy;
    private CheckExistsStrategy checkExistsStrategy;
}
