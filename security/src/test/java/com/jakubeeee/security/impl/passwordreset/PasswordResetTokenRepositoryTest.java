package com.jakubeeee.security.impl.passwordreset;

import com.jakubeeee.security.impl.user.User;
import com.jakubeeee.testutils.marker.SpringSliceTestCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.jakubeeee.common.CollectionUtils.sortedMapOf;
import static com.jakubeeee.common.DateTimeUtils.getCurrentDateTime;
import static com.jakubeeee.testutils.TestEntityManagerUtils.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("all")
@Category(SpringSliceTestCategory.class)
@RunWith(SpringRunner.class)
@DataJpaTest
public class PasswordResetTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordResetTokenRepository repository;

    private PasswordResetToken testPasswordResetToken1;
    private PasswordResetToken testPasswordResetToken2;
    private PasswordResetToken testPasswordResetToken3;
    private User testUser1;
    private User testUser2;

    @Before
    public void setUpForEveryTest() {
        entityManager.clear();
        clearTable(entityManager, User.class);
        clearTable(entityManager, PasswordResetToken.class);
        testUser1 = new User("testUsername1", "testPassword1", "testEmail1");
        entityManager.persist(testUser1);
        testUser2 = new User("testUsername2", "testPassword2", "testEmail2");
        entityManager.persist(testUser2);
        testPasswordResetToken1 = new PasswordResetToken("testValue1", testUser1, getCurrentDateTime(), 120);
        entityManager.persist(testPasswordResetToken1);
        testPasswordResetToken2 = new PasswordResetToken("testValue2", testUser1, getCurrentDateTime(), 120);
        entityManager.persist(testPasswordResetToken2);
        testPasswordResetToken3 = new PasswordResetToken("testValue3", testUser1, getCurrentDateTime(), 120);
        entityManager.persist(testPasswordResetToken3);
    }

    @Test
    public void saveTest_shouldSave() {
        var savedEntity = new PasswordResetToken("anotherTestValue", testUser1, getCurrentDateTime(), 120);
        repository.save(savedEntity);
        entityManager.detach(savedEntity);
        var result = findSingle(entityManager, PasswordResetToken.class,
                sortedMapOf(Map.of("id", savedEntity.getId())));
        assertThat(savedEntity.getId(), is(not(nullValue())));
        assertThat(result.getId(), is(equalTo(savedEntity.getId())));
        assertThat(result, is(equalTo(savedEntity)));
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nullEntity_shouldThrowException() {
        PasswordResetToken savedEntity = null;
        repository.save(savedEntity);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nonUniqueValue_shouldThrowException() {
        var savedEntity1 = new PasswordResetToken("nonUniqueTestValue", testUser1, getCurrentDateTime(), 120);
        repository.save(savedEntity1);
        var savedEntity2 = new PasswordResetToken("nonUniqueTestValue", testUser1, getCurrentDateTime(), 120);
        repository.save(savedEntity2);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nullValue_shouldThrowException() {
        var savedEntity = new PasswordResetToken(null, testUser1, getCurrentDateTime(), 120);
        repository.save(savedEntity);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nullUser_shouldThrowException() {
        var savedEntity = new PasswordResetToken("anotherTestValue", null, getCurrentDateTime(), 120);
        repository.save(savedEntity);
    }

    @Test(expected = NullPointerException.class)
    public void saveTest_nullExpiryDate_shouldThrowException() {
        var savedEntity = new PasswordResetToken("anotherTestValue", testUser1, null, 120);
        repository.save(savedEntity);
    }

    @Test
    public void saveAllTest_shouldSave() {
        var savedEntity1 = new PasswordResetToken("anotherTestValue1", testUser1, getCurrentDateTime(), 120);
        var savedEntity2 = new PasswordResetToken("anotherTestValue2", testUser1, getCurrentDateTime(), 120);
        var savedEntity3 = new PasswordResetToken("anotherTestValue3", testUser1, getCurrentDateTime(), 120);
        repository.saveAll(List.of(savedEntity1, savedEntity2, savedEntity3));
        entityManager.detach(savedEntity1);
        entityManager.detach(savedEntity2);
        entityManager.detach(savedEntity3);
        var resultStream = findAll(entityManager, PasswordResetToken.class);
        assertThat(Stream.of(savedEntity1, savedEntity2, savedEntity3)
                .allMatch(entity -> entity.getId() != null), is(equalTo(true)));
        List<PasswordResetToken> resultList = resultStream.collect(toList());
        assertThat(resultList, hasItems(
                hasProperty("id", is(equalTo(savedEntity1.getId()))),
                hasProperty("id", is(equalTo(savedEntity2.getId()))),
                hasProperty("id", is(equalTo(savedEntity3.getId())))
        ));
        assertThat(resultList,
                hasItems(savedEntity1, savedEntity2, savedEntity3)
        );
    }

    @Test
    public void findByIdTest_shouldFind() {
        Optional<PasswordResetToken> resultO = repository.findById(testPasswordResetToken1.getId());
        assertThat(resultO.isPresent(), is(equalTo(true)));
        PasswordResetToken result = resultO.get();
        assertThat(result, is(equalTo(testPasswordResetToken1)));
    }

    @Test(expected = DataAccessException.class)
    public void findByIdTest_nullId_shouldThrowException() {
        repository.findById(null);
    }

    @Test
    public void existsByIdTest_shouldExist() {
        boolean exists = repository.existsById(testPasswordResetToken1.getId());
        assertThat(exists, is(equalTo(true)));
    }

    @Test
    public void findAllTest_shouldFind() {
        var resultList = (List<PasswordResetToken>) repository.findAll();
        assertThat(resultList,
                hasItems(testPasswordResetToken1, testPasswordResetToken2, testPasswordResetToken3)
        );
    }

    @Test
    public void findAllByIdTest_shouldFind() {
        var resultList = (List<PasswordResetToken>) repository.findAllById(
                (List<Long>) List.of(testPasswordResetToken1.getId(), testPasswordResetToken2.getId()));
        assertThat(resultList,
                hasItems(testPasswordResetToken1, testPasswordResetToken2));
    }

    @Test(expected = DataAccessException.class)
    public void findAllByIdTest_nullIdList_shouldThrowException() {
        repository.findAllById(null);
    }

    @Test(expected = NullPointerException.class)
    public void findAllByIdTest_listOfNulls_shouldThrowException() {
        repository.findAllById(List.of(null, null, null));
    }

    @Test
    public void findByValueTest_shouldFind() {
        Optional<PasswordResetToken> resultO = repository.findByValue(testPasswordResetToken1.getValue());
        assertThat(resultO.isPresent(), is(equalTo(true)));
        PasswordResetToken result = resultO.get();
        assertThat(result, is(equalTo(testPasswordResetToken1)));
    }

    @Test
    public void findByValueTest_nullValue_shouldNotFind() {
        Optional<PasswordResetToken> resultO = repository.findByValue(null);
        assertThat(resultO.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void findAllByUserTest() {
        var anotherTestPasswordResetToken1 = new PasswordResetToken("anotherTestValue1", testUser2, getCurrentDateTime(), 120);
        var anotherTestPasswordResetToken2 = new PasswordResetToken("anotherTestValue2", testUser2, getCurrentDateTime(), 120);
        insert(entityManager, anotherTestPasswordResetToken1);
        insert(entityManager, anotherTestPasswordResetToken2);
        Set<PasswordResetToken> resultSet = repository.findAllByUser(testUser2);
        assertThat(resultSet,
                not(hasItems(testPasswordResetToken1, testPasswordResetToken2, testPasswordResetToken3))
        );
        assertThat(resultSet,
                hasItems(anotherTestPasswordResetToken1, anotherTestPasswordResetToken2)
        );
    }

    @Test
    public void findAllByUserTest_nullUser_shouldNotFind() {
        Set<PasswordResetToken> resultSet = repository.findAllByUser(null);
        assertThat(resultSet.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void countTest() {
        long count = repository.count();
        assertThat(count, is(equalTo(3L)));
    }

    @Test
    public void deleteByIdTest_shouldDelete() {
        repository.deleteById(testPasswordResetToken1.getId());
        var resultStream = findAll(entityManager, PasswordResetToken.class);
        List<PasswordResetToken> resultList = resultStream.collect(toList());
        assertThat(resultList,
                not(hasItem(testPasswordResetToken1))
        );
        assertThat(resultList,
                hasItems(testPasswordResetToken2, testPasswordResetToken3)
        );
    }

    @Test(expected = DataAccessException.class)
    public void deleteByIdTest_nullId_shouldThrowException() {
        repository.deleteById(null);
    }

    @Test(expected = DataAccessException.class)
    public void deleteTest_noEntityWithGivenId_shouldThrowException() {
        repository.deleteById(99L);
    }

    @Test
    public void deleteTest_shouldDelete() {
        repository.delete(testPasswordResetToken1);
        var resultStream = findAll(entityManager, PasswordResetToken.class);
        List<PasswordResetToken> resultList = resultStream.collect(toList());
        assertThat(resultList,
                not(hasItem(testPasswordResetToken1))
        );
        assertThat(resultList,
                hasItems(testPasswordResetToken2, testPasswordResetToken3)
        );
    }

    @Test(expected = DataAccessException.class)
    public void deleteTest_nullEntity_shouldThrowException() {
        repository.delete(null);
    }

    @Test
    public void deleteAllTest_nonNullEntityList_shouldDelete() {
        repository.deleteAll(List.of(testPasswordResetToken1, testPasswordResetToken2));
        var resultStream = findAll(entityManager, PasswordResetToken.class);
        List<PasswordResetToken> resultList = resultStream.collect(toList());
        assertThat(resultList,
                not(hasItems(testPasswordResetToken1, testPasswordResetToken2))
        );
        assertThat(resultList,
                hasItem(testPasswordResetToken3)
        );
    }

    @Test(expected = DataAccessException.class)
    public void deleteAllTest_nullEntityList_shouldThrowException() {
        repository.deleteAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteAllTest_listOfNulls_shouldThrowException() {
        repository.deleteAll(List.of(null, null, null));
    }

    @Test
    public void deleteAllTest_noParameters_shouldDelete() {
        repository.deleteAll();
        var resultStream = findAll(entityManager, PasswordResetToken.class);
        List<PasswordResetToken> resultList = resultStream.collect(toList());
        assertThat(resultList, is(emptyCollectionOf(PasswordResetToken.class)));
    }

}
