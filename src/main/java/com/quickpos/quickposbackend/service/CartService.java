package com.quickpos.quickposbackend.service;

import com.quickpos.quickposbackend.model.Order;
import com.quickpos.quickposbackend.model.Product;
import com.quickpos.quickposbackend.model.Transaction;
import com.quickpos.quickposbackend.model.User;
import com.quickpos.quickposbackend.model.enums.PaymentMethod;
import com.quickpos.quickposbackend.model.enums.TransactionStatus;
import com.quickpos.quickposbackend.repository.OrderRepo;
import com.quickpos.quickposbackend.repository.ProductRepo;
import com.quickpos.quickposbackend.repository.TransactionRepo;
import com.quickpos.quickposbackend.repository.UserRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@Setter
public class CartService {

    private final ProductRepo productRepository;

    private final TransactionRepo transactionRepository;

    private final OrderRepo orderRepository;

    private final UserRepo userRepository;

    private Map<String, List<CartItem>> carts = new HashMap<>();

    public CartService(ProductRepo productRepository, TransactionRepo transactionRepository, OrderRepo orderRepository, UserRepo userRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public void addToCart(String sessionId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<CartItem> cart = carts.getOrDefault(sessionId, new ArrayList<>());

        CartItem existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            cart.add(new CartItem(productId, product.getName(), product.getPrice(), quantity));
        }

        carts.put(sessionId, cart);
    }

    public void removeFromCart(String sessionId, Long productId) {
        List<CartItem> cart = carts.get(sessionId);
        if (cart != null) {
            cart.removeIf(item -> item.getProductId().equals(productId));
        }
    }

    public void updateCartItemQuantity(String sessionId, Long productId, int quantity) {
        List<CartItem> cart = carts.get(sessionId);
        if (cart != null) {
            cart.stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(quantity));
        }
    }

    public List<CartItem> getCart(String sessionId) {
        return carts.getOrDefault(sessionId, new ArrayList<>());
    }

    public void clearCart(String sessionId) {
        carts.remove(sessionId);
    }

    @Transactional
    // 1. Accept employeeId to link the user, and String paymentMethod from Controller
    public Transaction checkout(String sessionId, Long employeeId, String paymentMethodStr) {

        List<CartItem> cart = carts.get(sessionId);
        if (cart == null || cart.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // --- Fetch the Employee (Cashier) ---
        // Assuming you have a UserRepo. If not, autowire it at the top.
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // --- Create Transaction ---
        Transaction transaction = new Transaction();

        // FIX 1: Use 'setTimestamp' to match your Entity
        transaction.setTimestamp(LocalDateTime.now());

        // FIX 2: Convert String to Enum for PaymentMethod
        try {
            transaction.setPaymentMethod(PaymentMethod.valueOf(paymentMethodStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Payment Method: " + paymentMethodStr);
        }

        // FIX 3: Set Status and Employee
        transaction.setStatus(TransactionStatus.COMPLETED); // Or PENDING
        transaction.setEmployee(employee);

        double totalAmount = 0;
        List<Order> orders = new ArrayList<>();

        for (CartItem item : cart) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            Order order = new Order();
            order.setProduct(product);
            order.setQuantity(item.getQuantity());
            order.setPrice(product.getPrice());
            order.setTransaction(transaction);

            orders.add(order);
            totalAmount += product.getPrice() * item.getQuantity();
        }

        transaction.setTotalAmount(totalAmount);
        transaction.setOrders(orders);

        Transaction savedTransaction = transactionRepository.save(transaction);
        clearCart(sessionId);

        return savedTransaction;
    }
    @Setter
    @Getter
    public static class CartItem {
        private Long productId;
        private String productName;
        private double price;
        private int quantity;

        public CartItem(Long productId, String productName, double price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

    }
}
