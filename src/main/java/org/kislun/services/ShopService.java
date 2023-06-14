package org.kislun.services;

import org.kislun.Main;
import org.kislun.exception.InvalidLineException;
import org.kislun.models.Customer;
import org.kislun.models.Invoice;
import org.kislun.models.Telephone;
import org.kislun.models.Television;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.kislun.models.Invoice.calculateTotalPrice;

public class ShopService {
    private final int priceLimit;
    private List<Object> products;
    private List<Invoice> invoices;
    private PersonService personService;
    private Random random;

    public ShopService(int priceLimit) {
        this.priceLimit = priceLimit;
        products = new ArrayList<>();
        invoices = new ArrayList<>();
        personService = new PersonService();
        random = new Random();
    }

    public void readDataFromCSV() throws IOException, InvalidLineException {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("products.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length != 7) {
                    throw new InvalidLineException("Invalid line: " + line);
                }

                String type = data[0];
                String series = data[1];
                String model = data[2];
                int diagonal = data[3].equals("none") ? 0 : Integer.parseInt(data[3]);
                String screenType = data[4].equals("none") ? null : data[4];
                String country = data[5].equals("none") ? null : data[5];
                int price = Integer.parseInt(data[6]);

                if (type.equals("Telephone")) {
                    Telephone telephone = new Telephone(series, model, screenType, price);
                    invoices.add(new Invoice(Collections.singletonList(telephone), null, type));
                } else if (type.equals("Television")) {
                    Television television = new Television(series, diagonal, screenType, country, price);
                    invoices.add(new Invoice(Collections.singletonList(television), null, type));
                } else {
                    throw new InvalidLineException("Invalid line: " + line);
                }
            }
        }
    }

    public void generateRandomOrder() {
        Customer customer = personService.generateRandomCostumer();
        List<Object> items = new ArrayList<>();
        int total = 0;
        int itemCount = new Random().nextInt(5) + 1; // От 1 до 5 позиций
        for (int i = 0; i < itemCount; i++) {
            Invoice invoice = invoices.get(new Random().nextInt(invoices.size()));
            items.addAll(invoice.getItems());
            total += calculateTotalPrice(invoice);
        }

        String type = total > priceLimit ? "wholesale" : "retail";
        Invoice order = new Invoice(items, customer, type);
        saveOrder(order);
    }

    private void saveOrder(Invoice invoice) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String userData = invoice.getCustomer().getId() + ", " + invoice.getCustomer().getEmail();
        String invoiceData = invoice.getType() + ", " + invoice.getItemsAsString() + ", " + calculateTotalPrice(invoice);
        String logEntry = "[" + timestamp + "] [" + userData + "] [" + invoiceData + "]";

        try (PrintWriter writer = new PrintWriter(new FileWriter("order.log", true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getStatistics() {
        Map<String, Long> productCounts = invoices.stream()
                .flatMap(invoice -> invoice.getItems().stream())
                .collect(Collectors.groupingBy(item -> item.getClass().getSimpleName(), Collectors.counting()));
        System.out.println("Количество проданных товаров по категориям:");
        productCounts.forEach((category, count) -> System.out.println(category + ": " + count));

        Invoice smallestInvoice = invoices.stream()
                .min(Comparator.comparingDouble(ShopService::calculateTotalPrice))
                .orElse(null);
        if (smallestInvoice != null) {
            double smallestInvoiceTotal = calculateTotalPrice(smallestInvoice);
            System.out.println("Сумма самого маленького чека: " + smallestInvoiceTotal);
            System.out.println("Информация о покупателе:");
            System.out.println("ID: " + smallestInvoice.getCustomer().getId());
            System.out.println("Email: " + smallestInvoice.getCustomer().getEmail());
            System.out.println("Возраст: " + smallestInvoice.getCustomer().getAge());
        }

        double totalSales = invoices.stream()
                .mapToDouble(ShopService::calculateTotalPrice)
                .sum();
        System.out.println("Сумма всех покупок: " + totalSales);

        long retailInvoiceCount = invoices.stream()
                .filter(invoice -> invoice.getType().equals("retail"))
                .count();
        System.out.println("Количество чеков с категорией retail: " + retailInvoiceCount);

        List<Invoice> singleProductInvoices = invoices.stream()
                .filter(invoice -> invoice.getItems().size() == 1)
                .collect(Collectors.toList());
        System.out.println("Чеки, которые содержат только один тип товара:");
        singleProductInvoices.forEach(System.out::println);

        List<Invoice> firstThreeInvoices = invoices.stream()
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("Первые три чека, сделанные покупателями:");
        firstThreeInvoices.forEach(System.out::println);


        List<Invoice> underageCustomerInvoices = invoices.stream()
                .filter(invoice -> invoice.getCustomer().getAge() < 18)
                .collect(Collectors.toList());
        System.out.println("Информация по чекам, купленным пользователями младше 18 лет:");
        underageCustomerInvoices.forEach(System.out::println);
    }


    public List<Invoice> getOrders() {
        return invoices;
    }

    private static double calculateTotalPrice(Invoice invoice) {
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

    public void sortOrders() {
        invoices.sort(Comparator.comparing((Invoice invoice) -> invoice.getCustomer().getAge())
                .reversed()
                .thenComparing(invoice -> invoice.getItems().size())
                .thenComparing(Invoice::calculateTotalPrice));
    }
}


