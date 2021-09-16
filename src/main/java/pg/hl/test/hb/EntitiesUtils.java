package pg.hl.test.hb;

import java.util.Collection;
import java.util.function.BiConsumer;

public class EntitiesUtils {
    public static <TParent, TItem> void addAll(TParent parent, Collection<TItem> from, Collection<TItem> to, BiConsumer<TItem, TParent> consumer) {
        to.addAll(from);
        for (TItem item : from) {
            consumer.accept(item, parent);
        }
    }
}