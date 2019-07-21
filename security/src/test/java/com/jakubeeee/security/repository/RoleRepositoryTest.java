package com.jakubeeee.security.repository;

import com.jakubeeee.security.entity.Role;
import com.jakubeeee.security.entity.User;
import com.jakubeeee.testutils.marker.SliceUnitTest;
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

import static com.jakubeeee.common.util.CollectionUtils.sortedMapOf;
import static com.jakubeeee.security.entity.Role.Type.*;
import static com.jakubeeee.testutils.util.TestEntityManagerUtils.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("all")
@Category(SliceUnitTest.class)
@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository repository;

    private static Role testBasicRole;
    private static Role testProRole;

    @Before
    public void setUpForEveryTest() {
        entityManager.clear();
        clearTable(entityManager, User.class);
        clearTable(entityManager, Role.class);
        testBasicRole = Role.of(BASIC_USER);
        entityManager.persist(testBasicRole);
        testProRole = Role.of(PRO_USER);
        entityManager.persist(testProRole);
    }

    @Test
    public void saveTest_shouldSave() {
        var savedEntity = Role.of(ADMIN);
        repository.save(savedEntity);
        entityManager.detach(savedEntity);
        var result = findSingle(entityManager, Role.class, sortedMapOf(Map.of("id", savedEntity.getId())));
        assertThat(savedEntity.getId(), is(not(nullValue())));
        assertThat(result.getId(), is(equalTo(savedEntity.getId())));
        assertThat(result, is(equalTo(savedEntity)));
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nullEntity_shouldThrowException() {
        Role savedEntity = null;
        repository.save(savedEntity);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nonUniqueType_shouldThrowException() {
        var savedEntity1 = Role.of(ADMIN);
        repository.save(savedEntity1);
        var savedEntity2 = Role.of(ADMIN);
        repository.save(savedEntity2);
    }

    @Test(expected = DataAccessException.class)
    public void saveTest_nullType_shouldThrowException() {
        var savedEntity = Role.of(null);
        repository.save(savedEntity);
    }

    @Test
    public void saveAllTest_shouldSave() {
        clearTable(entityManager, Role.class);
        var savedEntity1 = Role.of(BASIC_USER);
        var savedEntity2 = Role.of(PRO_USER);
        var savedEntity3 = Role.of(ADMIN);
        repository.saveAll(List.of(savedEntity1, savedEntity2, savedEntity3));
        entityManager.detach(savedEntity1);
        entityManager.detach(savedEntity2);
        entityManager.detach(savedEntity3);
        var resultStream = findAll(entityManager, Role.class);
        assertThat(Stream.of(savedEntity1, savedEntity2, savedEntity3)
                .allMatch(entity -> entity.getId() != null), is(equalTo(true)));
        List<Role> resultList = resultStream.collect(toList());
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
        Optional<Role> resultO = repository.findById(testBasicRole.getId());
        assertThat(resultO.isPresent(), is(equalTo(true)));
        Role result = resultO.get();
        assertThat(result, is(equalTo(testBasicRole)));
    }

    @Test(expected = DataAccessException.class)
    public void findByIdTest_nullId_shouldThrowException() {
        repository.findById(null);
    }

    @Test
    public void existsByIdTest_shouldExist() {
        boolean exists = repository.existsById(testBasicRole.getId());
        assertThat(exists, is(equalTo(true)));
    }

    @Test
    public void findAllTest_shouldFind() {
        var resultList = (List<Role>) repository.findAll();
        assertThat(resultList,
                hasItems(testBasicRole, testProRole)
        );
    }

    @Test
    public void findAllByIdTest_shouldFind() {
        var resultList = (List<Role>) repository.findAllById(
                (List<Long>) List.of(testBasicRole.getId(), testProRole.getId()));
        assertThat(resultList,
                hasItems(testBasicRole, testProRole));
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
    public void findByTypeTest() {
        Optional<Role> resultO = repository.findByType(testBasicRole.getType());
        assertThat(resultO.isPresent(), is(equalTo(true)));
        Role result = resultO.get();
        assertThat(result.getType(), is(equalTo(BASIC_USER)));
    }

    @Test
    public void findAllByTypeTest_nullType_shouldNotFind() {
        Optional<Role> resultO = repository.findByType(null);
        assertThat(resultO.isPresent(), is(equalTo(false)));
    }

    @Test
    public void findByTypeInTest() {
        Set<Role> result = repository.findByTypeIn(Set.of(BASIC_USER, PRO_USER));
        assertThat(result.size() == 2, is(equalTo(true)));
        assertThat(result, contains(
                hasProperty("type", is(BASIC_USER)),
                hasProperty("type", is(PRO_USER))
        ));
    }

    @Test(expected = DataAccessException.class)
    public void findAllByTypeInTest_nullTypeSet_shouldThrowException() {
        repository.findByTypeIn(null);
    }

    @Test(expected = NullPointerException.class)
    public void findAllByTypeInTest_setOfNulls_shouldThrowException() {
        repository.findByTypeIn(Set.of(null, null, null));
    }

    @Test
    public void countTest() {
        long count = repository.count();
        assertThat(count, is(equalTo(2L)));
    }

    @Test
    public void deleteByIdTest_shouldDelete() {
        repository.deleteById(testProRole.getId());
        var resultStream = findAll(entityManager, Role.class);
        List<Role> resultList = resultStream.collect(toList());
        assertThat(resultList,
                not(hasItem(testProRole))
        );
        assertThat(resultList,
                hasItem(testBasicRole)
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
        repository.delete(testProRole);
        var resultStream = findAll(entityManager, Role.class);
        List<Role> resultList = resultStream.collect(toList());
        assertThat(resultList,
                not(hasItem(testProRole))
        );
        assertThat(resultList,
                hasItem(testBasicRole)
        );
    }

    @Test(expected = DataAccessException.class)
    public void deleteTest_nullEntity_shouldThrowException() {
        repository.delete(null);
    }

    @Test
    public void deleteAllTest_nonNullEntityList_shouldDelete() {
        Role testAdminRole = Role.of(ADMIN);
        entityManager.persist(testAdminRole);
        repository.deleteAll(List.of(testProRole, testAdminRole));
        var resultStream = findAll(entityManager, Role.class);
        List<Role> resultList = resultStream.collect(toList());
        assertThat(resultList,
                not(hasItems(testProRole, testAdminRole))
        );
        assertThat(resultList,
                hasItem(testBasicRole)
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
        var resultStream = findAll(entityManager, Role.class);
        List<Role> resultList = resultStream.collect(toList());
        assertThat(resultList, is(emptyCollectionOf(Role.class)));
    }

}
