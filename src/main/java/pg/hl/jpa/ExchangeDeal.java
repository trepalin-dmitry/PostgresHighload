package pg.hl.jpa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "exchangeDeals")
public class ExchangeDeal {
    /**
     * GUID объекта
     */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID guid;

    /**
     * Счет
     */
    @Column
    @Getter
    @Setter
    private UUID accountGUId;

    /**
     * Тип сделки
     */
    @Column
    private String typeCode;

    /**
     * Направление сделки
     */
    @Column
    private String directionCode;

    /**
     * Площадка
     */
    @Column
    private String placeCode;

    /**
     * Торговая сессия, в рамках которой подано данное поручение
     */
    @Column
    private UUID tradeSessionGUId;

    /**
     * Время заключения сделки
     */
    @Column
    private LocalDateTime dealDateTime;

    /**
     * Поручение
     */
    @Column
    private UUID orderGUId;

    /**
     * ФИ
     */
    @Column
    private UUID instrumentGUId;

    /**
     * За валюту
     */
    @Column
    private UUID currencyGUId;

    /**
     * Количество
     */
    @Column
    private BigDecimal quantity;

    /**
     * Цена
     */
    @Column
    private BigDecimal price;

    /**
     * На сумму
     */
    @Column
    private BigDecimal volume;

    /**
     * Валюта НКД
     */
    @Column
    private UUID couponCurrencyGUId;

    /**
     * Сумма НКД
     */
    @Column
    private BigDecimal couponVolume;

    /**
     * Дата поставки инструмента
     */
    @Column
    private LocalDate planDeliveryDate;

    /**
     * Дата расчетов (оплаты)
     */
    @Column
    private LocalDate planPaymentDate;

    @OneToMany(mappedBy = "exchangeDeal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private List<ExchangeDealPerson> exchangeDealPersons = new ArrayList<>();

    @OneToMany(mappedBy = "exchangeDeal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private List<ExchangeDealStatus> exchangeDealStatuses = new ArrayList<>();

    public void addPersonsAll(Collection<ExchangeDealPerson> exchangeDealPersons) {
        addAllInternal(exchangeDealPersons, this.exchangeDealPersons, ExchangeDealPerson::setExchangeDeal);
    }

    public void addStatusesAll(Collection<ExchangeDealStatus> exchangeDealStatuses) {
        addAllInternal(exchangeDealStatuses, this.exchangeDealStatuses, ExchangeDealStatus::setExchangeDeal);
    }

    protected <T> void addAllInternal(Collection<T> from, Collection<T> to, BiConsumer<T, ExchangeDeal> consumer){
        to.addAll(from);
        for (T item : from) {
            consumer.accept(item, this);
        }
    }
}
