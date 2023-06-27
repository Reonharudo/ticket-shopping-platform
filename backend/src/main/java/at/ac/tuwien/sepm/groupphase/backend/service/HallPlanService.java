package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.DetailedHallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan.HallPlanSectionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlanSection;
import jakarta.xml.bind.ValidationException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HallPlanService {

    /**
     * Find all hall plans stored in the system.
     *
     * @return Returns a list of hall plan entities
     */
    List<HallPlan> findAll();

    /**
     * Find all hall plans stored in the system which match the parameters.
     *
     * @param searchDto contains the search parameters name and description
     * @return Returns a list of hall plan entities
     */
    List<HallPlan> searchHallPlans(HallPlanSearchDto searchDto);

    /**
     * Creates a new hall plan.
     *
     * @param hallplan the hallPlanDto containing the information for the new hall plan
     * @return the created HallPlan entity
     * @throws ValidationException if the hallPlanDto is invalid
     */
    HallPlan createHallPlan(HallPlanDto hallplan) throws ValidationException;


    /**
     * Creates a new hall plan.
     *
     * @param baseHallplanId the hallPlanDto containing the information for the snapshoted hall plan
     * @return the created HallPlan entity with all tuples from its relationships from given basehallplan
     * @throws ValidationException if the hallPlanDto is invalid
     */
    DetailedHallPlanDto snapshotHallplan(HallPlanDto newHallplan, Long baseHallplanId) throws ValidationException;

    /**
     * Retrieves a detailed hall plan by its ID.
     *
     * @param id the ID of the hall plan to retrieve
     * @return the DetailedHallPlanDto associated with the given ID
     */
    DetailedHallPlanDto getHallPlanById(Long id);

    /**
     * Deletes a hall plan by its ID.
     *
     * @param id the ID of the hall plan to delete
     */
    void deleteHallPlanById(Long id);

    /**
     * Updates a hall plan by its ID.
     *
     * @param id          the ID of the hall plan to update
     * @param hallPlanDto the hallPlanDto containing the updated information
     * @return the updated HallPlanDto
     */
    HallPlanDto updateHallPlanById(Long id, HallPlanDto hallPlanDto);

    /**
     * Creates a new hall plan section.
     *
     * @param sectionDto the HallPlanSectionDto containing the information for the new section
     * @return the created HallPlanSection entity
     */
    HallPlanSection createSection(HallPlanSectionDto sectionDto);

    /**
     * Updates a hall plan section by its ID.
     *
     * @param id         the ID of the section to update
     * @param sectionDto the HallPlanSectionDto containing the updated information
     * @return the updated HallPlanSection entity
     */
    HallPlanSection updateSection(Long id, HallPlanSectionDto sectionDto);

    /**
     * Deletes a hall plan section by its ID.
     *
     * @param id the ID of the section to delete
     */
    void deleteSection(Long id);

    /**
     * Retrieves a hall plan section by its ID.
     *
     * @param id the ID of the section to retrieve
     * @return the HallPlanSection entity associated with the given ID
     */
    HallPlanSection getSection(Long id);

    /**
     * Retrieves all hall plan sections.
     *
     * @return a list of all HallPlanSection entities
     */
    List<HallPlanSection> getAllSections();

    /**
     * Retrieves all hall plan sections belonging to a specific hall plan.
     *
     * @param id the ID of the hall plan
     * @return a list of HallPlanSection entities associated with the given hall plan ID
     */
    List<HallPlanSection> findAllByHallPlanId(Long id);

    /**
     * Retrieves all sections by hallplanId.
     *
     * @param hallplanId id of the hallplan of the sections to retrieve
     * @return the sections of the hallplan
     */
    List<HallPlanSectionDto> findAllSectionsByHallPlanId(Long hallplanId);

    /**
     * Retrieves all sections by hallplanId and counts of how often a certain section occurs.
     *
     * @param hallplanId id of the hallplan of the sections to retrieve
     * @return the sections of the hallplan
     */
    List<HallPlanSectionDto> findAllSectionsByHallPlanIdWithCounts(Long hallplanId);

    /**
     * Retrieves a page containing 5 Hallplans.
     *
     * @param pageIndex index of the page
     * @param search    term to be searched for
     * @return a list of hallplans
     */
    Page<HallPlan> findPageOfHallplans(int pageIndex, String search);

}
