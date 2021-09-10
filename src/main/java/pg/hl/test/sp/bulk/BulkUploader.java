package pg.hl.test.sp.bulk;

import de.bytefish.pgbulkinsert.PgBulkInsert;
import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import de.bytefish.pgbulkinsert.util.PostgreSqlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.postgresql.PGConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BulkUploader<TDealSource, TDeal, TDealPersonSource, TDealPerson, TDealStatusSource, TDealStatus> {
    private final PGConnection pgConnection;
    private final PgBulkInsert<TDeal> pgBulkInsertExchangeDealSourceExtended;
    private final PgBulkInsert<TDealPerson> pgBulkInsertExchangeDealPersonSourceExtended;
    private final PgBulkInsert<TDealStatus> pgBulkInsertExchangeDealStatusSourceExtended;

    public BulkUploader(Connection connection) {
        this.pgConnection = PostgreSqlUtils.getPGConnection(connection);
        this.pgBulkInsertExchangeDealSourceExtended = new PgBulkInsert<>(createDealMapping());
        this.pgBulkInsertExchangeDealPersonSourceExtended = new PgBulkInsert<>(createDealPersonMapping());
        this.pgBulkInsertExchangeDealStatusSourceExtended = new PgBulkInsert<>(createDealStatusMapping());
    }

    public UploadResult upload(Collection<TDealSource> collection) throws SQLException {
        UUID uploadKey = UUID.randomUUID();

        var targetCollection = extendCollection(collection, m -> extendDeal(m, uploadKey));

        pgBulkInsertExchangeDealSourceExtended.saveAll(this.pgConnection, targetCollection);
        pgBulkInsertExchangeDealPersonSourceExtended.saveAll(this.pgConnection, extendChildrenCollection(targetCollection, this::getPersons, this::extendDealPerson));
        pgBulkInsertExchangeDealStatusSourceExtended.saveAll(this.pgConnection, extendChildrenCollection(targetCollection, this::getStatuses, this::extendDealStatus));


        return new UploadResult(uploadKey);
    }

    protected abstract TDeal extendDeal(TDealSource dealSource, UUID uuid);

    protected abstract TDealPerson extendDealPerson(TDealPersonSource personSource, TDeal deal);

    protected abstract TDealStatus extendDealStatus(TDealStatusSource statusSource, TDeal deal);

    protected abstract AbstractMapping<TDeal> createDealMapping();

    protected abstract AbstractMapping<TDealPerson> createDealPersonMapping();

    protected abstract AbstractMapping<TDealStatus> createDealStatusMapping();

    protected abstract Collection<TDealPersonSource> getPersons(TDeal deal);

    protected abstract Collection<TDealStatusSource> getStatuses(TDeal deal);

    protected <TEntity, TResultItem> Collection<TResultItem> extendCollection(Collection<TEntity> collection, Function<TEntity, TResultItem> extend) {
        return collection
                .stream()
                .map(extend)
                .collect(Collectors.toList());
    }

    protected <TEntity, TExt, TResultItem> Stream<TResultItem> extendChildrenCollection(Collection<TExt> collection, Function<TExt, Collection<TEntity>> getChildren, BiFunction<TEntity, TExt, TResultItem> extend) {
        return collection
                .stream()
                .flatMap(f -> getChildren
                        .apply(f)
                        .stream()
                        .map(m -> extend.apply(m, f))
                );
    }

    @Getter
    @AllArgsConstructor
    public static class UploadResult {
        private UUID uploadKey;
    }
}
