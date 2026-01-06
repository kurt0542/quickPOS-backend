package com.quickpos.quickposbackend.controller;

import com.quickpos.quickposbackend.model.Transaction;
import com.quickpos.quickposbackend.service.CartService;
import com.quickpos.quickposbackend.service.CartService.CartItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CartController {

    @Autowired
    private CartService cartService;
    private String getSessionId(HttpSession session) {
        return session.getId();
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(HttpSession session) {
        List<CartItem> cart = cartService.getCart(getSessionId(session));
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            HttpSession session,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        cartService.addToCart(getSessionId(session), productId, quantity);
        return ResponseEntity.ok("Item added to cart successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCartItem(
            HttpSession session,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        cartService.updateCartItemQuantity(getSessionId(session), productId, quantity);
        return ResponseEntity.ok("Cart item updated successfully");
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromCart(
            HttpSession session,
            @PathVariable Long productId) {

        cartService.removeFromCart(getSessionId(session), productId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        cartService.clearCart(getSessionId(session));
        return ResponseEntity.ok("Cart cleared");
    }

    @PostMapping("/checkout")
    public ResponseEntity<Transaction> checkout(
            HttpSession session,
            @RequestParam Long employeeId,
            @RequestParam(defaultValue = "CASH") String paymentMethod) {

        try {
            Transaction transaction = cartService.checkout(getSessionId(session), employeeId, paymentMethod);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}