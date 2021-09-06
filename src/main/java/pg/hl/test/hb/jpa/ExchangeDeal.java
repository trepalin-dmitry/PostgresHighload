package pg.hl.test.hb.jpa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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
@Table(name = "exchangeDeals")
public class ExchangeDeal {
    /**
     * ID объекта
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Setter(value = AccessLevel.NONE)
    private Long id;

    /**
     * GUID объекта
     */
    @Column(columnDefinition = "uuid", unique = true, nullable = false)
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
    private List<ExchangeDealPerson> exchangeDealPersons = new ArrayList<>();

    @OneToMany(mappedBy = "exchangeDeal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private List<ExchangeDealStatus> exchangeDealStatuses = new ArrayList<>();

    public ExchangeDeal addPersonsAll(Collection<ExchangeDealPerson> exchangeDealPersons) {
        addAllInternal(exchangeDealPersons, this.exchangeDealPersons, ExchangeDealPerson::setExchangeDeal);
        return this;
    }

    public ExchangeDeal addStatusesAll(Collection<ExchangeDealStatus> exchangeDealStatuses) {
        addAllInternal(exchangeDealStatuses, this.exchangeDealStatuses, ExchangeDealStatus::setExchangeDeal);
        return this;
    }

    protected <T> void addAllInternal(Collection<T> from, Collection<T> to, BiConsumer<T, ExchangeDeal> consumer){
        to.addAll(from);
        for (T item : from) {
            consumer.accept(item, this);
        }
    }
}
