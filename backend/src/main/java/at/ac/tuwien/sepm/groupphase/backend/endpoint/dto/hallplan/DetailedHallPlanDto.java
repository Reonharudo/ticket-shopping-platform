package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatRowDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class DetailedHallPlanDto {
    private Long id;
    @NotBlank(message = "name must not be blank")
    @Size(max = 255, message = "name must be less than or equal to {max} characters")
    @NotNull(message = "name must be specified")
    private String name;
    @Size(max = 255, message = "description must be less than or equal to {max} characters")
    private String description;

    private List<SeatRowDto> seatRows;

    private boolean isTemplate;

    public boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public List<SeatRowDto> getSeatRows() {
        return seatRows;
    }

    public void setSeatRows(List<SeatRowDto> seatRows) {
        this.seatRows = seatRows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
