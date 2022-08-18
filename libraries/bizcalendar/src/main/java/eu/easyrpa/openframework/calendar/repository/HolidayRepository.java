package eu.easyrpa.openframework.calendar.repository;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.ibagroup.easyrpa.persistence.CrudRepository;
import eu.ibagroup.easyrpa.persistence.EntityManager;
import eu.ibagroup.easyrpa.persistence.TypedQuery;

import java.util.*;

public interface HolidayRepository extends CrudRepository<HolidayEntity, Integer> {
    default HolidayEntity save_(HolidayEntity entity) {
        HolidayEntity e = getEntityManager().persist(entity);
        entity.setId(e.getId());
        return e;
    }

    default HolidayEntity findById_(String dsName, String id) {
        TypedQuery<HolidayEntity> q = getEntityManager().createQuery("select t from \"" + dsName + "\" t where t.id::text = :id", HolidayEntity.class)
                .withParam("id", id);
        List<HolidayEntity> entities = q.execute();

        HolidayEntity e = entities.stream().findFirst().orElse(null);
        if (e != null) {
            e.setDsName(dsName);
        }
        return e;
    }

    default List<HolidayEntity> findAllById_(String dsName, Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, String> params = new HashMap<>();
        int i = 0;
        StringBuilder where = new StringBuilder("where");
        for (String id : ids) {
            String key = "id" + i++;
            where.append(" t.").append("t.id::text").append("=").append(":").append(key);
            params.put(key, String.valueOf(id));
            if (i < ids.size()) {
                where.append(" or ");
            }
        }
        TypedQuery<HolidayEntity> createQuery = getEntityManager().createQuery("select t from " + dsName + " t " + where, HolidayEntity.class);
        for (Map.Entry<String, String> param : params.entrySet()) {
            createQuery.withParam(param.getKey(), param.getValue());
        }
        List<HolidayEntity> result = createQuery.execute();
        result.forEach(e -> e.setDsName(dsName));
        return result;

    }

    default List<HolidayEntity> findAll_(String dsName) {
        TypedQuery<HolidayEntity> tq = getEntityManager().createQuery("select t from " + dsName + " t", HolidayEntity.class);
        List<HolidayEntity> result = tq.execute();
        result.forEach(e -> e.setDsName(dsName));
        return result;
    }

    default void delete(HolidayEntity entity) {

    }

    //TODO: have to be checked
    default List<HolidayEntity> findAllOtherHolidays_(String dsName){
        TypedQuery<HolidayEntity> tq = getEntityManager().createQuery("select t from " + dsName + " t " + "where t.isCustomHoliday =: true", HolidayEntity.class);
        List<HolidayEntity> result = tq.execute();
        result.forEach(e -> e.setDsName(dsName));
        return result;
    }

    //TODO: have to be checked
    default List<HolidayEntity> findAllPublicHolidays_(String dsName){
        TypedQuery<HolidayEntity> tq = getEntityManager().createQuery("select t from " + dsName + " t " + "where t.isCustomHoliday =: false", HolidayEntity.class);
        List<HolidayEntity> result = tq.execute();
        result.forEach(e -> e.setDsName(dsName));
        return result;
    }


    EntityManager getEntityManager();
}
