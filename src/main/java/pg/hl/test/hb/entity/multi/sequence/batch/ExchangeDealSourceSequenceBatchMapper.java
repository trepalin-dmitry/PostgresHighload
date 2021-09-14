package pg.hl.test.hb.entity.multi.sequence.batch;

import pg.hl.dto.multi.ExchangeDealPersonSource;
import pg.hl.dto.multi.ExchangeDealSource;
import pg.hl.dto.multi.ExchangeDealStatusSource;
import pg.hl.test.hb.HibernateResolver;
import pg.hl.test.hb.HibernateTestItemMapper;

public class ExchangeDealSourceSequenceBatchMapper extends HibernateTestItemMapper<ExchangeDealSource, ExchangeDealSequenceBatch> {
    public ExchangeDealSourceSequenceBatchMapper(HibernateResolver testItem) {
        super(testItem);
    }

    @Override
    protected ExchangeDealSequenceBatch parse(ExchangeDealSource source) {
        return new ExchangeDealSequenceBatch().setGuid(source.getGuid())
                .setAccountGUId(source.getAccountGUId())
                .setType(getResolver().resolveDealType(source.getTypeCode()))
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

    private ExchangeDealStatusSequenceBatch parse(ExchangeDealStatusSource source) {
        return new ExchangeDealStatusSequenceBatch()
                .setComment(source.getComment())
                .setDateTime(source.getDateTime())
                .setIndex(source.getIndex())
                .setType(getResolver().resolveStatusType(source.getTypeCode()));
    }

    private ExchangeDealPersonSequenceBatch parse(ExchangeDealPersonSource source) {
        return new ExchangeDealPersonSequenceBatch()
                .setComment(source.getComment())
                .setPerson(getResolver().resolvePerson(source.getPersonGUId()));
    }
}