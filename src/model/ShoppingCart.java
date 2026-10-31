package model;

import exception.InsufficientStockException;
import java.util.ArrayList;
import java.util.List;

/**
 * ShoppingCart — manages a list of CartItems for a Customer.
 *
 * Demonstrates: Encapsulation (private items list, exposed via controlled methods).
 */
public class ShoppingCart {

    private List<CartItem> items;

    // ------------------------------------------------------------------ constructor
    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    // ------------------------------------------------------------------ operations
    /**
     * Adds product to cart. If product already exists, increases quantity.
     * @throws InsufficientStockException if requested qty exceeds stock
     */
    public void addItem(Product product, int quantity) throws InsufficientStockException {
        if (quantity <= 0) return;

        // Check stock
        int currentInCart = getQuantityInCart(product.getProductId());
        int totalRequested = currentInCart + quantity;
        if (totalRequested > product.getStockQuantity()) {
            throw new InsufficientStockException(product.getName(), totalRequested, product.getStockQuantity());
        }

        // Update existing or add new
        for (CartItem item : items) {
            if (item.getProduct().getProductId().equals(product.getProductId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    /**
     * Removes a product from cart by productId.
     * @return true if removed, false if not found
     */
    public boolean removeItem(String productId) {
        return items.removeIf(item -> item.getProduct().getProductId().equals(productId));
    }

    /**
     * Updates the quantity of an existing cart item.
     * @return true if updated, false if not found
     */
    public boolean updateQuantity(String productId, int newQty) {
        for (CartItem item : items) {
            if (item.getProduct().getProductId().equals(productId)) {
                if (newQty <= 0) {
                    return removeItem(productId);
                }
                item.setQuantity(newQty);
                return true;
            }
        }
        return false;
    }

    /**
     * @return total price of all items in the cart
     */
    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void clear() { items.clear(); }

    public boolean isEmpty() { return items.isEmpty(); }

    /** Returns unmodifiable snapshot of items */
    public List<CartItem> getItems() { return new ArrayList<>(items); }

    private int getQuantityInCart(String productId) {
        for (CartItem item : items) {
            if (item.getProduct().getProductId().equals(productId)) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    // ------------------------------------------------------------------ display
    public void printCart() {
        if (items.isEmpty()) {
            System.out.println("  [!] Giỏ hàng của bạn đang trống.");
            return;
        }
        System.out.println("  ┌────────────────────────────────┬────────┬────────────────────┬────────────────────────┐");
        for (CartItem item : items) {
            System.out.println(item);
        }
        System.out.println("  └────────────────────────────────┴────────┴────────────────────┴────────────────────────┘");
        System.out.printf("  >> TỔNG TẠM TÍNH: $%.2f%n", getTotalAmount());
    }
}
