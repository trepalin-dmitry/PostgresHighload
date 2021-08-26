package pg.hl.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(property = "guid", generator = ObjectIdGenerators.PropertyGenerator.class)
public class ExchangeDealSource extends AbstractDataObjectWithGuidAndRevision {
    /**
     * Счет
     */
    private UUID accountGUId;

    /**
     * Тип сделки
     */
    private String typeCode;

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