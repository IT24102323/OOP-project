package com.oop.order.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Receipt {
    private String id;
    private String customerName;
    private String itemName;
    private int quantity;
    private double pricePerUnit;
    private LocalDateTime dateTime;

    public Receipt() {}

    public Receipt(String id, String customerName, String itemName, int quantity, double pricePerUnit, LocalDateTime dateTime) {
        this.id = id;
        this.customerName = customerName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.dateTime = dateTime;
    }

    public double getTotalAmount() {
        return quantity * pricePerUnit;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String formatDateTime() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return id + "|" + customerName + "|" + itemName + "|" + quantity + "|" + pricePerUnit + "|" + formatDateTime();
    }

    public static Receipt fromString(String line) {
        String[] parts = line.split("\\|");
        return new Receipt(
                parts[0],
                parts[1],
                parts[2],
                Integer.parseInt(parts[3]),
                Double.parseDouble(parts[4]),
                LocalDateTime.parse(parts[5], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}