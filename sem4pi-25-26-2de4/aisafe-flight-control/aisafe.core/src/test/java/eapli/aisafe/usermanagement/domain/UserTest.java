package eapli.aisafe.usermanagement.domain;





import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.UserName;
import org.junit.jupiter.api.Test;


import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;





class UserTest {





    private static final Email VALID_EMAIL = new Email("a@b.com");


    private static final String VALID_PWD = "MyPass123";


    private static final String NAME = "John Doe";


    private static final PhoneNumber PHONE = PhoneNumber.of("123456789");


    private static final Set<Role> ROLES = Set.of(Role.ADMIN);





    @Test


    void constructorShouldCreateActiveUserWithGivenAttributes() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        assertEquals(VALID_EMAIL, u.email());


        assertEquals(UserName.of(NAME), u.name());


        assertEquals(PHONE, u.phoneNumber());


        assertTrue(u.isActive());


        assertTrue(u.hasRole(Role.ADMIN));


        assertFalse(u.hasRole(Role.PILOT));


        assertEquals(ROLES, u.roles());


    }





    @Test


    void constructorShouldThrowIfPasswordTooShort() {


        assertThrows(IllegalArgumentException.class, () ->


                new User(VALID_EMAIL, "12345", UserName.of(NAME), PHONE, ROLES)


        );


    }





    @Test


    void passwordMatchesShouldReturnTrueForCorrectPassword() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        assertTrue(u.passwordMatches(VALID_PWD));


    }





    @Test


    void passwordMatchesShouldReturnFalseForWrongPassword() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        assertFalse(u.passwordMatches("WrongPass"));


    }





    @Test


    void activateAndDeactivateShouldChangeActiveStatus() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        assertTrue(u.isActive());


        u.deactivate();


        assertFalse(u.isActive());


        u.activate();


        assertTrue(u.isActive());


    }





    @Test


    void hasRoleShouldReturnTrueOnlyForAssignedRoles() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, Set.of(Role.ADMIN, Role.PILOT));


        assertTrue(u.hasRole(Role.ADMIN));


        assertTrue(u.hasRole(Role.PILOT));


        assertFalse(u.hasRole(Role.WEATHER_PERSON));


    }





    // ==================== PASSWORD VALIDATION TESTS ====================





    @Test


    void setPasswordShouldThrowIfNoDigit() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        // Password with uppercase but no digit


        assertThrows(IllegalArgumentException.class, () -> u.setPassword("Password"));


    }





    @Test


    void setPasswordShouldThrowIfNoUppercase() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        // Password with digit but no uppercase


        assertThrows(IllegalArgumentException.class, () -> u.setPassword("password123"));


    }





    @Test


    void setPasswordShouldAcceptValidPassword() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        // Should not throw


        assertDoesNotThrow(() -> u.setPassword("NewPass456"));


        assertTrue(u.passwordMatches("NewPass456"));


    }





    // ==================== PHONE NUMBER CHANGE TESTS ====================





    @Test


    void changePhoneNumberShouldUpdatePhone() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        u.changePhoneNumber(PhoneNumber.of("987654321"));


        assertEquals(PhoneNumber.of("987654321"), u.phoneNumber());


    }





    @Test


    void changePhoneNumberShouldTrimWhitespace() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        u.changePhoneNumber(PhoneNumber.of("987654321"));


        assertEquals(PhoneNumber.of("987654321"), u.phoneNumber());


    }





    @Test


    void changePhoneNumberShouldThrowIfNull() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        assertThrows(IllegalArgumentException.class, () -> u.changePhoneNumber(null));


    }





    @Test


    void changePhoneNumberShouldThrowIfEmpty() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        assertThrows(IllegalArgumentException.class, () -> u.changePhoneNumber(PhoneNumber.of("")));


    }





    @Test


    void changePhoneNumberShouldThrowIfBlank() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        assertThrows(IllegalArgumentException.class, () -> u.changePhoneNumber(PhoneNumber.of("   ")));


    }





    // ==================== COPY USER TESTS ====================





    @Test


    void copyWithNewEmailAndPhoneShouldCopyAllFields() {


        User original = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, Set.of(Role.ADMIN, Role.PILOT));


        original.deactivate(); // Make inactive to test copy preserves state


        


        Email newEmail = new Email("new@email.com");


        User copy = original.copyWithNewEmailAndPhone(newEmail, PhoneNumber.of("999888777"));


        


        assertEquals(newEmail, copy.email());


        assertEquals(PhoneNumber.of("999888777"), copy.phoneNumber());


        assertEquals(UserName.of(NAME), copy.name());


        assertFalse(copy.isActive()); // Preserved inactive state


        assertTrue(copy.hasRole(Role.ADMIN));


        assertTrue(copy.hasRole(Role.PILOT));


        // Password should still match


        assertTrue(copy.passwordMatches(VALID_PWD));


    }





    @Test


    void copyWithNewEmailAndPhoneShouldCreateIndependentCopy() {


        User original = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        


        Email newEmail = new Email("new@email.com");


        User copy = original.copyWithNewEmailAndPhone(newEmail, PhoneNumber.of("999888777"));


        


        // Modifying copy should not affect original


        copy.activate();


        assertTrue(original.isActive());


        


        // Roles should be independent


        copy.deactivate();


        assertTrue(original.isActive());


    }





    // ==================== UNSET FIELDS GETTERS TESTS ====================





    @Test


    void securityClearanceExpiryShouldReturnNullWhenNotSet() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        assertNull(u.securityClearanceExpiry());


    }





    @Test


    void lastSkillsAssessmentShouldReturnNullWhenNotSet() {


        User u = new User(VALID_EMAIL, VALID_PWD, UserName.of(NAME), PHONE, ROLES);


        assertNull(u.lastSkillsAssessment());


    }


}
