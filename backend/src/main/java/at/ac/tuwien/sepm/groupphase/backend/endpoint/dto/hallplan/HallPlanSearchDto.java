package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan;

public class HallPlanSearchDto {

    private String name;

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
}
