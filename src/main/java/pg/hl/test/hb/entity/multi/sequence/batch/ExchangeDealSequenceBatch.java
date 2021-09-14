package pg.hl.test.hb.entity.multi.sequence.batch;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import pg.hl.test.hb.common.ExchangeDealType;
import pg.hl.test.hb.HibernateRootEntity;

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
@Table(name = "exchangeDealsSequenceBatch")
@ToString(onlyExplicitlyIncluded = true)
public class ExchangeDealSequenceBatch implements HibernateRootEntity {
    private static final String SEQUENCE_NAME = "exchange_deals_sequence_batch_id_seq";

    /**
     * ID объекта
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 100)
    @Column(nullable = false)
    @ToString.Include
    @Accessors
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
    @ManyToOne
    @JoinColumn(name = "typeId", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("typeId")
    @ToString.Include
    private ExchangeDealType type;

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
    private List<ExchangeDealPersonSequenceBatch> exchangeDealPersons = new ArrayList<>();

    @OneToMany(mappedBy = "exchangeDeal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @ToString.Exclude
    private List<ExchangeDealStatusSequenceBatch> exchangeDealStatuses = new ArrayList<>();

    public ExchangeDealSequenceBatch addPersonsAll(Collection<ExchangeDealPersonSequenceBatch> exchangeDealPersons) {
        addAllInternal(exchangeDealPersons, this.exchangeDealPersons, ExchangeDealPersonSequenceBatch::setExchangeDeal);
        return this;
    }

    public ExchangeDealSequenceBatch addStatusesAll(Collection<ExchangeDealStatusSequenceBatch> exchangeDealStatuses) {
        addAllInternal(exchangeDealStatuses, this.exchangeDealStatuses, ExchangeDealStatusSequenceBatch::setExchangeDeal);
        return this;
    }

    protected <T> void addAllInternal(Collection<T> from, Collection<T> to, BiConsumer<T, ExchangeDealSequenceBatch> consumer) {
        to.addAll(from);
        for (T item : from) {
            consumer.accept(item, this);
        }
    }
}

