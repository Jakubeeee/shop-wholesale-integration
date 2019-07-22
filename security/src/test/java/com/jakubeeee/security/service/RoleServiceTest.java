package com.jakubeeee.security.service;

import com.jakubeeee.common.exception.DatabaseResultEmptyException;
import com.jakubeeee.security.entity.Role;
import com.jakubeeee.security.entity.User;
import com.jakubeeee.security.repository.RoleRepository;
import com.jakubeeee.testutils.marker.FlowControlUnitTestCategory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.jakubeeee.common.util.ReflectUtils.getFieldValue;
import static com.jakubeeee.security.entity.Role.Type.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@Category(FlowControlUnitTestCategory.class)
@RunWith(SpringRunner.class)
public class RoleServiceTest {

    @TestConfiguration
    static class TestContextConfig {

        @MockBean
        private RoleRepository roleRepository;

        @Bean
        public RoleService roleService() {
            return new RoleService(roleRepository);
        }

    }

    @Autowired
    private RoleService roleService;

    private RoleRepository roleRepository;

    private User testUser;

    @Before
    public void setUpForEveryTest() throws Exception {
        roleRepository = getFieldValue(roleService, RoleRepository.class);
        testUser = new User("testUsername", "testPassword", "testEmail");
    }

    @Test
    public void grantRolesTest_basicRole() {
        var roles = Set.of(Role.of(BASIC_USER));
        when(roleRepository.findByType(BASIC_USER)).thenReturn(Optional.of(Role.of(BASIC_USER)));
        roleService.grantRoles(testUser, roles);
        assertThat(testUser.getRoles(), hasItem(Role.of(BASIC_USER)));
        assertThat(testUser.getRoles(), not(hasItems(Role.of(PRO_USER), Role.of(ADMIN))));
    }

    @Test
    public void grantRolesTest_proRole() {
        var roles = Set.of(Role.of(PRO_USER));
        when(roleRepository.findByTypeIn(Set.of(BASIC_USER))).thenReturn(Set.of(Role.of(BASIC_USER)));
        roleService.grantRoles(testUser, roles);
        assertThat(testUser.getRoles(), hasItems(Role.of(BASIC_USER), Role.of(PRO_USER)));
        assertThat(testUser.getRoles(), not(hasItem(Role.of(ADMIN))));
    }

    @Test
    public void grantRolesTest_adminRole() {
        var roles = Set.of(Role.of(ADMIN));
        when(roleRepository.findByTypeIn(Set.of(BASIC_USER, PRO_USER))).thenReturn(Set.of(Role.of(BASIC_USER),
                Role.of(PRO_USER)));
        roleService.grantRoles(testUser, roles);
        assertThat(testUser.getRoles(), hasItems(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN)));
    }

    @Test
    public void grantRolesTest_basicProAndAdminRoles() {
        var roles = Set.of(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN));
        roleService.grantRoles(testUser, roles);
        assertThat(testUser.getRoles(), hasItems(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN)));
        verify(roleRepository, times(0)).findByTypeIn(anySet());
    }

    @Test
    public void findOneByTypeTest() {
        when(roleRepository.findByType(BASIC_USER)).thenReturn(Optional.of(Role.of(BASIC_USER)));
        Role result = roleService.findOneByType(BASIC_USER);
        assertThat(result, is(equalTo(Role.of(BASIC_USER))));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findOneByTypeTest_roleNotFound_shouldThrowException() {
        when(roleRepository.findByType(BASIC_USER)).thenReturn(Optional.empty());
        roleService.findOneByType(BASIC_USER);
    }

    @Test
    public void findOneOptionalByTypeTest() {
        when(roleRepository.findByType(BASIC_USER)).thenReturn(Optional.of(Role.of(BASIC_USER)));
        Optional<Role> result = roleService.findOneOptionalByType(BASIC_USER);
        assertThat(result, is(equalTo(Optional.of(Role.of(BASIC_USER)))));
    }

    @Test
    public void findOneOptionalByTypeTest_roleNotFound_shouldReturnEmptyOptional() {
        when(roleRepository.findByType(BASIC_USER)).thenReturn(Optional.empty());
        Optional<Role> result = roleService.findOneOptionalByType(BASIC_USER);
        assertThat(result, is(equalTo(Optional.empty())));
    }

    @Test
    public void findAllByTypesTest() {
        var roleTypes = Set.of(BASIC_USER, PRO_USER, ADMIN);
        var roles = Set.of(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN));
        when(roleRepository.findByTypeIn(roleTypes)).thenReturn(roles);
        Set<Role> result = roleService.findAllByTypes(roleTypes);
        Assert.assertThat(result, hasItems(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN)));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findAllByTypesTest_rolesNotFound_shouldThrowException() {
        var roleTypes = Set.of(BASIC_USER, PRO_USER, ADMIN);
        when(roleRepository.findByTypeIn(roleTypes)).thenReturn(emptySet());
        roleService.findAllByTypes(roleTypes);
    }

    @Test
    public void findAllTest() {
        var roles = List.of(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN));
        when(roleRepository.findAll()).thenReturn(roles);
        Set<Role> result = roleService.findAll();
        Assert.assertThat(result, hasItems(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN)));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findAllTest_rolesNotFound_shouldThrowException() {
        when(roleRepository.findAll()).thenReturn(emptyList());
        roleService.findAll();
    }

    @Test
    public void saveTest() {
        var role = Role.of(BASIC_USER);
        roleService.save(role);
        verify(roleRepository, times(1)).save(role);
    }

}
