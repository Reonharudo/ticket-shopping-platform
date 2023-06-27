package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.hallplan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class HallPlanSectionDto {
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 255, message = "Name must be at most {max} characters long")
    private String name;

    @NotBlank(message = "Color is mandatory")
    @Size(max = 50, message = "Color must be at most {max} characters long")
    private String color;

    @NotNull(message = "Price is mandatory")
    @PositiveOrZero(message = "Price must be a positive number or zero")
    private Double price;

    @NotNull(message = "HallPlanId is mandatory")
    private Long hallPlanId;

    private Long count;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getHallPlanId() {
        return hallPlanId;
    }

    public void setHallPlanId(Long hallPlanId) {
        this.hallPlanId = hallPlanId;
    }

    public static final class HallPlanSectionDtoBuilder {

        private Long id;
        private String name;
        private String color;
        private Double price;

        private HallPlanSectionDtoBuilder() {

        }

        public static HallPlanSectionDtoBuilder aHallPlanSectionDto() {
            return new HallPlanSectionDtoBuilder();
        }

        public HallPlanSectionDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public HallPlanSectionDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public HallPlanSectionDtoBuilder withColor(String color) {
            this.color = color;
            return this;
        }

        public HallPlanSectionDtoBuilder withPrice(Double price) {
            this.price = price;
            return this;
        }

        public HallPlanSectionDto build() {
            HallPlanSectionDto hallPlanSectionDto = new HallPlanSectionDto();
            hallPlanSectionDto.setId(id);
            hallPlanSectionDto.setName(name);
            hallPlanSectionDto.setColor(color);
            hallPlanSectionDto.setPrice(price);
            return hallPlanSectionDto;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HallPlanSectionDto other = (HallPlanSectionDto) o;
        return Objects.equals(name, other.name)
            && Objects.equals(color, other.color)
            && Objects.equals(price, other.price)
            && Objects.equals(hallPlanId, other.hallPlanId)
            && Objects.equals(count, other.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, price, hallPlanId, count);
    }


}
