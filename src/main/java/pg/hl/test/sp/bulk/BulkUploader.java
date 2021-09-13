package pg.hl.test.sp.bulk;

import de.bytefish.pgbulkinsert.PgBulkInsert;
import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class BulkUploader<TDealSource, TDealPersonSource, TDealPerson, TDealStatusSource, TDealStatus> {
    private final PgBulkInsert<TDealSource> pgBulkInsertExchangeDealSourceExtended;
    private final PgBulkInsert<TDealPerson> pgBulkInsertExchangeDealPersonSourceExtended;
    private final PgBulkInsert<TDealStatus> pgBulkInsertExchangeDealStatusSourceExtended;

    public BulkUploader() {
        this.pgBulkInsertExchangeDealSourceExtended = new PgBulkInsert<>(createDealMapping());
        this.pgBulkInsertExchangeDealPersonSourceExtended = new PgBulkInsert<>(createDealPersonMapping());
        this.pgBulkInsertExchangeDealStatusSourceExtended = new PgBulkInsert<>(createDealStatusMapping());
    }

    public void upload(PgConnection pgConnection, Collection<TDealSource> collection) throws SQLException {
        pgBulkInsertExchangeDealSourceExtended.saveAll(pgConnection, collection);
        pgBulkInsertExchangeDealPersonSourceExtended.saveAll(pgConnection, extendChildrenCollection(collection, this::getPersons, this::extendDealPerson));
        pgBulkInsertExchangeDealStatusSourceExtended.saveAll(pgConnection, extendChildrenCollection(collection, this::getStatuses, this::extendDealStatus));
    }

    protected abstract TDealPerson extendDealPerson(TDealPersonSource personSource, TDealSource deal);

    protected abstract TDealStatus extendDealStatus(TDealStatusSource statusSource, TDealSource deal);

    protected abstract AbstractMapping<TDealSource> createDealMapping();

    protected abstract AbstractMapping<TDealPerson> createDealPersonMapping();

    protected abstract AbstractMapping<TDealStatus> createDealStatusMapping();

    protected abstract Collection<TDealPersonSource> getPersons(TDealSource deal);

    protected abstract Collection<TDealStatusSource> getStatuses(TDealSource deal);

    protected <TEntity, TExt, TResultItem> Stream<TResultItem> extendChildrenCollection(Collection<TExt> collection, Function<TExt, Collection<TEntity>> getChildren, BiFunction<TEntity, TExt, TResultItem> extend) {
        return collection
                .stream()
                .flatMap(f -> getChildren
                        .apply(f)
                        .stream()
                        .map(m -> extend.apply(m, f))
                );
    }
}
