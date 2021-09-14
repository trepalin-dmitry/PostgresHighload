package pg.hl.test;

public interface CreatePackageArgument {
    Integer getPackageSize();
    Integer getPackageSizeExists();
    Integer getPersonsSize();
    Integer getStatusesSize();
    IdentityStrategy getIdentityStrategy();
    EntityType getEntityType();
}
