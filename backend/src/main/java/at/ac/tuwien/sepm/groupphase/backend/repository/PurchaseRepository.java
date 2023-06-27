package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findPurchasesByUserIdOrderByPurchaseNrDesc(Long id);

    @Override
    Purchase save(Purchase purchase);

    Purchase findPurchasesByPurchaseNr(Long purchaseNr);

    void deletePurchaseByPurchaseNr(Long purchaseNr);
}
