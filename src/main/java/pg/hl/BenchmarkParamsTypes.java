package pg.hl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pg.hl.test.*;
import pg.hl.test.hb.common.ResultItem;

import java.util.function.BiConsumer;
import java.util.function.Function;

@AllArgsConstructor
public enum BenchmarkParamsTypes {
    TEST_ITEM_KIND("testItemKind", new Setter<>(TestItemKind::valueOf, ResultItem::setTestItemKind)),
    CONNECTION_POOL_TYPE("connectionPoolType", new Setter<>(ConnectionPoolType::valueOf, ResultItem::setConnectionPoolType)),
    ENTITY_TYPE("entityType", new Setter<>(EntityType::valueOf, ResultItem::setEntityType)),
    RESOLVE_STRATEGY("resolveStrategy", new Setter<>(ResolveStrategy::valueOf, ResultItem::setResolveStrategy)),
    IDENTITY_STRATEGY("identityStrategy", new Setter<>(IdentityStrategy::valueOf, ResultItem::setIdentityStrategy)),
    PACKAGE_SIZE("packageSize", new Setter<>(Integer::valueOf, ResultItem::setPackageSize)),
    PACKAGE_SIZE_EXISTS("packageSizeExists", new Setter<>(Integer::valueOf, ResultItem::setPackageSizeExists));

    @Getter
    private String fieldName;
    private AbstractSetter setter;

    private static abstract class AbstractSetter {
        public abstract void setResultItemValue(ResultItem resultItem, String value);
    }

    @AllArgsConstructor
    @Getter
    private static class Setter<T> extends AbstractSetter {
        private Function<String, T> parse;
        private BiConsumer<ResultItem, T> set;

        @Override
        public void setResultItemValue(ResultItem resultItem, String value) {
            set.accept(resultItem, parse.apply(value));
        }
    }

    public void setResultItemValue(ResultItem resultItem, String value){
        setter.setResultItemValue(resultItem, value);
    }
}