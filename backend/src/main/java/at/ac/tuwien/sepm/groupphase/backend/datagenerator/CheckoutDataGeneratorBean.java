package at.ac.tuwien.sepm.groupphase.backend.datagenerator;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

@Component
@Profile("checkout-test-data")
public class CheckoutDataGeneratorBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DataSource dataSource;

    public CheckoutDataGeneratorBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void generateData() throws SQLException {
        LOGGER.info("Generating checkout-test-data...");
        try (var connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/deleteAll.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertHallplans.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertEvents.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertNewsEntries.sql"));
            LOGGER.info("Finished generating checkout-test-data without error.");
        }
    }
}
