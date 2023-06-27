package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class HallPlanDto {

    private Long id;
    @NotBlank(message = "name must not be blank")
    @Size(max = 255, message = "name must be less than or equal to {max} characters")
    @NotNull(message = "name must be specified")
    private String name;
    @Size(max = 255, message = "description must be less than or equal to {max} characters")
    private String description;

    private boolean isTemplate;

    public boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(boolean template) {
        isTemplate = template;
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

    public static final class HallPlanDtoBuilder {

        private Long id;
        private String name;
        private String description;

        private HallPlanDtoBuilder() {

        }

        public static HallPlanDtoBuilder aHallPlanDto() {
            return new HallPlanDtoBuilder();
        }

        public HallPlanDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public HallPlanDtoBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public HallPlanDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public HallPlanDto build() {
            HallPlanDto hallPlanDto = new HallPlanDto();
            hallPlanDto.setName(name);
            hallPlanDto.setDescription(description);
            hallPlanDto.setId(id);
            return hallPlanDto;
        }

    }
}
