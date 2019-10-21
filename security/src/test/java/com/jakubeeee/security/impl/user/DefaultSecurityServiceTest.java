package com.jakubeeee.security.impl.user;

import com.jakubeeee.common.persistence.DatabaseResultEmptyException;
import com.jakubeeee.security.impl.role.RoleService;
import com.jakubeeee.security.impl.role.RoleValue;
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

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static com.jakubeeee.common.reflection.ReflectUtils.getFieldValue;
import static com.jakubeeee.security.impl.role.RoleType.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("all")
@Category(FlowControlUnitTestCategory.class)
@RunWith(SpringRunner.class)
public class DefaultSecurityServiceTest {

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
            return new DefaultSecurityService(roleService, userRepository, passwordEncoder);
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
    private static UserValue TEST_USER_VALUE;

    @BeforeClass
    public static void setUp() throws Exception {
        TEST_ID = 1;
        TEST_USERNAME = "testUsername";
        TEST_PASSWORD = "testPassword";
        TEST_ENCODED_PASSWORD = "testEncodedPassword";
        TEST_EMAIL = "testEmail";
        TEST_USER = new User(TEST_USERNAME, TEST_ENCODED_PASSWORD, TEST_EMAIL);
        TEST_USER.setId(TEST_ID);
        TEST_USER_VALUE = new UserValue(TEST_ID, TEST_USERNAME, TEST_ENCODED_PASSWORD, TEST_EMAIL, true,
                Set.of(RoleValue.of(BASIC_USER)));
    }

    @Before
    public void setUpForEveryTest() throws Exception {
        roleService = getFieldValue(securityService, RoleService.class);
        userRepository = getFieldValue(securityService, UserRepository.class);
        passwordEncoder = getFieldValue(securityService, PasswordEncoder.class);
    }

    @Test
    public void createUserTest_oneRoleTypePassed() throws Exception {
        var roleTypes = EnumSet.of(BASIC_USER);
        var roles = Set.of(RoleValue.of(BASIC_USER));
        when(roleService.findAllByTypes(roleTypes)).thenReturn(roles);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        securityService.createUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        verify(roleService, times(1)).resolveRolesToAssign(roleTypes);
        verify(userRepository, times(1)).save(TEST_USER);
    }

    @Test
    public void createUserTest_multipleRoleTypesPassed() throws Exception {
        var roleTypes = EnumSet.of(BASIC_USER, PRO_USER, ADMIN);
        var roles = Set.of(RoleValue.of(BASIC_USER), RoleValue.of(PRO_USER), RoleValue.of(ADMIN));
        when(roleService.findAllByTypes(roleTypes)).thenReturn(roles);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODED_PASSWORD);
        securityService.createUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL, roleTypes);
        verify(roleService, times(1)).resolveRolesToAssign(roleTypes);
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
        UserValue user = securityService.findByUsername(TEST_USERNAME);
        assertThat(user, is(equalTo(TEST_USER_VALUE)));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findByUsernameTest_userNotFound_shouldThrowException() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        securityService.findByUsername(TEST_USERNAME);
    }

    @Test
    public void findByEmailTest() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(TEST_USER));
        UserValue user = securityService.findByEmail(TEST_EMAIL);
        assertThat(user, is(equalTo(TEST_USER_VALUE)));
    }

    @Test(expected = DatabaseResultEmptyException.class)
    public void findByEmailTest_userNotFound_shouldThrowException() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        securityService.findByEmail(TEST_EMAIL);
    }

}
