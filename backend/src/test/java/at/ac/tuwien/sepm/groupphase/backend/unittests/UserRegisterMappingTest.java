package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserRegisterMappingTest {

    private final ApplicationUser applicationUser =
        new ApplicationUser("martin@email.com", "Martin", "Gerdenich", LocalDate.parse("1999-12-12"), "Teststraße", 1010L, "Vienna", "Password123%", false,false);

    private final UserRegisterDto userRegisterDtoActual =
        new UserRegisterDto(1L, "martin@email.com", "Martin", "Gerdenich", LocalDate.parse("1999-12-12"), "Teststraße", 1010L, "Vienna", "Password123%");

    private final UserCreateDto userCreateDtoActual =
        new UserCreateDto(1L, "martin@email.com", "Martin", "Gerdenich", LocalDate.parse("1999-12-12"), "Teststraße", 1010L, "Vienna", "Password123%",
            false,false);
    @Autowired
    private UserMapper userMapper;

    @Test
    public void givenAUserRegisterDto_WhenMapped() {
        UserRegisterDto userRegisterDtoMapped = userMapper.entityToDto(applicationUser);

        assertAll(
            () -> assertEquals(userRegisterDtoActual.email(), userRegisterDtoMapped.email()),
            () -> assertEquals(userRegisterDtoActual.firstName(), userRegisterDtoMapped.firstName()),
            () -> assertEquals(userRegisterDtoActual.lastName(), userRegisterDtoMapped.lastName()),
            () -> assertEquals(userRegisterDtoActual.birthdate(), userRegisterDtoMapped.birthdate()),
            () -> assertEquals(userRegisterDtoActual.address(), userRegisterDtoMapped.address()),
            () -> assertEquals(userRegisterDtoActual.areaCode(), userRegisterDtoMapped.areaCode()),
            () -> assertEquals(userRegisterDtoActual.cityName(), userRegisterDtoMapped.cityName()),
            () -> assertEquals(userRegisterDtoActual.password(), userRegisterDtoMapped.password())
        );
    }

    @Test
    public void givenAUserCreateDto_WhenMapped() {
        UserCreateDto userCreateDtoMapped = userMapper.entityToUserCreateDto(applicationUser);

        assertAll(
            () -> assertEquals(userCreateDtoActual.email(), userCreateDtoMapped.email()),
            () -> assertEquals(userCreateDtoActual.firstName(), userCreateDtoMapped.firstName()),
            () -> assertEquals(userCreateDtoActual.lastName(), userCreateDtoMapped.lastName()),
            () -> assertEquals(userCreateDtoActual.birthdate(), userCreateDtoMapped.birthdate()),
            () -> assertEquals(userCreateDtoActual.address(), userCreateDtoMapped.address()),
            () -> assertEquals(userCreateDtoActual.areaCode(), userCreateDtoMapped.areaCode()),
            () -> assertEquals(userCreateDtoActual.cityName(), userCreateDtoMapped.cityName()),
            () -> assertEquals(userCreateDtoActual.password(), userCreateDtoMapped.password())
        );
    }

}
