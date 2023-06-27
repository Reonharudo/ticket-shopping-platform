package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.AbbreviatedHallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.DetailedHallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatBulkDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatRowBulkDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSectionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HallPlanMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HallPlanSectionMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanSeatService;
import at.ac.tuwien.sepm.groupphase.backend.service.HallPlanService;
import at.ac.tuwien.sepm.groupphase.backend.service.SeatRowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/hallplans")
public class HallPlanEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final HallPlanService hallPlanService;
    private final HallPlanSeatService hallPlanSeatService;

    private final HallPlanMapper hallPlanMapper;
    private final HallPlanSectionMapper hallPlanSectionMapper;
    private final SeatRowService seatRowService;

    @Autowired
    public HallPlanEndpoint(HallPlanService hallPlanService, HallPlanSeatService hallPlanSeatService, HallPlanMapper hallPlanMapper, HallPlanSectionMapper hallPlanSectionMapper, SeatRowService seatRowService) {
        this.hallPlanService = hallPlanService;
        this.hallPlanMapper = hallPlanMapper;
        this.hallPlanSectionMapper = hallPlanSectionMapper;
        this.seatRowService = seatRowService;
        this.hallPlanSeatService = hallPlanSeatService;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get a list of all hall plans", security = @SecurityRequirement(name = "apiKey"))
    public List<HallPlanDto> findAll(HallPlanSearchDto searchDto) {
        LOGGER.info("GET /api/v1/hallplans");
        return hallPlanMapper.hallPlanToHallPlanDto(hallPlanService.searchHallPlans(searchDto));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create a new hall plan entry in the system", security = @SecurityRequirement(name = "apiKey"))
    public HallPlanDto createHallPlan(@RequestBody HallPlanDto hallplan) {
        LOGGER.info("POST /api/v1/hallplans with {}", hallplan);
        try {
            return hallPlanMapper.hallPlanToHallPlanDto(hallPlanService.createHallPlan(hallplan));
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/snapshot")
    @Operation(summary = "Create snapshot of given hallplan", security = @SecurityRequirement(name = "apiKey"))
    public DetailedHallPlanDto createSnapshot(@PathVariable(name = "id") Long baseHallplanId, @RequestBody HallPlanDto hallplan) {
        LOGGER.info("POST /api/v1/hallplans/{}/snapshot", baseHallplanId);
        try {
            return hallPlanService.snapshotHallplan(hallplan, baseHallplanId);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @Operation(summary = "Get a hall plan by id", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<DetailedHallPlanDto> getHallPlanById(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/hallplans/{}", id);
        DetailedHallPlanDto hallPlanDto = hallPlanService.getHallPlanById(id);
        if (hallPlanDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(hallPlanDto);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hall plan by id", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Void> deleteHallPlanById(@PathVariable Long id) {
        LOGGER.info("DELETE /api/v1/hallplans/{}", id);
        hallPlanService.deleteHallPlanById(id);
        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    @Operation(summary = "Update a hall plan by id", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<HallPlanDto> updateHallPlanById(@PathVariable Long id, @RequestBody HallPlanDto hallPlanDto) {
        LOGGER.info("PUT /api/v1/hallplans/{}", id);
        HallPlanDto updatedHallPlanDto = hallPlanService.updateHallPlanById(id, hallPlanDto);
        if (updatedHallPlanDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedHallPlanDto);
    }

    @Secured("ROLE_USER")
    @GetMapping("/search")
    @Operation(summary = "Get list of hallplans without details", security = @SecurityRequirement(name = "apiKey"))
    public List<AbbreviatedHallPlanDto> searchHallPlanPages(@RequestParam(defaultValue = "0") int pageIndex, @RequestParam(required = false) String search) {
        LOGGER.info("GET {}/hallplansForSearch", search);
        LOGGER.info("called without param");
        return hallPlanService.findPageOfHallplans(pageIndex, search)
            .map(hallPlanMapper::hallPlanToAbbreviatedHallPlanDto)
            .toList();
    }

    @Secured("ROLE_USER")
    @GetMapping("/byId")
    @Operation(summary = "Get a hallplans without details by id", security = @SecurityRequirement(name = "apiKey"))
    public AbbreviatedHallPlanDto getAbbreviatedHallPlanById(@RequestParam(required = true) Long id) {
        LOGGER.info("GET {}/getByIdAbbreviatedHallPlan", id);
        LOGGER.info("called without param");
        return hallPlanMapper.hallPlanToAbbreviatedHallPlanDto(hallPlanMapper.detailedHallPlanDtoToHallPlan(hallPlanService.getHallPlanById(id)));

    }

    //*******************************************************
    //*                  SeatRow Mappings                   *
    //*******************************************************
    @Secured("ROLE_ADMIN")
    @PostMapping("{hallPlanId}/seatrows")
    @Operation(summary = "Create a new seat row in the system", security = @SecurityRequirement(name = "apiKey"))
    public SeatRowDto createSeatRow(@PathVariable Long hallPlanId, @RequestBody SeatRowDto seatRowDto) {
        LOGGER.info("POST /api/v1/{}/seatrows", hallPlanId);
        seatRowDto.setHallPlanId(hallPlanId);
        try {
            return seatRowService.createSeatRow(seatRowDto);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("{hallPlanId}/seatrows/bulk")
    @Operation(summary = "Create a new set of seat rows in the system", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<List<SeatRowDto>> bulkCreateSeatRow(@PathVariable Long hallPlanId, @RequestBody HallPlanSeatRowBulkDto seatRowBulkDto) {
        LOGGER.info("POST /api/v1/{}/seatrows/bulk", hallPlanId);
        seatRowBulkDto.setHallPlanId(hallPlanId);
        return ResponseEntity.ok(seatRowService.bulkCreateSeatRow(seatRowBulkDto));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("{hallPlanId}/seatrows/bulk")
    @Operation(summary = "Create a new set of seat rows in the system", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<List<SeatRowDto>> bulkUpdateSeatRow(@PathVariable Long hallPlanId, @RequestBody HallPlanSeatRowBulkDto seatRowBulkDto) {
        LOGGER.info("PUT /api/v1/{}/seatrows/bulk", hallPlanId);
        seatRowBulkDto.setHallPlanId(hallPlanId);
        return ResponseEntity.ok(seatRowService.bulkUpdateSeatRow(seatRowBulkDto));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("{hallplanId}/seatrows/{id}")
    @Operation(summary = "Delete a seat row from the system", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Void> deleteSeatRow(@PathVariable Long hallplanId, @PathVariable Long id) {
        LOGGER.info("DELETE /api/v1/hallplans/seatrows/{}", id);
        boolean deleted = seatRowService.deleteSeatRowById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("{hallplanId}/seatrows/{id}")
    @Operation(summary = "Update an existing seat row in the system", security = @SecurityRequirement(name = "apiKey"))
    public SeatRowDto updateSeatRow(@PathVariable Long hallplanId, @PathVariable Long id, @RequestBody SeatRowDto seatRowDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/hallplans/seatrows/{}", id);
        seatRowDto.setId(id);
        seatRowService.updateSeatRow(seatRowDto);
        return seatRowDto;
    }

    @Secured("ROLE_USER")
    @GetMapping("{hallplanId}/seatrows/{id}")
    @Operation(summary = "Get a seat row by id", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<SeatRowDto> getSeatRowById(@PathVariable Long hallplanId, @PathVariable Long id) {
        LOGGER.info("GET /api/v1/hallplans/{}/seatrows/{}", hallplanId, id);
        SeatRowDto seatRowDto = seatRowService.getSeatRowById(id);
        return seatRowDto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(seatRowDto);
    }

    @Secured("ROLE_USER")
    @GetMapping("{hallPlanId}/seatrows")
    @Operation(summary = "Get all seat rows of a Hallplan, in ascending rowNr order", security = @SecurityRequirement(name = "apiKey"))
    public List<SeatRowDto> getSeatRowByHallplanId(@PathVariable Long hallPlanId) {
        LOGGER.info("GET /api/v1/{}/seatrows", hallPlanId);
        return seatRowService.findAllSeatRowsOfHallPlan(hallPlanId);
    }

    @Secured("ROLE_USER")
    @GetMapping("/seatrows")
    @Operation(summary = "Get all seat rows in the system", security = @SecurityRequirement(name = "apiKey"))
    public List<SeatRowDto> findAllSeatRows() {
        LOGGER.info("GET /api/v1/seatrows");
        return seatRowService.findAllSeatRows();
    }

    //*******************************************************
    //*                   Section Mappings                  *
    //*******************************************************
    @Secured("ROLE_USER")
    @GetMapping("{hallplanId}/sections")
    @Operation(summary = "Get all sections in hallplan", security = @SecurityRequirement(name = "apiKey"))
    public List<HallPlanSectionDto> findAllSectionsByHallPlanId(@PathVariable Long hallplanId) {
        LOGGER.info("GET /api/v1/{}/sections", hallplanId);
        return hallPlanService.findAllSectionsByHallPlanIdWithCounts(hallplanId);
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("{hallplanId}/sections")
    @Operation(summary = "Create a new section in the system", security = @SecurityRequirement(name = "apiKey"))
    public HallPlanSectionDto createSection(@RequestBody HallPlanSectionDto section, @PathVariable Long hallplanId) {
        LOGGER.info("POST /api/v1/sections");
        section.setHallPlanId(hallplanId);
        return hallPlanSectionMapper.toDto(hallPlanService.createSection(section));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("{hallplanId}/sections/{id}")
    @Operation(summary = "Update a section in the system", security = @SecurityRequirement(name = "apiKey"))
    public HallPlanSectionDto updateSection(@PathVariable Long id, @RequestBody HallPlanSectionDto section, @PathVariable Long hallplanId) {
        LOGGER.info("PUT /api/v1/sections/{}", id);
        section.setHallPlanId(hallplanId);
        return hallPlanSectionMapper.toDto(hallPlanService.updateSection(id, section));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/sections/{id}")
    @Operation(summary = "Delete a section from the system", security = @SecurityRequirement(name = "apiKey"))
    public void deleteSection(@PathVariable Long id) {
        LOGGER.info("DELETE /api/v1/sections/{}", id);
        hallPlanService.deleteSection(id);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/sections/{id}")
    @Operation(summary = "Get a section from the system")
    public HallPlanSectionDto getSection(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/hallplans/sections/{}", id);
        return hallPlanSectionMapper.toDto(hallPlanService.getSection(id));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/sections")
    @Operation(summary = "Get all sections from the system")
    public List<HallPlanSectionDto> getAllSections() {
        LOGGER.info("GET /api/v1/hallplans/sections");
        return hallPlanService.getAllSections().stream()
            .map(hallPlanSectionMapper::toDto)
            .collect(Collectors.toList());
    }


    //*******************************************************
    //*                     Seat Mappings                   *
    //*******************************************************

    @Secured("ROLE_ADMIN")
    @PostMapping("/{hallPlanId}/seatrows/{seatRowId}/seats")
    @Operation(summary = "Add a new seat to a seat row")
    public ResponseEntity<HallPlanSeatDto> addSeat(@PathVariable Long hallPlanId, @PathVariable Long seatRowId, @Valid @RequestBody HallPlanSeatDto seatDto) {
        LOGGER.info("POST /api/v1/hallplans/{}/seatrows/{}/seats", hallPlanId, seatRowId);
        SeatRowDto seatRowDto = new SeatRowDto();
        seatRowDto.setHallPlanId(hallPlanId);
        seatRowDto.setId(seatRowId);
        seatDto.setSeatrowId(seatRowId);
        HallPlanSeatDto savedSeat = hallPlanSeatService.addSeat(seatDto);
        return ResponseEntity.created(URI.create("/api/v1/hallplans/" + hallPlanId + "/seatrows/" + seatRowId + "/seats/" + seatDto.getId())).body(savedSeat);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{hallPlanId}/seatrows/{seatRowId}/seats/bulk")
    @Operation(summary = "Add a new seat to a seat row")
    public ResponseEntity<HallPlanSeatBulkDto> bulkAddSeat(@PathVariable Long hallPlanId, @PathVariable Long seatRowId, @RequestBody HallPlanSeatBulkDto seatBulk) {
        seatBulk.setHallPlanId(hallPlanId);
        seatBulk.setSeatRowId(seatRowId);
        hallPlanSeatService.bulkAddSeats(seatBulk);
        return ResponseEntity.created(URI.create("/api/v1/hallplans/" + hallPlanId + "/seatrows/" + seatRowId + "/seats/bulk")).body(seatBulk);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{hallPlanId}/seats/bulk")
    @Operation(summary = "Add a new seat to a seat row")
    public ResponseEntity<HallPlanSeatBulkDto> bulkUpdateSeats(@PathVariable Long hallPlanId, @RequestBody HallPlanSeatBulkDto seatBulk) {
        seatBulk.setHallPlanId(hallPlanId);
        hallPlanSeatService.bulkUpdateSeats(seatBulk);
        return ResponseEntity.created(URI.create("/api/v1/hallplans/" + hallPlanId + "/seats/bulk")).body(seatBulk);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("{hallPlanId}/seatrows/{seatRowId}/seats/{id}")
    @Operation(summary = "Delete a seat by id from a seat row")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long hallPlanId, @PathVariable Long seatRowId, @PathVariable Long id) {
        LOGGER.info("DELETE /api/v1/hallplans/{}/seatrows/{}/seats/{}", hallPlanId, seatRowId, id);
        hallPlanSeatService.deleteSeat(id);
        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("{hallPlanId}/seatrows/{seatRowId}/seats/{id}")
    @Operation(summary = "Update a seat by id in a seat row")
    public ResponseEntity<HallPlanSeatDto> updateSeat(@PathVariable Long hallPlanId, @PathVariable Long seatRowId, @PathVariable Long id, @Valid @RequestBody HallPlanSeatDto seatDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/hallplans/{}/seatrows/{}/seats/{}", hallPlanId, seatRowId, id);
        seatDto.setId(id);
        seatDto.setSeatrowId(seatRowId);
        hallPlanSeatService.updateSeat(seatDto);
        return ResponseEntity.ok(seatDto);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/seatrows/seats/{id}")
    @Operation(summary = "Get a seat by id from a seat row")
    public ResponseEntity<HallPlanSeatDto> getSeatById(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/hallplans/seatrows/seats/{}", id);
        HallPlanSeatDto seatDto = hallPlanSeatService.getSeatById(id);
        return ResponseEntity.ok(seatDto);
    }
}
