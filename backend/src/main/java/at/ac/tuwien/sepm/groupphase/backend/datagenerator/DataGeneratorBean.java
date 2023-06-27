package at.ac.tuwien.sepm.groupphase.backend.datagenerator;


import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
@Profile("datagen")
public class DataGeneratorBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DataSource dataSource;

    private final CustomUserDetailService userDetailService;

    private final PasswordEncoder passwordEncoder;

    public DataGeneratorBean(DataSource dataSource, CustomUserDetailService userDetailService, PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void generateData() throws SQLException {
        LOGGER.info("Generating hall plan data...");
        try (var connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/deleteAll.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertHallplans.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertEvents.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertNewsEntries.sql"));
            LOGGER.info("Finished generating data without error.");
        }
    }

    @PostConstruct
    public void generateInitialAdmin() throws SQLException, ValidationException {
        LOGGER.info("Generating initial admin...");


        ApplicationUser initialAdmin =
            new ApplicationUser("adminTest@email.com", "Admin", "Admin", LocalDate.parse("1999-12-12"), "Adminstreet", 1010L, "Vienna", "Password123%", true, false);
        ApplicationUser initialUser =
            new ApplicationUser("userTest@email.com", "User", "User", LocalDate.parse("1999-12-12"), "Userstreet", 1010L, "Vienna", "Password123%", false, false);
        try (var connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/deleteAllUsers.sql"));
            userDetailService.register(initialAdmin);
            userDetailService.register(initialUser);
            executeInsertUsersScript(connection);
        }
        LOGGER.info("Finished generating admin and user without error.");
    }

    private void executeInsertUsersScript(Connection connection) {
        try {
            Resource scriptResource = new ClassPathResource("sql/insertUsers.sql");
            String sqlScript = FileCopyUtils.copyToString(new InputStreamReader(scriptResource.getInputStream(), StandardCharsets.UTF_8));

            String plainTextPassword = "Password123%";

            String hashedPassword = passwordEncoder.encode(plainTextPassword);

            sqlScript = StringUtils.replace(sqlScript, "{Password123%}", hashedPassword);

            ScriptUtils.executeSqlScript(connection, new ByteArrayResource(sqlScript.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            LOGGER.error("Failed to execute script: {}", e.getMessage());
        }
    }
}
