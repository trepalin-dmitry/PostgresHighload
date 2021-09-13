package pg.hl.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ToString
public abstract class AbstractDataPackage<T extends AbstractDataObject> implements DataPackage<T> {
    @JsonIgnore
    private final List<T> listObjects = new ArrayList<>();

    public AbstractDataPackage() {
    }

    @JsonProperty("objects")
    public Collection<T> getObjects() {
        return this.getListObjects();
    }

    @JsonIgnore
    public T get(int index) {
        return this.getListObjects().get(index);
    }

    @JsonIgnore
    public int size() {
        return this.getListObjects().size();
    }

    protected List<T> getListObjects() {
        return this.listObjects;
    }
}
