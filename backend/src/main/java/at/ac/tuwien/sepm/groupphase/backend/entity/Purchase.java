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

import java.time.LocalDate;
import java.util.List;

@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseNr;

    private Long userId;
    private LocalDate purchaseDate;
    private String billAddress;
    private Long billAreaCode;
    private String billCityName;
    @Column(columnDefinition = "boolean default false")
    private boolean canceled;
    private Long creditCardNr;
    private String expiration;
    private Long securityCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "purchaseNr")
    private List<Ticket> ticketList;

    public Long getPurchaseNr() {
        return purchaseNr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return purchaseDate;
    }

    public void setDate(LocalDate date) {
        this.purchaseDate = date;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
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

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
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

    public void setCreditCardNr(Long creditCardNr) {
        this.creditCardNr = creditCardNr;
    }

    public Long getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(Long securityCode) {
        this.securityCode = securityCode;
    }
}
