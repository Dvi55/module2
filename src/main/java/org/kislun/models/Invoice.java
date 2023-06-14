package org.kislun.models;

import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class Invoice {
    private List<Object> items; // Может содержать экземпляры Telephone или Television
    private Customer customer;
    private String type;

    public Invoice(List<Object> items, Customer customer, String type) {
        this.items = items;
        this.customer = customer;
        this.type = type;
    }

    public Invoice() {
    }

    public String getItemsAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object item : items) {
            stringBuilder.append(item.toString()).append(", ");
        }
        // Удаление последней запятой и пробела
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        return stringBuilder.toString();
    }
    public static double calculateTotalPrice(Invoice invoice) {
        return invoice.getItems().stream()
                .mapToDouble(item -> {
                    if (item instanceof Telephone) {
                        return ((Telephone) item).getPrice();
                    } else if (item instanceof Television) {
                        return ((Television) item).getPrice();
                    } else {
                        return 0.0;
                    }
                })
                .sum();
    }

}
