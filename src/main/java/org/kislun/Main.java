package org.kislun;

import org.kislun.exception.InvalidLineException;
import org.kislun.models.Invoice;
import org.kislun.services.ShopService;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InvalidLineException, IOException {
        ShopService shopService = new ShopService(3000); // Лимит покупки 1000
        try {
            shopService.readProductsFromCSV(); // Путь к файлу с товарами
        } catch (IOException | InvalidLineException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < 15; i++) {
            shopService.generateRandomOrder();
        }

        shopService.getStatistics();

        shopService.getStatistics();
        System.out.println("Отсортированные заказы:");

            System.out.println();
    }
}