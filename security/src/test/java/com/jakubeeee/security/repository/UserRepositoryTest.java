package com.jakubeeee.security.repository;

import com.jakubeeee.security.entity.PasswordResetToken;
import com.jakubeeee.security.entity.Role;
import com.jakubeeee.security.entity.User;
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
import static com.jakubeeee.testutils.util.TestEntityManagerUtils.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("all")
@Category(SpringSliceTestCategory.class)
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    private User testUser1;
    private User testUser2;
    private User testUser3;
    private PasswordResetToken testPasswordResetToken;
    private Role testBasicRole;

    @Before
    public void setUpForEveryTest() {
        entityManager.clear();
        clearTable(entityManager, User.class);
        testUser1 = new User("testUsername1", "testPassword1", "testEmail1");
        entityManager.persist(testUser1);
        testUser2 = new User("testUsername2", "testPassword2", "testEmail2");
        entityManager.persist(testUser2);
        testUser3 = new User("testUsername3", "testPassword3", "testEmail3");
        entityManager.persist(testUser3);
    }

    @Test
    public void saveTest_shouldSave() {
        var savedEntity = new User("additionalTestUsername", "additionalTestPassword", "additionalTestEmail");
        repository.save(savedEntity);
        entityManager.detach(savedEntity);
        var result = findSingle(entityManager, User.class,
                sortedMapOf(Map.of("id", savedEntity.getId())));
        assertThat(savedEntity.getId(), is(not(nullValue())));
        assertThat(result.getId(), is(equalTo(savedEntity.getId())));
        assertThat(result, is(equalTo(savedEntity)));
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nullEntity_shouldThrowException() {
        User savedEntity = null;
        repository.save(savedEntity);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nonUniqueUsername_shouldThrowException() {
        var savedEntity1 = new User("nonUniqueTestUsername", "additionalTestPassword1", "additionalTestEmail1");
        repository.save(savedEntity1);
        var savedEntity2 = new User("nonUniqueTestUsername", "additionalTestPassword2", "additionalTestEmail2");
        repository.save(savedEntity2);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nullUsername_shouldThrowException() {
        var savedEntity = new User(null, "additionalTestPassword", "additionalTestEmail");
        repository.save(savedEntity);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nullPassword_shouldThrowException() {
        var savedEntity = new User("additionalTestUsername", null, "additionalTestEmail");
        repository.save(savedEntity);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nonUniqueEmail_shouldThrowException() {
        var savedEntity1 = new User("additionalTestUsername", "additionalTestPassword1", "nonUniqueTestEmail");
        repository.save(savedEntity1);
        var savedEntity2 = new User("additionalTestUsername", "additionalTestPassword2", "nonUniqueTestEmail");
        repository.save(savedEntity2);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nullEmail_shouldThrowException() {
        var savedEntity = new User("additionalTestUsername", "additionalTestPassword", null);
        repository.save(savedEntity);
    }

    @Test
    public void saveAllTest_shouldSave() {
        var savedEntity1 = new User("additionalTestUsername1", "additionalTestPassword1", "additionalTestEmail");
        var savedEntity2 = new User("additionalTestUsername2", "additionalTestPassword2", "additionalTestEmai2");
        var savedEntity3 = new User("additionalTestUsername3", "additionalTestPassword3", "additionalTestEmai3");
        repository.saveAll(List.of(savedEntity1, savedEntity2, savedEntity3));
        entityManager.detach(savedEntity1);
        entityManager.detach(savedEntity2);
        entityManager.detach(savedEntity3);
        var resultStream = findAll(entityManager, User.class);
        assertThat(Stream.of(savedEntity1, savedEntity2, savedEntity3)
                .allMatch(entity -> entity.getId() != null), is(equalTo(true)));
        List<User> resultList = resultStream.collect(toList());
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
        Optional<User> resultO = repository.findById(testUser1.getId());
        assertThat(resultO.isPresent(), is(equalTo(true)));
        User result = resultO.get();
        assertThat(result, is(equalTo(testUser1)));
    }

    @Test(expected = DataAccessException.class)
    public void findByIdTest_nullId_shouldThrowException() {
        repository.findById(null);
    }

    @Test
    public void existsByIdTest_shouldExist() {
        boolean exists = repository.existsById(testUser1.getId());
        assertThat(exists, is(equalTo(true)));
    }

    @Test
    public void findAllTest_shouldFind() {
        var resultList = (List<User>) repository.findAll();
        assertThat(resultList,
                hasItems(testUser1, testUser2, testUser3)
        );
    }

    @Test
    public void findAllByIdTest_shouldFind() {
        var resultList = (List<User>) repository.findAllById(
                (List<Long>) List.of(testUser1.getId(), testUser2.getId()));
        assertThat(resultList,
                hasItems(testUser1, testUser2));
    }

    @Test(expected = DataAccessException.class)
    public void findAllByIdTest_nullId_shouldThrowException() {
        repository.findAllById(null);
    }

    @Test(expected = NullPointerException.class)
    public void findAllByIdTest_listOfNulls_shouldThrowException() {
        repository.findAllById(List.of(null, null, null));
    }

    @Test
    public void findByUsernameTest_shouldFind() {
        Optional<User> resultO = repository.findByUsername(testUser1.getUsername());
        assertThat(resultO.isPresent(), is(equalTo(true)));
        User result = resultO.get();
        assertThat(result, is(equalTo(testUser1)));
    }

    @Test
    public void findAllByUsernameTest_nullUsername_shouldNotFind() {
        Optional<User> resultO = repository.findByUsername(null);
        assertThat(resultO.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void findByEmailTest_shouldFind() {
        Optional<User> resultO = repository.findByEmail(testUser1.getEmail());
        assertThat(resultO.isPresent(), is(equalTo(true)));
        User result = resultO.get();
        assertThat(result, is(equalTo(testUser1)));
    }

    @Test
    public void findAllByEmailTest_nullEmail_shouldNotFind() {
        Optional<User> resultO = repository.findByEmail(null);
        assertThat(resultO.isEmpty(), is(equalTo(true)));
    }

    @Test
    public void countTest() {
        long count = repository.count();
        assertThat(count, is(equalTo(3L)));
    }

    @Test
    public void updateUserPasswordTest_shouldUpdate() {
        String updatedPassword = "updatedTestPassword";
        repository.updateUserPassword(testUser1.getId(), updatedPassword);
        entityManager.refresh(testUser1);
        var result = findSingle(entityManager, User.class,
                sortedMapOf(Map.of("id", testUser1.getId())));
        assertThat(result.getPassword(), is(equalTo(updatedPassword)));
    }

    @Test(expected = NullPointerException.class)
    public void updateUserPasswordTest_nullUserId_shouldThrowException() {
        String updatedPassword = "updatedTestPassword";
        repository.updateUserPassword((Long) null, updatedPassword);
    }

    @Test(expected = DataAccessException.class)
    public void updateUserPasswordTest_nullPassword_shouldThrowException() {
        String updatedPassword = null;
        repository.updateUserPassword(testUser1.getId(), null);
    }

    @Test
    public void deleteByIdTest_shouldDelete() {
        repository.deleteById(testUser1.getId());
        var resultStream = findAll(entityManager, User.class);
        List<User> resultList = resultStream.collect(toList());
        assertThat(resultList,
                not(hasItem(testUser1))
        );
        assertThat(resultList,
                hasItems(testUser2, testUser3)
        );
    }

    @Test
    public void deleteByIdTest_dependantPasswordResetToken_shouldDelete() {
        var testPasswordResetToken = new PasswordResetToken("testValue", testUser1, getCurrentDateTime(), 120);
        testUser1.setPasswordResetTokens(Set.of(testPasswordResetToken));
        repository.deleteById(testUser1.getId());
        var result = findSingle(entityManager, PasswordResetToken.class,
                sortedMapOf(Map.of("user.id", testUser1.getId())));
        assertThat(result, is(nullValue()));
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
        repository.delete(testUser1);
        var resultStream = findAll(entityManager, User.class);
        List<User> resultList = resultStream.collect(toList());
        assertThat(resultList,
                not(hasItem(testUser1))
        );
        assertThat(resultList,
                hasItems(testUser2, testUser3)
        );
    }

    @Test
    public void deleteTest_dependantPasswordResetToken_shouldDelete() {
        var testPasswordResetToken = new PasswordResetToken("testValue", testUser1, getCurrentDateTime(), 120);
        testUser1.setPasswordResetTokens(Set.of(testPasswordResetToken));
        repository.delete(testUser1);
        var result = findSingle(entityManager, PasswordResetToken.class,
                sortedMapOf(Map.of("user.id", testUser1.getId())));
        assertThat(result, is(nullValue()));
    }

    @Test(expected = DataAccessException.class)
    public void deleteTest_nullEntity_shouldThrowException() {
        repository.delete(null);
    }

    @Test
    public void deleteAllTest_nonNullEntityList_shouldDelete() {
        repository.deleteAll(List.of(testUser1, testUser2));
        var resultStream = findAll(entityManager, User.class);
        List<User> resultList = resultStream.collect(toList());
        assertThat(resultList,
                not(hasItems(testUser1, testUser2))
        );
        assertThat(resultList,
                hasItem(testUser3)
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
        var resultStream = findAll(entityManager, User.class);
        List<User> resultList = resultStream.collect(toList());
        assertThat(resultList, is(emptyCollectionOf(User.class)));
    }

}
