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
    private BatchSize batchSize;
    private CheckExistsStrategy checkExistsStrategy;
}
