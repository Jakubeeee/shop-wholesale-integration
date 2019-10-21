package com.jakubeeee.security.impl.role;

import com.jakubeeee.common.persistence.DatabaseResultEmptyException;
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

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.jakubeeee.common.reflection.ReflectUtils.getFieldValue;
import static com.jakubeeee.security.impl.role.RoleType.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@Category(FlowControlUnitTestCategory.class)
@RunWith(SpringRunner.class)
public class DefaultRoleServiceTest {

    @TestConfiguration
    static class TestContextConfig {

        @MockBean
        private RoleRepository roleRepository;

        @Bean
        public RoleService roleService() {
            return new DefaultRoleService(roleRepository);
        }

    }

    @Autowired
    private RoleService roleService;

    private RoleRepository roleRepository;

    @Before
    public void setUpForEveryTest() throws Exception {
        roleRepository = getFieldValue(roleService, RoleRepository.class);
    }

    @Test
    public void resolveRolesToAssignTest_basicRole() {
        var roleTypes = EnumSet.of(BASIC_USER);
        when(roleRepository.findByTypeIn(EnumSet.of(BASIC_USER))).thenReturn(Set.of(Role.of(BASIC_USER)));
        Set<RoleValue> result = roleService.resolveRolesToAssign(roleTypes);
        assertThat(result, hasItem(RoleValue.of(BASIC_USER)));
        assertThat(result, not(hasItems(RoleValue.of(PRO_USER), RoleValue.of(ADMIN))));
    }

    @Test
    public void resolveRolesToAssignTest_proRole() {
        var roleTypes = EnumSet.of(PRO_USER);
        when(roleRepository.findByTypeIn(EnumSet.of(PRO_USER))).thenReturn(Set.of(Role.of(PRO_USER)));
        when(roleRepository.findByTypeIn(EnumSet.of(BASIC_USER))).thenReturn(Set.of(Role.of(BASIC_USER)));
        Set<RoleValue> result = roleService.resolveRolesToAssign(roleTypes);
        assertThat(result, hasItems(RoleValue.of(BASIC_USER), RoleValue.of(PRO_USER)));
        assertThat(result, not(hasItem(RoleValue.of(ADMIN))));
    }

    @Test
    public void resolveRolesToAssignTest_adminRole() {
        var roleTypes = EnumSet.of(ADMIN);
        when(roleRepository.findByTypeIn(EnumSet.of(ADMIN))).thenReturn(Set.of(Role.of(ADMIN)));
        when(roleRepository.findByTypeIn(EnumSet.of(BASIC_USER, PRO_USER))).thenReturn(Set.of(Role.of(BASIC_USER),
                Role.of(PRO_USER)));
        Set<RoleValue> result = roleService.resolveRolesToAssign(roleTypes);
        assertThat(result, hasItems(RoleValue.of(BASIC_USER), RoleValue.of(PRO_USER), RoleValue.of(ADMIN)));
    }

    @Test
    public void resolveRolesToAssignTest_basicProAndAdminRoles() {
        var roleTypes = EnumSet.of(BASIC_USER, PRO_USER, ADMIN);
        when(roleRepository.findByTypeIn(EnumSet.of(BASIC_USER, PRO_USER, ADMIN))).thenReturn(Set.of(Role.of(BASIC_USER),
                Role.of(PRO_USER), Role.of(ADMIN)));
        Set<RoleValue> result = roleService.resolveRolesToAssign(roleTypes);
        assertThat(result, hasItems(RoleValue.of(BASIC_USER), RoleValue.of(PRO_USER), RoleValue.of(ADMIN)));
    }

    @Test
    public void findOneByTypeTest() {
        when(roleRepository.findByType(BASIC_USER)).thenReturn(Optional.of(Role.of(BASIC_USER)));
        RoleValue result = roleService.findOneByType(BASIC_USER);
        assertThat(result, is(equalTo(RoleValue.of(BASIC_USER))));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findOneByTypeTest_roleNotFound_shouldThrowException() {
        when(roleRepository.findByType(BASIC_USER)).thenReturn(Optional.empty());
        roleService.findOneByType(BASIC_USER);
    }

    @Test
    public void findOneOptionalByTypeTest() {
        when(roleRepository.findByType(BASIC_USER)).thenReturn(Optional.of(Role.of(BASIC_USER)));
        Optional<RoleValue> result = roleService.findOneOptionalByType(BASIC_USER);
        assertThat(result, is(equalTo(Optional.of(RoleValue.of(BASIC_USER)))));
    }

    @Test
    public void findOneOptionalByTypeTest_roleNotFound_shouldReturnEmptyOptional() {
        when(roleRepository.findByType(BASIC_USER)).thenReturn(Optional.empty());
        Optional<RoleValue> result = roleService.findOneOptionalByType(BASIC_USER);
        assertThat(result, is(equalTo(Optional.empty())));
    }

    @Test
    public void findAllByTypesTest() {
        var roleTypes = EnumSet.of(BASIC_USER, PRO_USER, ADMIN);
        var roles = Set.of(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN));
        when(roleRepository.findByTypeIn(roleTypes)).thenReturn(roles);
        Set<RoleValue> result = roleService.findAllByTypes(roleTypes);
        Assert.assertThat(result, hasItems(RoleValue.of(BASIC_USER), RoleValue.of(PRO_USER), RoleValue.of(ADMIN)));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findAllByTypesTest_rolesNotFound_shouldThrowException() {
        var roleTypes = EnumSet.of(BASIC_USER, PRO_USER, ADMIN);
        when(roleRepository.findByTypeIn(roleTypes)).thenReturn(emptySet());
        roleService.findAllByTypes(roleTypes);
    }

    @Test
    public void findAllTest() {
        var roles = List.of(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN));
        when(roleRepository.findAll()).thenReturn(roles);
        Set<RoleValue> result = roleService.findAll();
        Assert.assertThat(result, hasItems(RoleValue.of(BASIC_USER), RoleValue.of(PRO_USER), RoleValue.of(ADMIN)));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findAllTest_rolesNotFound_shouldThrowException() {
        when(roleRepository.findAll()).thenReturn(emptyList());
        roleService.findAll();
    }

    @Test
    public void saveTest() {
        var role = Role.of(BASIC_USER);
        var roleValue = RoleValue.of(BASIC_USER);
        roleService.save(roleValue);
        verify(roleRepository, times(1)).save(role);
    }

}
