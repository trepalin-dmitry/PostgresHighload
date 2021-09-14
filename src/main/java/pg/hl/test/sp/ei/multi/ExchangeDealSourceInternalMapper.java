package pg.hl.test.sp.ei.multi;

import pg.hl.dto.multi.ExchangeDealPersonSource;
import pg.hl.dto.multi.ExchangeDealSource;
import pg.hl.dto.multi.ExchangeDealStatusSource;
import pg.hl.dto.multi.ExchangeDealsPackage;
import pg.hl.test.EntityType;
import pg.hl.test.IdentityStrategy;
import pg.hl.test.sp.ei.AbstractMapper;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExchangeDealSourceInternalMapper extends AbstractMapper<ExchangeDealsPackage, ExchangeDealSource, ExchangeDealInternal> {
    public ExchangeDealSourceInternalMapper(IdentityStrategy identityStrategy, EntityType entityType) throws SQLException, PropertyVetoException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(identityStrategy, entityType);
    }

    @Override
    protected ExchangeDealInternal parse(ExchangeDealSource source) {
        return new ExchangeDealInternal()
                .setGuid(source.getGuid())
                .setAccountGUId(source.getAccountGUId())
                .setTypeId(getExistsDataController().getDealsTypes().resolve(source.getTypeCode()))
                .setDirectionCode(source.getDirectionCode())
                .setPlaceCode(source.getPlaceCode())
                .setTradeSessionGUId(source.getTradeSessionGUId())
                .setDealDateTime(source.getDealDateTime())
                .setOrderGUId(source.getOrderGUId())
                .setInstrumentGUId(source.getInstrumentGUId())
                .setCurrencyGUId(source.getCurrencyGUId())
                .setQuantity(source.getQuantity())
                .setPrice(source.getPrice())
                .setCouponCurrencyGUId(source.getCouponCurrencyGUId())
                .setCouponVolume(source.getCouponVolume())
                .setPlanDeliveryDate(source.getPlanDeliveryDate())
                .setPlanPaymentDate(source.getPlanPaymentDate())
                .setVolume(source.getVolume())
                .addPersonsAll(parse(source.getPersons(), this::parse))
                .addStatusesAll(parse(source.getStatuses(), this::parse));
    }

    protected  <TypeSource, TypeTarget> Collection<TypeTarget> parse(Collection<TypeSource> source, Function<TypeSource, TypeTarget> function) {
        return source.stream().map(function).collect(Collectors.toList());
    }

    private ExchangeDealStatusInternal parse(ExchangeDealStatusSource source) {
        return new ExchangeDealStatusInternal()
                .setComment(source.getComment())
                .setDateTime(source.getDateTime())
                .setIndex(source.getIndex())
                .setTypeId(getExistsDataController().getStatusesTypes().resolve(source.getTypeCode()));
    }

    private ExchangeDealPersonInternal parse(ExchangeDealPersonSource source) {
        return new ExchangeDealPersonInternal()
                .setComment(source.getComment())
                .setPersonId(getExistsDataController().getPersons().resolve(source.getPersonGUId()));
    }
}

