import customer.CustomerService;
import customer.OrderItem;
import payment.CSVPaymentRepositoryImpl;
import payment.Coin;
import payment.PaymentRepository;
import payment.PaymentService;
import product.CSVProductRepositoryImpl;
import product.Product;
import product.ProductRepository;
import product.ProductService;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MachineApp {
    protected static Scanner customerInput;
    protected static ProductService productService;
    protected static CustomerService customerService;
    protected static PaymentService paymentService;

    public static void main(String[] args) {
        initialize();
        showWelcomeScreen();
    }

    private static void initialize() {
        ProductRepository productRepository = new CSVProductRepositoryImpl();
        productService = new ProductService(productRepository);
        customerService = new CustomerService(productService);

        PaymentRepository paymentRepository = new CSVPaymentRepositoryImpl();
        paymentService = new PaymentService(productService, customerService, paymentRepository);
        customerInput = new Scanner(System.in);
    }

    private static void showWelcomeScreen() {
        System.out.println("\n");
        System.out.println("-----Drink Machine-----");
        System.out.println("1. Deposit");
        System.out.println("2. Order");
        System.out.println("3. Payment");
        System.out.println("4. Cancel Order");
        System.out.print("Select: ");
        String customerOption = customerInput.nextLine();
        try {
            int option = Integer.parseInt(customerOption);
            switch (option) {
                case 1:
                    showDepositScreen();
                    break;
                case 2:
                    showOrderScreen();
                    break;
                case 3:
                    showPaymentScreen();
                    break;
                case 4:
                    showCancelOrderScreen();
                    break;
                default:
                    System.out.println("Please input a valid number.");
                    showWelcomeScreen();
            }
        } catch (NumberFormatException ex) {
            System.out.println("Only number value is accepted.");
            showDepositScreen();
        }
    }

    public static void showDepositScreen() {
        System.out.println("\n");
        System.out.println("-----Deposit-----");
        List<Coin> supportedCoins = paymentService.getSupportedNominalValues();
        List<Integer> supportedCoinIds = supportedCoins.stream().map(Coin::getId).collect(Collectors.toList());
        System.out.println("Please input you coin by submit id.");
        System.out.println("0. Back");
        supportedCoins.forEach(System.out::println);
        System.out.print("Select: ");
        String selectedNominalValue = customerInput.nextLine();
        try {
            int coinId = Integer.parseInt(selectedNominalValue);
            if (coinId == 0) { // Customer choose back
                showWelcomeScreen();
            } else {
                int supportedCoinIndex = supportedCoinIds.indexOf(coinId);
                if (supportedCoinIndex >= 0) {
                    Coin depositedCoin = supportedCoins.get(supportedCoinIndex);
                    customerService.deposit(depositedCoin.getNominalValue(), 1);
                    System.out.println("Your current balance: " + customerService.getCustomerBalance() + " VND");
                } else {
                    System.out.println("Invalid input number");
                }
                showDepositScreen();
            }
        } catch (NumberFormatException ex) {
            System.out.println("Only number value is accepted.");
            showDepositScreen();
        }

    }

    private static void showOrderScreen() {
        System.out.println("\n");
        System.out.println("-----Products-----");
        System.out.println("Please order your item:");
        List <Product> productList = productService.listProducts();
        List <Integer> productIds = productList.stream().map(Product::getId).collect(Collectors.toList());
        System.out.println("0. Back");
        productList.forEach(System.out::println);
        Long customerBalance = customerService.getCustomerBalance();
        // check customer balance before allow to place an order
        if (customerBalance == 0) {
            System.out.println("Your balance is less than 0. Please deposit first.");
            showWelcomeScreen();
        } else {
            System.out.print("Select: ");
            String selectedProductId = customerInput.nextLine();
            try {
                int productId = Integer.parseInt(selectedProductId);
                if (productId == 0) {
                    showWelcomeScreen();
                } else {
                    int productListIndex = productIds.indexOf(productId);
                    if (productListIndex >= 0) {
                        Product orderProduct = productList.get(productListIndex);
                        OrderItem orderItem = new OrderItem(orderProduct.getId(), orderProduct.getName(),
                                orderProduct.getPrice(), 1);
                        customerService.addOrderItem(orderItem);
                        System.out.println("Your order Items: ");
                        customerService.getOrderItems().forEach(System.out::println);
                    } else {
                        System.out.println("Invalid input number.");
                    }
                    showOrderScreen();
                }
            } catch (NumberFormatException ex) {
                System.out.println("Only number value is accepted.");
                showOrderScreen();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                showOrderScreen();
            }
        }
    }

    private static void showPaymentScreen() {
        System.out.println("\n");
        System.out.println("-----Payment Confirmation-----");
        System.out.println("Your order Items: ");
        customerService.getOrderItems().forEach(System.out::println);
        System.out.println("Total Price: " + customerService.getOrderItemsPrice());
        System.out.println("Your balance: " + customerService.getCustomerBalance());
        System.out.println("1. Confirm");
        System.out.println("2. Back");
        System.out.print("Select: ");
        String orderOption = customerInput.nextLine();
        try {
            int orderOptionId = Integer.parseInt(orderOption);
            switch (orderOptionId) {
                case 1:
                    paymentService.processPaymentRequest();
                    showWelcomeScreen();
                    break;
                case 2:
                    showWelcomeScreen();
                    break;
                default:
                    System.out.println("Invalid input number.");
                    showPaymentScreen();
            }
        } catch (NumberFormatException ex) {
            System.out.println("Only number value is accepted.");
            showPaymentScreen();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            showWelcomeScreen();
        }
    }

    private static void showCancelOrderScreen() {
        System.out.println("\n");
        System.out.println("-----Order Cancellation-----");
        System.out.println("Your order Items:");
        customerService.getOrderItems().forEach(System.out::println);
        System.out.println("Total Price: " + customerService.getOrderItemsPrice());
        System.out.println("Your balance: " + customerService.getCustomerBalance());
        System.out.println("1. Confirm Cancellation");
        System.out.println("2. Back");
        System.out.print("Select: ");
        String orderOption = customerInput.nextLine();
        try {
            int orderOptionId = Integer.parseInt(orderOption);
            switch (orderOptionId) {
                case 1:
                    // remove all item in order
                    customerService.cancelOrder();
                    paymentService.processPaymentRequest();
                    showWelcomeScreen();
                    break;
                case 2:
                    showWelcomeScreen();
                    break;
                default:
                    System.out.println("Invalid input number.");
                    showCancelOrderScreen();
            }
        } catch (NumberFormatException ex) {
            System.out.println("Only number value is accepted.");
            showCancelOrderScreen();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            showWelcomeScreen();
        }
    }
}
