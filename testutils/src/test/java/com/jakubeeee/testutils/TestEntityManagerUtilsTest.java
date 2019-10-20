package com.jakubeeee.testutils;

import com.jakubeeee.testutils.marker.FlowControlUnitTestCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static com.jakubeeee.testutils.TestEntityManagerUtils.*;
import static java.util.Collections.emptySortedMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@Category(FlowControlUnitTestCategory.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(TestEntityManager.class)
public class TestEntityManagerUtilsTest {

    @Mock
    private TestEntityManager testEntityManager;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<TestEntity> typedQuery;

    private static TestEntity TEST_ENTITY_1;
    private static TestEntity TEST_ENTITY_2;
    private static TestEntity TEST_ENTITY_3;
    private static TestNotEntity TEST_NOT_ENTITY_1;

    @BeforeClass
    public static void setUp() {
        TEST_ENTITY_1 = new TestEntity(1, "aaa", "bbb", "ccc");
        TEST_ENTITY_2 = new TestEntity(2, "ddd", "eee", "fff");
        TEST_ENTITY_3 = new TestEntity(3, "ggg", "hhh", "iii");
        TEST_NOT_ENTITY_1 = new TestNotEntity(4, "jjj", "kkk", "lll");
    }

    @Test
    public void clearTableTest() {
        mockGetEntityManager();
        mockCreateQuery();
        mockExecuteUpdate();
        clearTable(testEntityManager, TestEntity.class);
        verifyCreateQuery("DELETE FROM TestEntity");
        verifyExecuteUpdate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void clearTableTest_notEntityClass_shouldThrowException() {
        clearTable(testEntityManager, TestNotEntity.class);
    }

    @Test
    public void findSingleTest_emptyFilterMap() {
        mockFindSingleMethodCalls(TEST_ENTITY_1);
        var result = findSingle(testEntityManager, TestEntity.class, emptySortedMap());
        verifyCreateQuery("SELECT e FROM TestEntity e", TestEntity.class);
        verifyNoParametersSet();
        verifyGetSingleResult();
        assertThat(result, is(equalTo(TEST_ENTITY_1)));
    }

    @Test
    public void findSingleTest_oneParameterFilterMap() {
        mockFindSingleMethodCalls(TEST_ENTITY_1);
        var result = findSingle(testEntityManager, TestEntity.class, new TreeMap<>(Map.of("stringField1", "aaa")));
        verifyCreateQuery("SELECT e FROM TestEntity e WHERE e.stringField1 = :stringField1", TestEntity.class);
        verifySetParameter("stringField1", "aaa");
        verifyGetSingleResult();
        assertThat(result, is(equalTo(TEST_ENTITY_1)));
    }

    @Test
    public void findSingleTest_multipleParameterFilterMap() {
        mockFindSingleMethodCalls(TEST_ENTITY_1);
        var result = findSingle(testEntityManager, TestEntity.class,
                new TreeMap<>(Map.of(
                        "stringField1", "aaa",
                        "stringField2", "bbb",
                        "stringField3", "ccc"
                )));
        verifyCreateQuery("SELECT e FROM TestEntity e " +
                "WHERE e.stringField1 = :stringField1 " +
                "AND e.stringField2 = :stringField2 " +
                "AND e.stringField3 = :stringField3", TestEntity.class);
        verifySetParameter("stringField1", "aaa");
        verifySetParameter("stringField2", "bbb");
        verifySetParameter("stringField3", "ccc");
        verifyGetSingleResult();
        assertThat(result, is(equalTo(TEST_ENTITY_1)));
    }

    @Test
    public void findSingleTest_listParameterFilterMap() {
        mockFindSingleMethodCalls(TEST_ENTITY_1);
        var result = findSingle(testEntityManager, TestEntity.class, new TreeMap<>(Map.of("stringField1",
                List.of("aa", "aaa", "aaaa"))));
        verifyCreateQuery("SELECT e FROM TestEntity e WHERE e.stringField1 IN :stringField1", TestEntity.class);
        verifySetParameter("stringField1", List.of("aa", "aaa", "aaaa"));
        verifyGetSingleResult();
        assertThat(result, is(equalTo(TEST_ENTITY_1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSingleTest_notEntityClass_shouldThrowException() {
        findSingle(testEntityManager, TestNotEntity.class, emptySortedMap());
    }

    @Test
    public void findMultipleTest_emptyFilterMap() {
        var expectedResult = Stream.of(TEST_ENTITY_1, TEST_ENTITY_2, TEST_ENTITY_3);
        mockFindMultipleMethodCalls(expectedResult);
        var result = findMultiple(testEntityManager, TestEntity.class, emptySortedMap());
        verifyCreateQuery("SELECT e FROM TestEntity e", TestEntity.class);
        verifyNoParametersSet();
        verifyGetResultStream();
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void findMultipleTest_oneParameterFilterMap() {
        var expectedResult = Stream.of(TEST_ENTITY_1);
        mockFindMultipleMethodCalls(expectedResult);
        var result = findMultiple(testEntityManager, TestEntity.class, new TreeMap<>(Map.of("stringField1", "aaa")));
        verifyCreateQuery("SELECT e FROM TestEntity e WHERE e.stringField1 = :stringField1", TestEntity.class);
        verifySetParameter("stringField1", "aaa");
        verifyGetResultStream();
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void findMultipleTest_multipleParameterFilterMap() {
        var expectedResult = Stream.of(TEST_ENTITY_1);
        mockFindMultipleMethodCalls(expectedResult);
        var result = findMultiple(testEntityManager, TestEntity.class,
                new TreeMap<>(Map.of(
                        "stringField1", "aaa",
                        "stringField2", "bbb",
                        "stringField3", "ccc"
                )));
        verifyCreateQuery("SELECT e FROM TestEntity e " +
                "WHERE e.stringField1 = :stringField1 " +
                "AND e.stringField2 = :stringField2 " +
                "AND e.stringField3 = :stringField3", TestEntity.class);
        verifySetParameter("stringField1", "aaa");
        verifySetParameter("stringField2", "bbb");
        verifySetParameter("stringField3", "ccc");
        verifyGetResultStream();
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test
    public void findMultipleTest_listParameterFilterMap() {
        var expectedResult = Stream.of(TEST_ENTITY_1, TEST_ENTITY_2, TEST_ENTITY_3);
        mockFindMultipleMethodCalls(expectedResult);
        var result = findMultiple(testEntityManager, TestEntity.class,
                new TreeMap<>(Map.of("stringField1", List.of("aaa", "ddd", "ggg"))));
        verifyCreateQuery("SELECT e FROM TestEntity e WHERE e.stringField1 IN :stringField1", TestEntity.class);
        verifySetParameter("stringField1", List.of("aaa", "ddd", "ggg"));
        verifyGetResultStream();
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findMultipleTest_notEntityClass_shouldThrowException() {
        findMultiple(testEntityManager, TestNotEntity.class, emptySortedMap());
    }

    @Test
    public void findAllTest() {
        var expectedResult = Stream.of(TEST_ENTITY_1, TEST_ENTITY_2, TEST_ENTITY_3);
        mockFindMultipleMethodCalls(expectedResult);
        var result = findAll(testEntityManager, TestEntity.class);
        verifyCreateQuery("SELECT e FROM TestEntity e", TestEntity.class);
        verifyNoParametersSet();
        verifyGetResultStream();
        assertThat(result, is(equalTo(expectedResult)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAllTest_notEntityClass_shouldThrowException() {
        findAll(testEntityManager, TestNotEntity.class);
    }

    @Test
    public void insertTest() {
        mockGetEntityManager();
        insert(testEntityManager, TEST_ENTITY_1);
        verifyPersist(TEST_ENTITY_1);
    }

    @Test
    public void insertTest_multipleEntities() {
        mockGetEntityManager();
        insert(testEntityManager, TEST_ENTITY_1, TEST_ENTITY_2, TEST_ENTITY_3);
        verifyPersist(TEST_ENTITY_1);
        verifyPersist(TEST_ENTITY_2);
        verifyPersist(TEST_ENTITY_3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertTest_notEntity_shouldThrowException() {
        mockGetEntityManager();
        insert(testEntityManager, TEST_NOT_ENTITY_1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertTest_multipleEntitiesAndOneNotEntity_shouldThrowException() {
        mockGetEntityManager();
        insert(testEntityManager, TEST_ENTITY_1, TEST_ENTITY_2, TEST_NOT_ENTITY_1);
        verifyPersist(TEST_ENTITY_1);
        verifyPersist(TEST_ENTITY_2);
    }

    private void mockFindSingleMethodCalls(TestEntity expectedResult) {
        mockGetEntityManager();
        mockCreateQueryForTestEntity();
        mockGetSingleResult(expectedResult);
    }

    private void mockFindMultipleMethodCalls(Stream<TestEntity> expectedResult) {
        mockGetEntityManager();
        mockCreateQueryForTestEntity();
        mockGetResultStream(expectedResult);
    }

    private void mockGetEntityManager() {
        when(testEntityManager.getEntityManager()).thenReturn(entityManager);
    }

    private void mockCreateQuery() {
        when(entityManager.createQuery(anyString())).thenReturn(typedQuery);
    }

    private void mockCreateQueryForTestEntity() {
        when(entityManager.createQuery(anyString(), eq(TestEntity.class))).thenReturn(typedQuery);
    }

    private void mockExecuteUpdate() {
        when(typedQuery.executeUpdate()).thenReturn(1);
    }

    private void mockGetSingleResult(TestEntity expectedResult) {
        when(typedQuery.getSingleResult()).thenReturn(expectedResult);
    }

    private void mockGetResultStream(Stream<TestEntity> expectedResult) {
        when(typedQuery.getResultStream()).thenReturn(expectedResult);
    }

    private void verifyCreateQuery(String query) {
        verify(entityManager, times(1)).createQuery(query);
    }

    private void verifyCreateQuery(String query, Class<?> entityClass) {
        verify(entityManager, times(1)).createQuery(query, entityClass);
    }

    private void verifySetParameter(String parameter, Object value) {
        verify(typedQuery, times(1)).setParameter(parameter, value);
    }

    private void verifyPersist(Object entity) {
        verify(entityManager, times(1)).persist(entity);
    }

    private void verifyNoParametersSet() {
        verify(typedQuery, times(0)).setParameter(anyString(), any());
    }

    private void verifyExecuteUpdate() {
        verify(typedQuery, times(1)).executeUpdate();
    }

    private void verifyGetSingleResult() {
        verify(typedQuery, times(1)).getSingleResult();
    }

    private void verifyGetResultStream() {
        verify(typedQuery, times(1)).getResultStream();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    private static class TestEntity {
        private long id;
        private String stringField1;
        private String stringField2;
        private String stringField3;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestNotEntity {
        private long id;
        private String stringField1;
        private String stringField2;
        private String stringField3;
    }

}
