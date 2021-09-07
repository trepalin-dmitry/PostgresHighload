package pg.hl.test.hb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.test.CreateTestItemArgument;

@Getter
@AllArgsConstructor
public class CreateHibernateTestItemArgument {
    private CreateTestItemArgument parentArgument;
    private ConnectionPoolType connectionPoolType;
    private SaveStrategy saveStrategy;
    private CheckExistsStrategy checkExistsStrategy;
}
