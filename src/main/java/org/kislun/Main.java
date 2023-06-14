package org.kislun;

import org.kislun.exception.InvalidLineException;
import org.kislun.models.Invoice;
import org.kislun.services.ShopService;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws InvalidLineException {
        ShopService shopService = new ShopService(3000);
        try {
            shopService.readDataFromCSV();
        } catch (IOException | InvalidLineException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < 16; i++) {
            shopService.generateRandomOrder();
        }

        shopService.getStatistics();
        System.out.println("Отсортированные заказы:");

            System.out.println();
    }
}