package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "hallplan")
public class HallPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_template")
    private boolean isTemplate;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "hallplan_id")
    private List<SeatRow> seatRows;

    public boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(boolean template) {
        isTemplate = template;
    }

    public List<SeatRow> getSeatRows() {
        return seatRows;
    }

    public void setSeatRows(List<SeatRow> seatRows) {
        this.seatRows = seatRows;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static final class HallPlanBuilder {
        private String name;
        private String description;

        public static HallPlanBuilder aHallPlan() {
            return new HallPlanBuilder();
        }

        public HallPlan build() {
            HallPlan hallPlan = new HallPlan();
            hallPlan.setName(name);
            hallPlan.setDescription(description);
            return hallPlan;
        }


    }
}
