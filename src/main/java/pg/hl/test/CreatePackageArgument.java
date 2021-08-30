package pg.hl.test;

import lombok.Getter;

@Getter
public class CreatePackageArgument {
    private final int size;
    private final int personsSize;
    private final int statusesSize;

    public CreatePackageArgument(int size, int personsSize, int statusesSize) {
        this.size = size;
        this.personsSize = personsSize;
        this.statusesSize = statusesSize;
    }
}
