package com.messenger.repository;

import com.messenger.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByPhoneNumber(String phoneNumber);
    List<InventoryItem> findByPhoneNumberAndItemType(String phoneNumber, String itemType);
    long countByPhoneNumberAndItemName(String phoneNumber, String itemName);
}
