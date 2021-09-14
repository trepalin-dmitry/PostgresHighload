package pg.hl.test.sp.ei.simple;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class SimpleExchangeDealInternal {
    /**
     * GUID объекта
     */
    private UUID guid;

    /**
     * Счет
     */
    private UUID accountGUId;

    /**
     * Тип сделки
     */
    private Integer typeId;

    /**
     * Направление сделки
     */
    private String directionCode;

    /**
     * Площадка
     */
    private String placeCode;

    /**
     * Торговая сессия, в рамках которой подано данное поручение
     */
    private UUID tradeSessionGUId;

    /**
     * Время заключения сделки
     */
    private LocalDateTime dealDateTime;

    /**
     * Поручение
     */
    private UUID orderGUId;

    /**
     * ФИ
     */
    private UUID instrumentGUId;

    /**
     * За валюту
     */
    private UUID currencyGUId;

    /**
     * Количество
     */
    private BigDecimal quantity;

    /**
     * Цена
     */
    private BigDecimal price;

    /**
     * На сумму
     */
    private BigDecimal volume;

    /**
     * Валюта НКД
     */
    private UUID couponCurrencyGUId;

    /**
     * Сумма НКД
     */
    private BigDecimal couponVolume;

    /**
     * Дата поставки инструмента
     */
    private LocalDate planDeliveryDate;

    /**
     * Дата расчетов (оплаты)
     */
    private LocalDate planPaymentDate;
}