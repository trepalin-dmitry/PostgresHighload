package pg.hl.test;

public interface CreatePackageArgument {
    Integer getPackageSize();
    Integer getPackageSizeExists();
    IdentityStrategy getIdentityStrategy();
    EntityType getEntityType();
}
