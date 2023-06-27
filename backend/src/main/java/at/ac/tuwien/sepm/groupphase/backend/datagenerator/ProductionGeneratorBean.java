package at.ac.tuwien.sepm.groupphase.backend.datagenerator;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSectionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.SeatRowService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.HallPlanServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.type.HallPlanSeatStatus;
import at.ac.tuwien.sepm.groupphase.backend.type.HallPlanSeatType;
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
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("production")
public class ProductionGeneratorBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DataSource dataSource;

    private final CustomUserDetailService userDetailService;

    private final HallPlanServiceImpl hallPlanService;

    private final SeatRowService seatRowService;

    private final PasswordEncoder passwordEncoder;

    public ProductionGeneratorBean(DataSource dataSource, CustomUserDetailService userDetailService, PasswordEncoder passwordEncoder, HallPlanServiceImpl hallPlanService, SeatRowService seatRowService) {
        this.dataSource = dataSource;
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
        this.hallPlanService = hallPlanService;
        this.seatRowService = seatRowService;
    }

    @PostConstruct
    public void generateData() throws SQLException, ValidationException {
        LOGGER.info("Generating hall plan data...");
        try (var connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/deleteAll.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertHallplansProduction.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertEvents.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertNewsEntries.sql"));
            LOGGER.info("Finished generating data without error.");
        }
        List<HallPlanSeatDto> seats;
        HallPlanSeatDto seatDto;
        //Outer loop determines row count
        for (int g = 1; g < 8; g++) {


            for (int i = 1; i < 11; i++) {
                SeatRowDto seatRowDto = new SeatRowDto();
                seatRowDto.setHallPlanId((long) -g);
                seatRowDto.setRowNr((long) i);
                //Generate seats
                seats = new ArrayList<>();
                for (int z = 1; z < 30; z++) {
                    seatDto = new HallPlanSeatDto();
                    seatDto.setReservedNr(0L);
                    seatDto.setBoughtNr(0L);
                    List<HallPlanSectionDto> hallPlanSectionDto = this.hallPlanService.findAllSectionsByHallPlanIdWithCounts(-1L);
                    seatDto.setSection(hallPlanSectionDto.get(i % 6));
                    if (i > 5) {
                        seatDto.setSection(hallPlanSectionDto.get(7));
                    }

                    //Set individual seat parameter
                    seatDto.setOrderNr((long) z);
                    seatDto.setSeatNr((long) z);
                    seatDto.setCapacity(50L);
                    seatDto.setStatus(HallPlanSeatStatus.FREE);
                    seatDto.setType(HallPlanSeatType.SEAT);
                    seats.add(seatDto);
                }
                seatRowDto.setSeats(seats);
                if (i % 5 == 0) {
                    seats = new ArrayList<>();
                    for (int p = 1; p < 4; p++) {
                        seatDto = new HallPlanSeatDto();
                        seatDto.setReservedNr(0L);
                        seatDto.setBoughtNr(0L);
                        List<HallPlanSectionDto> hallPlanSectionDto = this.hallPlanService.findAllSectionsByHallPlanIdWithCounts(-1L);
                        seatDto.setSection(hallPlanSectionDto.get(i % 2));

                        //Set individual seat parameter
                        seatDto.setOrderNr((long) p + ((p - 1) * 7));
                        seatDto.setSeatNr((long) p);
                        seatDto.setCapacity(100L + (p * 50));
                        if (p == 3) {
                            seatDto.setCapacity(150L);
                        }
                        seatDto.setStatus(HallPlanSeatStatus.FREE);
                        seatDto.setType(HallPlanSeatType.STANDING_SEAT);
                        seats.add(seatDto);
                        if (p != 3) {
                            for (int u = 1; u < 8; u++) {
                                seatDto = new HallPlanSeatDto();
                                seatDto.setReservedNr(0L);
                                seatDto.setBoughtNr(0L);
                                seatDto.setSection(hallPlanSectionDto.get(i % 6));

                                //Set individual seat parameter
                                int addIncr = 0;
                                if (p == 1) {
                                    addIncr = 1;
                                } else {
                                    addIncr = 0;
                                }
                                seatDto.setOrderNr((long) u + (p - 1) * 7 + p);
                                seatDto.setSeatNr((long) -1);
                                seatDto.setCapacity(50L);
                                seatDto.setStatus(HallPlanSeatStatus.FREE);
                                seatDto.setType(HallPlanSeatType.VACANT_SEAT);
                                seats.add(seatDto);
                            }
                        }


                    }
                    seatRowDto.setSeats(seats);
                }

                LOGGER.info("Generated seatRow");
                if (i == 1) {
                    //Update first row instance
                    seatRowDto.setId((long) -g);
                    seatRowDto.setRowNr(1L);
                    this.seatRowService.updateSeatRow(seatRowDto);
                    continue;
                }
                this.seatRowService.createSeatRow(seatRowDto);
            }
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

    @PostConstruct
    public void generateHallPlanData() throws ValidationException {

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
