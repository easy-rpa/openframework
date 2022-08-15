package eu.easyrpa.openframework.calendar.entity;

import eu.ibagroup.easyrpa.persistence.annotation.Column;
import eu.ibagroup.easyrpa.persistence.annotation.Entity;
import eu.ibagroup.easyrpa.persistence.annotation.EntityType;
import eu.ibagroup.easyrpa.persistence.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(value = "")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class HolidayEntity {

    private String dsName;

    public HolidayEntity(String dsName, String region, String type, int month, int day, String description,
                         boolean isChurchHoliday, String churchHolidayType, int validFrom, int validTo) {
        this.dsName = dsName;
        this.region = region;
        this.type = type;
        this.month = month;
        this.day = day;
        this.description = description;
        this.isChurchHoliday = isChurchHoliday;
        this.churchHolidayType = churchHolidayType;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    @Id
    @Column(Column.CS_ID)
    private Long id;            // ---1---

    @Column("country")
    private String region;         // ---BY---

    @Column("type")
    private String type;            // ---Fixed---

    @Column("month")
    private int month;          // ---12---

    @Column("day")
    private int day;            // ---31---

    @Column("description")
    private String description;     // ---New Year---

    @Column("isChurchHoliday")
    private boolean isChurchHoliday;    // ---false---

    @Column("churchHolidayType")
    private String churchHolidayType;     // ---null---

    @Column("validFrom")
    private int validFrom;          // ---null (every year)---

    @Column("validTo")
    private int validTo;            // ---2024---

    public String entityName() {
        return dsName;
    }

    public EntityType entityType() {
        return EntityType.DATASTORE;
    }
}
