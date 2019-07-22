package com.jakubeeee.security.service;

import com.jakubeeee.common.exception.DatabaseResultEmptyException;
import com.jakubeeee.security.entity.Role;
import com.jakubeeee.security.entity.User;
import com.jakubeeee.security.exception.EmailNotUniqueException;
import com.jakubeeee.security.exception.UsernameNotUniqueException;
import com.jakubeeee.security.repository.UserRepository;
import com.jakubeeee.testutils.marker.FlowControlUnitTestCategory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Set;

import static com.jakubeeee.common.util.ReflectUtils.getFieldValue;
import static com.jakubeeee.security.entity.Role.Type.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("all")
@Category(FlowControlUnitTestCategory.class)
@RunWith(SpringRunner.class)
public class SecurityServiceTest {

    @TestConfiguration
    static class TestContextConfig {

        @MockBean
        private RoleService roleService;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private PasswordEncoder passwordEncoder;

        @Bean
        public SecurityService securityService() {
            return new SecurityService(roleService, userRepository, passwordEncoder);
        }

    }

    @Autowired
    private SecurityService securityService;

    private RoleService roleService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private static long TEST_ID;
    private static String TEST_USERNAME;
    private static String TEST_PASSWORD;
    private static String TEST_ENCODED_PASSWORD;
    private static String TEST_EMAIL;
    private static User TEST_USER;

    @BeforeClass
    public static void setUp() {
        TEST_ID = 1;
        TEST_USERNAME = "testUsername";
        TEST_PASSWORD = "testPassword";
        TEST_ENCODED_PASSWORD = "testEncodedPassword";
        TEST_EMAIL = "testEmail";
        TEST_USER = new User(TEST_USERNAME, TEST_ENCODED_PASSWORD, TEST_EMAIL);
    }

    @Before
    public void setUpForEveryTest() throws Exception {
        roleService = getFieldValue(securityService, RoleService.class);
        userRepository = getFieldValue(securityService, UserRepository.class);
        passwordEncoder = getFieldValue(securityService, PasswordEncoder.class);
    }

    @Test
    public void createUserTest_noRoleTypesPassed() throws Exception {
        var roleTypes = Set.of(BASIC_USER);
        var roles = Set.of(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN));
        when(roleService.findAllByTypes(roleTypes)).thenReturn(roles);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        securityService.createUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        verify(roleService, times(1)).grantRoles(TEST_USER, roles);
        verify(userRepository, times(1)).save(TEST_USER);
    }

    @Test
    public void createUserTest_roleTypesPassed() throws Exception {
        var roleTypes = Set.of(BASIC_USER, PRO_USER, ADMIN);
        var roles = Set.of(Role.of(BASIC_USER), Role.of(PRO_USER), Role.of(ADMIN));
        when(roleService.findAllByTypes(roleTypes)).thenReturn(roles);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        securityService.createUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL, roleTypes);
        verify(roleService, times(1)).grantRoles(TEST_USER, roles);
        verify(userRepository, times(1)).save(TEST_USER);
    }

    @Test(expected = UsernameNotUniqueException.class)
    public void createUserTest_nonUniqueUsername_shouldThrowException() {
        SecurityService securityServiceSpy = spy(securityService);
        doReturn(false).when(securityServiceSpy).isUsernameUnique(TEST_USERNAME);
        securityServiceSpy.createUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
    }

    @Test(expected = EmailNotUniqueException.class)
    public void createUserTest_nonUniqueEmail_shouldThrowException() {
        SecurityService securityServiceSpy = spy(securityService);
        doReturn(false).when(securityServiceSpy).isEmailUnique(TEST_EMAIL);
        securityServiceSpy.createUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
    }

    @Test
    public void updateUserPasswordTest() {
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        securityService.updateUserPassword(TEST_ID, TEST_PASSWORD);
        verify(userRepository, times(1)).updateUserPassword(TEST_ID, TEST_ENCODED_PASSWORD);
    }

    @Test
    public void isUsernameUniqueTest_Unique_shouldReturnTrue() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        boolean usernameUnique = securityService.isUsernameUnique(TEST_USERNAME);
        assertThat(usernameUnique, is(equalTo(true)));
    }

    @Test
    public void isUsernameUniqueTest_nonUnique_shouldReturnFalse() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(TEST_USER));
        boolean usernameUnique = securityService.isUsernameUnique(TEST_USERNAME);
        assertThat(usernameUnique, is(equalTo(false)));
    }

    @Test
    public void isEmailUniqueTest_Unique_shouldReturnTrue() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        boolean emailUnique = securityService.isEmailUnique(TEST_EMAIL);
        assertThat(emailUnique, is(equalTo(true)));
    }

    @Test
    public void isEmailUniqueTest_nonUnique_shouldReturnFalse() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(TEST_USER));
        boolean emailUnique = securityService.isEmailUnique(TEST_EMAIL);
        assertThat(emailUnique, is(equalTo(false)));
    }

    @Test
    public void findByUsernameTest() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(TEST_USER));
        User user = securityService.findByUsername(TEST_USERNAME);
        assertThat(user, is(equalTo(TEST_USER)));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findByUsernameTest_userNotFound_shouldThrowException() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        securityService.findByUsername(TEST_USERNAME);
    }

    @Test
    public void findByEmailTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(TEST_USER));
        User user = securityService.findByEmail(TEST_EMAIL);
        assertThat(user, is(equalTo(TEST_USER)));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findByEmailTest_userNotFound_shouldThrowException() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        securityService.findByEmail(TEST_EMAIL);
    }

}
