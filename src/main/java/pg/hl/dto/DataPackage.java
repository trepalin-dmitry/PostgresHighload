package pg.hl.dto;

import java.util.Collection;

public interface DataPackage<T extends DataObject> {
    Collection<T> getObjects();

    T get(int var1);

    int size();
}
