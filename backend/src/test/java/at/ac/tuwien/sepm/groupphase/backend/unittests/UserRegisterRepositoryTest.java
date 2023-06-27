package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)

@DataJpaTest
@ActiveProfiles("test")
public class UserRegisterRepositoryTest {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Test
    public void givenAUser_whenSaveUser_thenFindUser() {
        ApplicationUser applicationUser =
            new ApplicationUser("martin@email.com", "Martin", "Gerdenich", LocalDate.parse("1999-12-12"), "Teststraße", 1010L, "Vienna", "Password123%",
                false, false);
        applicationUserRepository.save(applicationUser);

        Optional<ApplicationUser> testApplicationUser = applicationUserRepository.findById(applicationUser.getId());

        assertAll(
            () -> assertEquals(1, applicationUserRepository.findAll().size()),
            () -> assertNotNull(applicationUserRepository.findById(applicationUser.getId())),
            () -> assertEquals("Martin", testApplicationUser.get().getFirstName()),
            () -> assertEquals("Gerdenich", testApplicationUser.get().getLastName()),
            () -> assertEquals("1999-12-12", testApplicationUser.get().getBirthdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))),
            () -> assertEquals("Teststraße", testApplicationUser.get().getAddress()),
            () -> assertEquals(1010L, testApplicationUser.get().getAreaCode()),
            () -> assertEquals("Vienna", testApplicationUser.get().getCityName()),
            () -> assertEquals("Password123%", testApplicationUser.get().getPassword())
        );
    }
}
