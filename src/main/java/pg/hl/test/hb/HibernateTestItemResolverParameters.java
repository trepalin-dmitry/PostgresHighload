package pg.hl.test.hb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Function;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public abstract class HibernateTestItemResolverParameters<TEntity, TKeyInternal, TKeyExternal> {
    private String hqlAll;
    private String hqlByKeyExternal;
    private Function<Object, TEntity> parseObjectFunction;
    private Function<TEntity, TKeyExternal> getKeyExternalFunction;
    private String hqlByKeyExternalParameterName;
}
