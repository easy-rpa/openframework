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
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;

/**
 * Class that represents a holiday day.
 */
@Entity(value = "")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class HolidayEntity {


    private String dsName = "";

    public HolidayEntity(String dsName, String region, HolidayType type, String dateOfFloatingHoliday, int month, int day,
                         String description, boolean isChurchHoliday, boolean isCustomHoliday, ChurchHolidayType churchHolidayType, int validFrom, int validTo, boolean isSubstitute) {
        this.dsName = dsName;
        this.region = region;
        this.type = type;
        this.dateOfFloatingHoliday = dateOfFloatingHoliday;
        this.month = month;
        this.day = day;
        this.description = description;
        this.isChurchHoliday = isChurchHoliday;
        this.isCustomHoliday = isCustomHoliday;
        this.churchHolidayType = churchHolidayType;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isSubstitute = isSubstitute;
        generateId();
    }

    /**
     * Unique identifier of a holiday day.
     */
    @Id
    @Column("id")
    private String id;

    /**
     * Region where a holiday day is held.
     */
    @Column("country")
    private String region;

    /**
     * Type of holiday day.
     * <p>
     * Can be "FIXED" or "FLOATING".
     */
    @Column(value = "holiday_type", adapter = HolidayTypeAdaptor.class)
    private HolidayType type;

    /**
     * Special column where the moving date is defined.
     * <p>
     * For example "3 FRIDAY MAY"
     */
    @Column("date_of_floating_holiday")
    private String dateOfFloatingHoliday;

    /**
     * Number of month of a holiday day.
     */
    @Column("month")
    private int month;

    /**
     * Day of month if a holiday day.
     */
    @Column("day")
    private int day;

    /**
     * Description of a holiday day. Usually it is an official name of a holiday day.
     */
    @Column("description")
    private String description;

    /**
     * True if the holiday day is a church holiday. False otherwise.
     */
    @Column("is_church_holiday")
    private boolean isChurchHoliday;

    /**
     * True if the holiday day is custom(not an official government holiday). False otherwise.
     */
    @Column("is_custom_holiday")
    private boolean isCustomHoliday;

    /**
     * Type of the church holiday.
     */
    @Column(value = "church_holiday_type", adapter = ChurchHolidayTypeAdaptor.class)
    private ChurchHolidayType churchHolidayType;

    @Column(value = "days_from_easter")
    private int daysFromEaster;

    /**
     * Year value from which the holiday is valid.
     */
    @Column("valid_from")
    private int validFrom;

    /**
     * Year value to which the holiday is valid.
     */
    @Column("valid_to")
    private int validTo;

    /**
     * True if the holiday day will be substituted because if different terms. False otherwise.
     */
    @Column("is_substitute")
    private boolean isSubstitute;

    public void updateEntityValue() {
        Entity annotation = this.getClass().getAnnotation(Entity.class);
        final String key = "value";
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        f.setAccessible(true);
        Map<String, Object> memberValues;
        try {
            memberValues = (Map<String, Object>) f.get(handler);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        memberValues.put(key, dsName);
    }

    public void generateId() {
        if (id == null) {
            this.id = dsName + "_" + UUID.randomUUID();
        }
        updateEntityValue();
    }

    /**
     * Enum class which describes type of the holiday day.
     * <p>
     * "FIXED" is for holiday days that have a fixed date.
     * "MOVING" is for holiday days that have a moving date(3rd thursday of may for example).
     */
    public enum HolidayType {
        FIXED, FLOATING
    }

    /**
     * Enum class which describes type of the church holiday day.
     *
     */
    public enum ChurchHolidayType {
        ORTHODOX, CATHOLIC, ISLAMIC, NONE
    }

    /**
     * Static class which is used to adapt the usage of HolidayType field in DataStore.
     */
    public static class HolidayTypeAdaptor extends EnumAdaptor<HolidayType> {
        public HolidayType adaptString(Field f, String s) {
            return s != null && !s.isEmpty() ? HolidayType.valueOf(s) : null;
        }
    }

    /**
     * Static class which is used to adapt the usage of ChurchHolidayType field in DataStore.
     */
    public static class ChurchHolidayTypeAdaptor extends EnumAdaptor<ChurchHolidayType> {
        public ChurchHolidayType adaptString(Field f, String s) {
            return s != null && !s.isEmpty() ? ChurchHolidayType.valueOf(s) : null;
        }
    }

    public String entityName() {
        return dsName;
    }

    public EntityType entityType() {
        return EntityType.DATASTORE;
    }
}


