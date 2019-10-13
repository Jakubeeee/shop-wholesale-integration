package com.jakubeeee.testutils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.stream.Stream;

import static java.util.Collections.emptySortedMap;

/**
 * Utility class providing useful static methods for operations related to {@link TestEntityManager} class.
 * These methods are used for database related tests.
 */
@UtilityClass
public final class TestEntityManagerUtils {

    public static void clearTable(@NonNull TestEntityManager testEntityManager, @NonNull Class<?> entityClass) {
        clearTable(testEntityManager.getEntityManager(), entityClass);
    }

    private static void clearTable(EntityManager entityManager, Class<?> entityClass) {
        validateIfEntity(entityClass);
        entityManager.createQuery("DELETE FROM " + entityClass.getSimpleName()).executeUpdate();
    }

    @Nullable
    public static <T> T findSingle(@NonNull TestEntityManager testEntityManager,
                                   @NonNull Class<T> entityClass,
                                   @NonNull SortedMap<String, Object> filterMap) {
        return findSingle(testEntityManager.getEntityManager(), entityClass, filterMap);
    }

    private static <T> T findSingle(EntityManager entityManager, Class<T> entityClass,
                                    SortedMap<String, Object> filterMap) {
        try {
            return getTypedQueryForFind(entityManager, entityClass, filterMap).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public static <T> Stream<T> findMultiple(@NonNull TestEntityManager testEntityManager,
                                             @NonNull Class<T> entityClass,
                                             @NonNull SortedMap<String, Object> filterMap) {
        return findMultiple(testEntityManager.getEntityManager(), entityClass, filterMap);
    }

    private static <T> Stream<T> findMultiple(EntityManager entityManager, Class<T> entityClass,
                                              SortedMap<String, Object> filterMap) {
        return getTypedQueryForFind(entityManager, entityClass, filterMap).getResultStream();
    }

    public static <T> Stream<T> findAll(@NonNull TestEntityManager testEntityManager, @NonNull Class<T> entityClass) {
        return findAll(testEntityManager.getEntityManager(), entityClass);
    }

    private static <T> Stream<T> findAll(EntityManager entityManager, Class<T> entityClass) {
        return getTypedQueryForFind(entityManager, entityClass, emptySortedMap()).getResultStream();
    }

    public static <T> void insert(@NonNull TestEntityManager testEntityManager, @NonNull T entity) {
        insert(testEntityManager.getEntityManager(), entity);
    }

    private static <T> void insert(EntityManager entityManager, T entity) {
        validateIfEntity(entity.getClass());
        entityManager.persist(entity);
    }

    private static void validateIfEntity(Class<?> entityClass) {
        var entityAnnotation = entityClass.getAnnotation(Entity.class);
        if (entityAnnotation == null)
            throw new IllegalArgumentException("Class " + entityClass + "is not a JPA entity");
    }

    private static <T> TypedQuery<T> getTypedQueryForFind(EntityManager entityManager, Class<T> entityClass,
                                                          SortedMap<String, Object> filterMap) {
        validateIfEntity(entityClass);
        String queryAsString = buildFindQuery(entityClass, filterMap);
        TypedQuery<T> typedQuery = entityManager.createQuery(queryAsString, entityClass);
        fillQueryParameters(typedQuery, filterMap);
        return typedQuery;
    }

    private static String buildFindQuery(Class<?> entityClass, SortedMap<String, Object> filterMap) {
        StringBuilder queryBuilder = new StringBuilder("SELECT e FROM " + entityClass.getSimpleName() + " e");
        if (!filterMap.isEmpty())
            appendConditionsToQuery(queryBuilder, filterMap);
        return queryBuilder.toString();
    }

    private static void appendConditionsToQuery(StringBuilder queryBuilder, SortedMap<String, Object> filterMap) {
        queryBuilder.append(" WHERE ");
        Iterator<Entry<String, Object>> iterator;
        for (iterator = filterMap.entrySet().iterator(); iterator.hasNext(); ) {
            var filterCondition = iterator.next();
            appendConditionToQuery(queryBuilder, filterCondition.getKey(), filterCondition.getValue());
            if (iterator.hasNext())
                queryBuilder.append(" AND ");
        }
    }

    private static void appendConditionToQuery(StringBuilder queryBuilder, String jpqlProperty, Object value) {
        queryBuilder
                .append("e.")
                .append(jpqlProperty);
        if (value instanceof Collection)
            queryBuilder.append(" IN ");
        else
            queryBuilder.append(" = ");
        queryBuilder.append(":")
                .append(toParameterName(jpqlProperty));
    }

    private static void fillQueryParameters(TypedQuery<?> typedQuery, SortedMap<String, Object> filterMap) {
        for (SortedMap.Entry<String, Object> filterCondition : filterMap.entrySet())
            typedQuery.setParameter(toParameterName(filterCondition.getKey()), filterCondition.getValue());
    }

    private static String toParameterName(String property) {
        return property.replace(".", "_");
    }

}
