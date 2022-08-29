package eu.easyrpa.openframework.calendar.repository;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.ibagroup.easyrpa.persistence.CrudRepository;
import eu.ibagroup.easyrpa.persistence.annotation.Param;
import eu.ibagroup.easyrpa.persistence.annotation.Query;

import java.util.List;

public interface CalendarRepo extends CrudRepository<HolidayEntity, Integer> {

    @Query("select b from crud_example_holidays b where b.region = :region")
    public List<HolidayEntity> findAllHolidaysOfRegion(@Param("region") String region);

    @Query("select b from crud_example_holidays b where b.region = :region and b.isCustomHoliday = :true")
    public List<HolidayEntity> findAllOtherHolidays(@Param("region") String region);

    @Query("select b from crud_example_holidays b where b.region = :region and b.isCustomHoliday = :false")
    public List<HolidayEntity> findAllPublicHolidays(@Param("region") String region);

    @Query("select b from crud_example_holidays b where b.region = :region and b.type = :MOVING")
    public List<HolidayEntity> findAllMovingHolidays(@Param("region") String region);

}
