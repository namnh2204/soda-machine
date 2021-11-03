package payment;

import customer.CustomerService;
import customer.OrderItem;
import product.Product;
import product.ProductException;
import product.ProductService;

import java.util.*;
import java.util.stream.Collectors;

public class PaymentService {
    private final ProductService productService;
    private final CustomerService customerService;
    private final PaymentRepository paymentRepository;

    public PaymentService(ProductService productService, CustomerService customerService,
                          PaymentRepository paymentRepository) {
        this.productService = productService;
        this.customerService = customerService;
        this.paymentRepository = paymentRepository;
    }

    public void processPaymentRequest() {
        Long customerBalance = this.customerService.getCustomerBalance();
        Long customerOrderPrice = this.customerService.getOrderItemsPrice();
        if (customerOrderPrice == 0) { // customer order nothing
            System.out.println("You order nothing. Return: " + customerBalance + " VND");
            for (Map.Entry<Long, Integer> refundCoin : this.customerService.getDepositedCoins().entrySet()) {
                System.out.println(refundCoin.getKey() + " VND: " + refundCoin.getValue() + " coin(s)");
            }
            this.customerService.emptyOrderAndDepositedCoins();
            return;
        }

        verifyCustomerBalance();
        verifyAvailableItem();
        Long change = customerBalance - customerOrderPrice;
        Map<Long, Integer> customerDepositedCoin = this.customerService.getDepositedCoins();

        Map<Long, Integer> reserves = paymentRepository.getReserves();
        // add customer deposited coin to reserves
        for (Map.Entry<Long, Integer> depositedCoin : customerDepositedCoin.entrySet()) {
            reserves.put(depositedCoin.getKey(), reserves.get(depositedCoin.getKey()) + depositedCoin.getValue());
        }
        Map<Long, Integer> refundCoins = calculateChange(change, reserves);
        System.out.println("Payment successfully. Please get all your items and change: " + change + " VND");
        for (Map.Entry<Long, Integer> refundCoin : refundCoins.entrySet()) {
            System.out.println(refundCoin.getKey() + " VND: " + refundCoin.getValue() + " coin(s)");
        }
        this.paymentRepository.updateReserves(reserves);
        this.productService.updateProductQuantity(this.customerService.getOrderItems());
        this.customerService.emptyOrderAndDepositedCoins();
    }

    private void verifyAvailableItem() {
        List<OrderItem> orderItems = this.customerService.getOrderItems();
        for (OrderItem orderItem : orderItems ) {
            Product product = this.productService.getProductById(orderItem.getId())
                    .orElseThrow(() -> new ProductException("Product not found"));
            if (product.getAvailableQuantity() -  orderItem.getQuantity() < 0) {
                throw new PaymentException(String.format("Product is out of stock. Remaining %d items", product.getAvailableQuantity()));
            }
        }
    }

    public List<Coin> getSupportedNominalValues() {
        return this.paymentRepository.getSupportedNominalValues();
    }

    private Map<Long, Integer> calculateChange(Long paybackValue, Map<Long, Integer> reserves) throws PaymentException {
        Map<Long, Integer> refundList = new HashMap<>();
        Long remainingPaybackValue = paybackValue;
        // get all nominalValues in reserves list with count > 0
        List<Long> reservesNominalValues = reserves.keySet().stream()
                .filter(key -> reserves.get(key) > 0).sorted(Collections.reverseOrder()).collect(Collectors.toList());

        // sort reservesNominalValues descending
        for (Long reservesNominalValue : reservesNominalValues) {
            // if paybackValue > reservesNominalValue, payback number of reservesNominalValue first
            if (remainingPaybackValue >= reservesNominalValue) {
                // number of coin need to be payback to customer
                int requiredNumberOfChangeCoin =  (int) (remainingPaybackValue / reservesNominalValue);
                Integer totalReservesCoin = reserves.get(reservesNominalValue);
                // get number of available coin in reserves enough to payback customer.
                int actualNumberOfChangeCoin = Math.min(requiredNumberOfChangeCoin, totalReservesCoin);

                // calculate remaining payback value machine have to pay customer
                remainingPaybackValue = remainingPaybackValue - reservesNominalValue * actualNumberOfChangeCoin;

                // update reserves list after get some coin to payback customer
                reserves.put(reservesNominalValue, totalReservesCoin - actualNumberOfChangeCoin);

                // update refund coins that will be payback to customer
                refundList.put(reservesNominalValue, actualNumberOfChangeCoin);
            }
        }
        if (remainingPaybackValue > 0) {
            throw new PaymentException("This machine do not enough money to change. Please buy more items or cancel order.");
        }

        return refundList;
    }

    private void verifyCustomerBalance() {
        Long currentOrderItemTotalPrice = this.customerService.getOrderItemsPrice();
        if (this.customerService.getCustomerBalance() < currentOrderItemTotalPrice) {
            throw new PaymentException("Insufficient balance. Your balance: " + this.customerService.getCustomerBalance()
                    + " VND. Order total: " + currentOrderItemTotalPrice + " VND.");
        }
    }
}
