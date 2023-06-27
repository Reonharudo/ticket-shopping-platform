package at.ac.tuwien.sepm.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "applicationuser")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Pattern(regexp = "[ÄÖÜA-Zäöüa-z]*")
    private String firstName;

    @Pattern(regexp = "[ÄÖÜA-Zäöüa-z]*")
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;


    @Pattern(regexp = "[ÄÖÜäöüßA-Za-z0-9\\s/-]*")
    private String address;


    @Positive
    private Long areaCode;

    @Pattern(regexp = "[ÄÖÜA-Zäöüa-z]*")
    private String cityName;

    private String password;

    private Boolean admin = false;

    private Boolean isLocked = false;

    private int failedLoginAttempts;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApplicationUser that = (ApplicationUser) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "news_read",
        joinColumns = @JoinColumn(name = "applicationuser_id"),
        inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    Set<News> readNews = new HashSet<>();

    public ApplicationUser() {
    }


    public ApplicationUser(String email, String password, Boolean admin) {
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    public ApplicationUser(String email, String firstName, String lastName, LocalDate birthdate, String address, Long areaCode, String cityName,
                           String password, Boolean admin, Boolean isLocked) throws ValidationException {
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setBirthdate(birthdate);
        setAddress(address);
        setAreaCode(areaCode);
        setCityName(cityName);
        setPassword(password);
        setAdmin(admin);
        setLocked(isLocked);
    }

    public ApplicationUser(String email, Boolean isLocked) {
        this.email = email;
        this.isLocked = isLocked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Long areaCode) {
        this.areaCode = areaCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public boolean hasRead(News news) {
        return this.readNews.contains(news);
    }

    public void addNews(News news) {
        this.readNews.add(news);
    }

    public void remove(News news) {
        this.readNews.remove(news);
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
}
