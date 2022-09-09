package eu.easyrpa.openframework.calendar.repository;

import eu.easyrpa.openframework.calendar.entity.HolidayEntity;
import eu.ibagroup.easyrpa.persistence.CrudRepository;
import eu.ibagroup.easyrpa.persistence.EntityManager;
import eu.ibagroup.easyrpa.persistence.TypedQuery;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Used to simplify and organize DB related operations.
 * <p>
 * Repository provides basic operations like saving or deleting an object as well as custom query creation via @Query
 * annotation.
 */
public interface HolidayRepository extends CrudRepository<HolidayEntity, Integer> {

    //checked
    default HolidayEntity save_(HolidayEntity entity) {
        entity.generateId();
        HolidayEntity e = getEntityManager().persist(entity);
        entity.setId(e.getId());
        entity.updateEntityValue();
        return e;
    }


    //checked
    default HolidayEntity findById_(String dsName, String id) {
        TypedQuery<HolidayEntity> q = getEntityManager().createQuery("select t from \"" + dsName + "\" t where t.id::text = :id", HolidayEntity.class)
                .withParam("id", id);

        HolidayEntity en = new HolidayEntity();
        en.setDsName(dsName);
        en.updateEntityValue();
        getEntityManager().registerEntity(en);
        List<HolidayEntity> entities = q.execute();

        HolidayEntity e = entities.stream().findFirst().orElse(null);
        if (e != null) {
            e.setDsName(dsName);
        }
        return e;
    }

    //checked
    default List<HolidayEntity> findAllById_(String dsName, Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        ids = ids.stream().map(id -> {
            if (!id.contains(dsName)) {
                return dsName + "_" + id;
            }
            return id;
        }).collect(Collectors.toList());

        Map<String, String> params = new HashMap<>();
        int i = 0;
        StringBuilder where = new StringBuilder("where");
        for (String id : ids) {
            String key = "id" + i++;
            where.append(" t.").append("t.id").append("=").append(":").append(key);
            params.put(key, String.valueOf(id));
            if (i < ids.size()) {
                where.append(" or ");
            }
        }
        TypedQuery<HolidayEntity> createQuery = getEntityManager().createQuery("select t from \"" + dsName + "\" t " + where, HolidayEntity.class);
        for (Map.Entry<String, String> param : params.entrySet()) {
            createQuery.withParam(param.getKey(), param.getValue());
        }
        HolidayEntity en = new HolidayEntity();
        en.setDsName(dsName);
        en.updateEntityValue();
        getEntityManager().registerEntity(en);

        List<HolidayEntity> result = createQuery.execute();
        result.forEach(e -> e.setDsName(dsName));
        return result;

    }

    //checked
    default void delete_(HolidayEntity entity) {
        getEntityManager().delete(entity);
    }

    //checked
    default List<HolidayEntity> findAll_(String dsName) {
        TypedQuery<HolidayEntity> tq = getEntityManager().createQuery("select t from " + dsName + " t", HolidayEntity.class);

        HolidayEntity en = new HolidayEntity();
        en.setDsName(dsName);
        en.updateEntityValue();
        getEntityManager().registerEntity(en);

        List<HolidayEntity> result = tq.execute();
        result.forEach(e -> e.setDsName(dsName));
        return result;
    }

    //fixed + checked
    default List<HolidayEntity> findAllOtherHolidays_(String dsName) {
        TypedQuery<HolidayEntity> tq = getEntityManager().createQuery("select t from \"" + dsName + "\" t " + "where t.is_custom_holiday::boolean is true", HolidayEntity.class);

        HolidayEntity en = new HolidayEntity();
        en.setDsName(dsName);
        en.updateEntityValue();
        getEntityManager().registerEntity(en);

        List<HolidayEntity> result = tq.execute();
        result.forEach(e -> e.setDsName(dsName));
        return result;
    }

    //fixed + checked
    default List<HolidayEntity> findAllPublicHolidays_(String dsName) {
        TypedQuery<HolidayEntity> tq = getEntityManager().createQuery("select t from \"" + dsName + "\" t " + "where t.is_custom_holiday::boolean is false", HolidayEntity.class);

        HolidayEntity en = new HolidayEntity();
        en.setDsName(dsName);
        en.updateEntityValue();
        getEntityManager().registerEntity(en);

        List<HolidayEntity> result = tq.execute();
        result.forEach(e -> e.setDsName(dsName));
        return result;
    }


    EntityManager getEntityManager();
}
