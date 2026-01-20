package com.messenger.controller;

import com.messenger.model.*;
import com.messenger.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/store")
@CrossOrigin(origins = "*")
public class StoreController {

    @Autowired
    private StoreItemRepository storeItemRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Autowired
    private com.messenger.repository.UserRepository userRepository;

    // GET /store/items - Get all store items
    @GetMapping("/items")
    public List<StoreItem> getAllItems() {
        return storeItemRepository.findAll();
    }

    // GET /store/items/{type} - Get items by type (FRAME, EMOTICON, GIFT)
    @GetMapping("/items/{type}")
    public List<StoreItem> getItemsByType(@PathVariable String type) {
        return storeItemRepository.findByType(type);
    }

    // POST /store/buy - Buy an item
    @PostMapping("/buy")
    public ResponseEntity<?> buyItem(@RequestBody Map<String, Object> request) {
        String phoneNumber = (String) request.get("phoneNumber");
        Integer itemId = (Integer) request.get("itemId");
        String itemName = (String) request.get("itemName");

        // Get user
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (!userOpt.isPresent()) {
            // Create user if doesn't exist
            User newUser = new User();
            newUser.setPhoneNumber(phoneNumber);
            newUser.setCoinBalance(100); // Default balance
            userRepository.save(newUser);
            userOpt = Optional.of(newUser);
        }

        User user = userOpt.get();

        // Get store item
        Optional<StoreItem> itemOpt = storeItemRepository.findById(itemId.longValue());
        if (!itemOpt.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Item not found"
            ));
        }

        StoreItem item = itemOpt.get();

        // Check balance
        if (user.getCoinBalance() < item.getPrice()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Fonduri insuficiente"
            ));
        }

        // Deduct coins
        user.setCoinBalance(user.getCoinBalance() - item.getPrice());
        userRepository.save(user);

        // Add to inventory
        InventoryItem inventoryItem = new InventoryItem(
            phoneNumber,
            item.getName(),
            item.getType(),
            item.getResourceName()
        );
        inventoryItemRepository.save(inventoryItem);

        // Create transaction log
        TransactionLog log = new TransactionLog(
            phoneNumber,
            "Cumpărat: " + item.getName(),
            -item.getPrice(),
            "PURCHASE"
        );
        transactionLogRepository.save(log);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Articol cumpărat cu succes",
            "newBalance", user.getCoinBalance()
        ));
    }

    // GET /store/inventory/{phoneNumber} - Get user inventory
    @GetMapping("/inventory/{phoneNumber}")
    public List<InventoryItem> getInventory(@PathVariable String phoneNumber) {
        return inventoryItemRepository.findByPhoneNumber(phoneNumber);
    }

    // GET /store/transactions/{phoneNumber} - Get transaction history
    @GetMapping("/transactions/{phoneNumber}")
    public List<TransactionLog> getTransactions(@PathVariable String phoneNumber) {
        return transactionLogRepository.findByPhoneNumberOrderByTimestampDesc(phoneNumber);
    }

    // POST /store/coins/add - Add coins (for purchases)
    @PostMapping("/coins/add")
    public ResponseEntity<?> addCoins(@RequestBody Map<String, Object> request) {
        String phoneNumber = (String) request.get("phoneNumber");
        Integer amount = (Integer) request.get("amount");
        String description = (String) request.get("description");

        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        }

        User user = userOpt.get();
        user.setCoinBalance(user.getCoinBalance() + amount);
        userRepository.save(user);

        TransactionLog log = new TransactionLog(phoneNumber, description, amount, "COIN_ADD");
        transactionLogRepository.save(log);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "newBalance", user.getCoinBalance()
        ));
    }

    // POST /store/coins/remove - Remove coins (for sales)
    @PostMapping("/coins/remove")
    public ResponseEntity<?> removeCoins(@RequestBody Map<String, Object> request) {
        String phoneNumber = (String) request.get("phoneNumber");
        Integer amount = (Integer) request.get("amount");
        String description = (String) request.get("description");

        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        }

        User user = userOpt.get();
        
        if (user.getCoinBalance() < amount) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Insufficient balance"
            ));
        }

        user.setCoinBalance(user.getCoinBalance() - amount);
        userRepository.save(user);

        TransactionLog log = new TransactionLog(phoneNumber, description, -amount, "COIN_REMOVE");
        transactionLogRepository.save(log);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "newBalance", user.getCoinBalance()
        ));
    }

    // GET /store/gifts - Get available gifts
    @GetMapping("/gifts")
    public List<Map<String, Object>> getGifts() {
        List<StoreItem> gifts = storeItemRepository.findByType("GIFT");
        List<Map<String, Object>> giftList = new ArrayList<>();
        
        for (StoreItem gift : gifts) {
            Map<String, Object> giftData = new HashMap<>();
            giftData.put("name", gift.getName());
            giftData.put("iconResourceName", gift.getResourceName());
            giftData.put("price", gift.getPrice());
            giftList.add(giftData);
        }
        
        return giftList;
    }

    // POST /store/gift/send - Send a gift
    @PostMapping("/gift/send")
    public ResponseEntity<?> sendGift(@RequestBody Map<String, Object> request) {
        String senderPhone = (String) request.get("senderId");
        String receiverPhone = (String) request.get("receiverId");
        String giftName = (String) request.get("giftName");
        Integer giftValue = (Integer) request.get("giftValue");

        // Deduct from sender
        Optional<User> senderOpt = userRepository.findByPhoneNumber(senderPhone);
        if (!senderOpt.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false));
        }

        User sender = senderOpt.get();
        if (sender.getCoinBalance() < giftValue) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Insufficient balance"
            ));
        }

        sender.setCoinBalance(sender.getCoinBalance() - giftValue);
        userRepository.save(sender);

        // Add transaction for sender
        TransactionLog senderLog = new TransactionLog(
            senderPhone,
            "Gift trimis: " + giftName,
            -giftValue,
            "GIFT_SENT"
        );
        transactionLogRepository.save(senderLog);

        // Add to receiver (optional - if you want to track received gifts)
        TransactionLog receiverLog = new TransactionLog(
            receiverPhone,
            "Gift primit: " + giftName,
            giftValue,
            "GIFT_RECEIVED"
        );
        transactionLogRepository.save(receiverLog);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Gift trimis cu succes"
        ));
    }
}
