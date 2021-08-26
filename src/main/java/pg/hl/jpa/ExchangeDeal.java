package pg.hl.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
}
