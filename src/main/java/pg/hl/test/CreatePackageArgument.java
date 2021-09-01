package pg.hl.test;

import com.mchange.util.AssertException;
import lombok.Getter;

@Getter
public class CreatePackageArgument {
    private final int size;
    private final int sizeExists;
    private final int personsSize;
    private final int statusesSize;

    public CreatePackageArgument(int size, int sizeExists, int personsSize, int statusesSize) {
        this.size = size;
        this.sizeExists = sizeExists;
        this.personsSize = personsSize;
        this.statusesSize = statusesSize;

        if (sizeExists > size){
            throw new AssertException("sizeExists > size");
        }
    }
}
