package org.kislun.services;

import org.kislun.models.Customer;

import java.util.Random;

public class PersonService {
    private Random random;

    public PersonService() {
        random = new Random();
    }
    public int generateCustomerId() {
        int id = random.nextInt(100);
        return id;
    }

    public String generateRandomEmail() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            char emailChar = (char) (random.nextInt(26) + 'a');
            sb.append(emailChar);
        }
        sb.append("@gmail.com");
        return sb.toString();
    }

    public int generateRandomAge() {
        int age = random.nextInt(9, 70);
        return age;
    }

    public Customer generateRandomCostumer() {
        return new Customer(generateCustomerId(), generateRandomEmail(), generateRandomAge());
    }
}
