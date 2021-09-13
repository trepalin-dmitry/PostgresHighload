package pg.hl.test.hb.sequence.one;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "exchangeDealsSequenceOne")
@ToString(onlyExplicitlyIncluded = true)
public class ExchangeDealSequenceOne {
    private static final String SEQUENCE_NAME = "exchange_deals_sequence_one_id_seq";

    /**
     * ID объекта
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @Column(nullable = false)
    @ToString.Include
    private Long id;

    /**
     * GUID объекта
     */
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    @ToString.Include
    private UUID guid;

    /**
     * Счет
     */
    @Column(nullable = false)
    private UUID accountGUId;

    /**
     * Тип сделки
     */
    @Column(nullable = false)
    private String typeCode;

    /**
     * Направление сделки
     */
    @Column(nullable = false)
    private String directionCode;

    /**
     * Площадка
     */
    @Column(nullable = false)
    private String placeCode;

    /**
     * Торговая сессия, в рамках которой подано данное поручение
     */
    @Column(nullable = false)
    private UUID tradeSessionGUId;

    /**
     * Время заключения сделки
     */
    @Column(nullable = false)
    private LocalDateTime dealDateTime;

    /**
     * Поручение
     */
    @Column(nullable = false)
    private UUID orderGUId;

    /**
     * ФИ
     */
    @Column(nullable = false)
    private UUID instrumentGUId;

    /**
     * За валюту
     */
    @Column(nullable = false)
    private UUID currencyGUId;

    /**
     * Количество
     */
    @Column(nullable = false)
    private BigDecimal quantity;

    /**
     * Цена
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * На сумму
     */
    @Column(nullable = false)
    private BigDecimal volume;

    /**
     * Валюта НКД
     */
    @Column(nullable = false)
    private UUID couponCurrencyGUId;

    /**
     * Сумма НКД
     */
    @Column(nullable = false)
    private BigDecimal couponVolume;

    /**
     * Дата поставки инструмента
     */
    @Column(nullable = false)
    private LocalDate planDeliveryDate;

    /**
     * Дата расчетов (оплаты)
     */
    @Column(nullable = false)
    private LocalDate planPaymentDate;

    @OneToMany(mappedBy = "exchangeDeal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @ToString.Exclude
    private List<ExchangeDealPersonSequenceOne> exchangeDealPersons = new ArrayList<>();

    @OneToMany(mappedBy = "exchangeDeal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @ToString.Exclude
    private List<ExchangeDealStatusSequenceOne> exchangeDealStatuses = new ArrayList<>();

    public ExchangeDealSequenceOne addPersonsAll(Collection<ExchangeDealPersonSequenceOne> exchangeDealPersons) {
        addAllInternal(exchangeDealPersons, this.exchangeDealPersons, ExchangeDealPersonSequenceOne::setExchangeDeal);
        return this;
    }

    public ExchangeDealSequenceOne addStatusesAll(Collection<ExchangeDealStatusSequenceOne> exchangeDealStatuses) {
        addAllInternal(exchangeDealStatuses, this.exchangeDealStatuses, ExchangeDealStatusSequenceOne::setExchangeDeal);
        return this;
    }

    protected <T> void addAllInternal(Collection<T> from, Collection<T> to, BiConsumer<T, ExchangeDealSequenceOne> consumer) {
        to.addAll(from);
        for (T item : from) {
            consumer.accept(item, this);
        }
    }
}

