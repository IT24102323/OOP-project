package com.oop.order.service;

import com.oop.order.model.Receipt;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReceiptService {

    private static final String FILE_PATH = "receipts.txt";

    public void saveReceipt(Receipt receipt) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(receipt.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Receipt> getAllReceipts() {
        try {
            return Files.lines(Paths.get(FILE_PATH))
                    .map(Receipt::fromString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public Receipt getReceiptById(String id) {
        return getAllReceipts().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean updateReceipt(Receipt updatedReceipt) {
        List<Receipt> receipts = getAllReceipts();
        boolean updated = false;

        for (int i = 0; i < receipts.size(); i++) {
            if (receipts.get(i).getId().equals(updatedReceipt.getId())) {
                receipts.set(i, updatedReceipt);
                updated = true;
                break;
            }
        }

        if (updated) {
            writeAll(receipts);
        }

        return updated;
    }

    public boolean deleteReceipt(String id) {
        List<Receipt> receipts = getAllReceipts();
        boolean removed = receipts.removeIf(r -> r.getId().equals(id));

        if (removed) {
            writeAll(receipts);
        }

        return removed;
    }

    private void writeAll(List<Receipt> receipts) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Receipt r : receipts) {
                writer.write(r.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public Receipt createNewReceipt(String customerName, String itemName, int quantity, double pricePerUnit) {
        return new Receipt(
                generateUniqueId(),
                customerName,
                itemName,
                quantity,
                pricePerUnit,
                LocalDateTime.now()
        );
    }
}