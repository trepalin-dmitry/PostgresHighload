package pg.hl.test;

import lombok.Getter;
import lombok.ToString;
import pg.hl.DevException;

@Getter
@ToString
public class CreatePackageArgument {
    private final int size;
    private final int sizeExists;
    private final int personsSize;
    private final int statusesSize;
    private final IdentityStrategy identityStrategy;

    public CreatePackageArgument(int size, int sizeExists, int personsSize, int statusesSize, IdentityStrategy identityStrategy) {
        this.size = size;
        this.sizeExists = sizeExists;
        this.personsSize = personsSize;
        this.statusesSize = statusesSize;
        this.identityStrategy = identityStrategy;

        if (sizeExists > size){
            throw new DevException("sizeExists > size");
        }
    }
}
