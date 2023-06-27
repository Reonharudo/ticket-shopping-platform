package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class PurchaseCreationDto {
    @NotNull
    private boolean useUserAddress;
    private String address;
    private Long areaCode;
    private String city;
    @NotEmpty
    private List<SeatDto> seats;
    private Long creditCardNr;
    @Pattern(regexp = "^[0-9][0-9]/[0-9][0-9]$", message = "invalid date format")
    private String expiration;
    private Long securityCode;

    public PurchaseCreationDto() {

    }

    public PurchaseCreationDto(boolean useUserAddress, String address, Long areaCode, String city, List<SeatDto> seatDtoList) {
        this.useUserAddress = useUserAddress;
        this.address = address;
        this.areaCode = areaCode;
        this.city = city;
        this.seats = seatDtoList;
    }

    public List<SeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDto> ticketList) {
        this.seats = ticketList;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Long areaCode) {
        this.areaCode = areaCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean getUseUserAddress() {
        return useUserAddress;
    }

    public void setUseUserAddress(boolean useUserAddress) {
        this.useUserAddress = useUserAddress;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public Long getCreditCardNr() {
        return creditCardNr;
    }

    public Long getSecurityCode() {
        return securityCode;
    }

    public void setCreditCardNr(Long creditCardNr) {
        this.creditCardNr = creditCardNr;
    }

    public void setSecurityCode(Long securityCode) {
        this.securityCode = securityCode;
    }
}
