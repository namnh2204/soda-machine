package common;

import payment.Coin;
import product.Product;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CSVUtil {
    public static final String PRODUCT_CSV_PATH = "resources/product.csv";
    public static final String RESERVES_CSV_PATH = "resources/reserves.csv";
    public static List<Product> readFromCSV() throws IOException {
        List<Product> productList = new java.util.ArrayList<>(Collections.emptyList());
        BufferedReader csvReader = new BufferedReader(new FileReader(PRODUCT_CSV_PATH));
        String line;
        while ((line = csvReader.readLine()) != null) {
            String[] data = line.split(";");
            if ("id".equals(data[0])) {
                continue;
            }
            Product product = new Product(Integer.valueOf(data[0]), data[1],
                    Long.valueOf(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]));
            productList.add(product);
        }
        csvReader.close();

        return productList;
    }

    public static void saveToCSV(List<Product> updatedProductList) throws IOException {
        // build entire product list with updatedProductList
        List<Product> productList = readFromCSV();
        for (Product product : productList) {
            int updatedProductIndex = updatedProductList.indexOf(product);
            if (updatedProductIndex >= 0) {
                product.setSales(updatedProductList.get(updatedProductIndex).getSales());
            }
        }

        // write new product list to csv file
        FileWriter csvWriter = new FileWriter(PRODUCT_CSV_PATH);
        csvWriter.append("id");
        csvWriter.append(";");
        csvWriter.append("name");
        csvWriter.append(";");
        csvWriter.append("price");
        csvWriter.append(";");
        csvWriter.append("quantity");
        csvWriter.append(";");
        csvWriter.append("sales");
        csvWriter.append("\n");

        for (Product product : productList) {
            csvWriter.append(String.valueOf(product.getId()));
            csvWriter.append(";");
            csvWriter.append(product.getName());
            csvWriter.append(";");
            csvWriter.append(String.valueOf(product.getPrice()));
            csvWriter.append(";");
            csvWriter.append(String.valueOf(product.getQuantity()));
            csvWriter.append(";");
            csvWriter.append(String.valueOf(product.getSales()));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    public static List<Coin> getSupportedNominalValues() throws IOException {
        List<Coin> supportedNominalValues = new java.util.ArrayList<>(Collections.emptyList());
        BufferedReader csvReader = new BufferedReader(new FileReader(RESERVES_CSV_PATH));
        String line;
        while ((line = csvReader.readLine()) != null) {
            String[] data = line.split(";");
            if ("id".equals(data[0])) {
                continue;
            }
            Coin coin = new Coin(Integer.valueOf(data[0]), Long.valueOf(data[1]));
            supportedNominalValues.add(coin);
        }
        csvReader.close();

        return supportedNominalValues;
    }

    public static Map<Long, Integer> readReservesFromCSV() throws IOException {
        Map<Long, Integer> reservesMap = new java.util.HashMap<>(Collections.emptyMap());
        BufferedReader csvReader = new BufferedReader(new FileReader(RESERVES_CSV_PATH));
        String line;
        while ((line = csvReader.readLine()) != null) {
            String[] data = line.split(";");
            if ("id".equals(data[0])) {
                continue;
            }
            reservesMap.put(Long.valueOf(data[1]), Integer.valueOf(data[2]));
        }
        csvReader.close();

        return reservesMap;
    }

    public static void updateReservesToCSV(Map<Long, Integer> reserves) throws IOException {
        List<Coin> coins = getSupportedNominalValues();
        // write new product list to csv file
        FileWriter csvWriter = new FileWriter(RESERVES_CSV_PATH);
        csvWriter.append("id");
        csvWriter.append(";");
        csvWriter.append("nominalValue");
        csvWriter.append(";");
        csvWriter.append("count");
        csvWriter.append("\n");

        for (Coin coin : coins) {
            csvWriter.append(String.valueOf(coin.getId()));
            csvWriter.append(";");
            csvWriter.append(String.valueOf(coin.getNominalValue()));
            csvWriter.append(";");
            csvWriter.append(String.valueOf(reserves.get(coin.getNominalValue())));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }
}
