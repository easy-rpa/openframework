package eu.easyrpa.openframework.calendar.entity;

import eu.ibagroup.easyrpa.persistence.annotation.Column;
import eu.ibagroup.easyrpa.persistence.annotation.Entity;
import eu.ibagroup.easyrpa.persistence.annotation.EntityType;
import eu.ibagroup.easyrpa.persistence.annotation.Id;
import eu.ibagroup.easyrpa.persistence.impl.adaptor.EnumAdaptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;

/**
 * Class that represents a holiday day.
 */
@Entity(value = "crud_example_holidays", type = EntityType.DATASTORE)
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class HolidayEntity {


    private String dsName;

    public HolidayEntity(String dsName, String region, HolidayType type, int month, int day,
                         String description, boolean isChurchHoliday, boolean isCustomHoliday, String churchHolidayType, int validFrom, int validTo, boolean isSubstitute) {
        this.dsName = dsName;
        this.region = region;
        this.type = type;
        this.month = month;
        this.day = day;
        this.description = description;
        this.isChurchHoliday = isChurchHoliday;
        this.isCustomHoliday = isCustomHoliday;
        this.churchHolidayType = churchHolidayType;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isSubstitute = isSubstitute;
    }

    /**
     * Unique identifier of a holiday day.
     */
    @Id
    @Column(Column.CS_ID)
    private Long id;            // ---1---

    /**
     * Region where a holiday day is held.
     */
    @Column("country")
    private String region;         // ---BY---

    /**
     * Type of holiday day.
     * <p>
     * Can be "FIXED" or "MOVING".
     */
    @Column(value = "type", adapter = HolidayTypeAdaptor.class)
    private HolidayType type;// ---Fixed---


    /**
     * Special column where the moving date is defined.
     * <p>
     *  For example "3 FRIDAY MAY"
     */
    @Column("dateOfMovingHoliday")
    private String dateOfMovingHoliday;

    /**
     * Number of month of a holiday day.
     */
    @Column("month")
    private int month;          // ---12---

    /**
     * Day of month if a holiday day.
     */
    @Column("day")
    private int day;            // ---31---

    /**
     * Description of a holiday day. Usually it is an official name of a holiday day.
     */
    @Column("description")
    private String description;     // ---New Year---

    /**
     * True if the holiday day is a church holiday. False otherwise.
     */
    @Column("isChurchHoliday")
    private boolean isChurchHoliday;  // ---false---

    /**
     * True if the holiday day is custom(not an official government holiday). False otherwise.
     */
    @Column("isCustomHoliday")
    private boolean isCustomHoliday;

    /**
     * Type of the church holiday.
     */
    @Column("churchHolidayType")
    private String churchHolidayType;     // ---null---

    /**
     * Year value from which the holiday is valid.
     */
    @Column("validFrom")
    private int validFrom;          // ---null (every year)---

    /**
     * Year value to which the holiday is valid.
     */
    @Column("validTo")
    private int validTo;            // ---2024---

    /**
     * True if the holiday day will be substituted because if different terms. False otherwise.
     */
    @Column("isSubstitute")
    private boolean isSubstitute;

    /**
     * Enum class which describes type of the holiday day.
     * <p>
     * "FIXED" is for holiday days that have a fixed date.
     * "MOVING" is for holiday days that have a moving date(3rd thursday of may for example).
     */
    public enum HolidayType {
       FIXED, MOVING
    }

    /**
     * Static class which is used to adapt the usage of HolidayType object in DataStore.
     */
    public static class HolidayTypeAdaptor extends EnumAdaptor<HolidayType> {
        public HolidayType adaptString(Field f, String s) {
            return s != null && !s.isEmpty() ? HolidayType.valueOf(s) : null;
        }
    }

    public String entityName() {
        return dsName;
    }

    public EntityType entityType() {
        return EntityType.DATASTORE;
    }
}
