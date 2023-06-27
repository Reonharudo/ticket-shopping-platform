package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Purchase;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public class PurchaseDto {
    private Long purchaseNr;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
    private String billAddress;
    private Long billAreaCode;
    private String billCityName;
    private List<TicketDto> ticketList;
    private Boolean canceled;
    private Long creditCardNr;
    private String expiration;
    private Long securityCode;

    public PurchaseDto() {
    }

    public PurchaseDto(List<TicketDto> ticketList, Purchase purchase) {
        this.purchaseNr = purchase.getPurchaseNr();
        this.purchaseDate = purchase.getDate();
        this.billAddress = purchase.getBillAddress();
        this.billAreaCode = purchase.getBillAreaCode();
        this.billCityName = purchase.getBillCityName();
        this.ticketList = ticketList;
        this.canceled = purchase.isCanceled();
        this.creditCardNr = purchase.getCreditCardNr();
        this.expiration = purchase.getExpiration();
        this.securityCode = purchase.getSecurityCode();
    }


    public Long getPurchaseNr() {
        return purchaseNr;
    }

    public void setPurchaseNr(Long purchaseNr) {
        this.purchaseNr = purchaseNr;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getBillAddress() {
        return billAddress;
    }

    public void setBillAddress(String billAddress) {
        this.billAddress = billAddress;
    }

    public Long getBillAreaCode() {
        return billAreaCode;
    }

    public void setBillAreaCode(Long billAreaCode) {
        this.billAreaCode = billAreaCode;
    }

    public String getBillCityName() {
        return billCityName;
    }

    public void setBillCityName(String billCityName) {
        this.billCityName = billCityName;
    }

    public List<TicketDto> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<TicketDto> ticketList) {
        this.ticketList = ticketList;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Long getCreditCardNr() {
        return creditCardNr;
    }

    public void setCreditCardNr(Long creditCardNr) {
        this.creditCardNr = creditCardNr;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public Long getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(Long securityCode) {
        this.securityCode = securityCode;
    }
}
