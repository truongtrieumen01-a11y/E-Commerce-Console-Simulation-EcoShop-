package skeleton.model;

import skeleton.exception.InsufficientStockException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp ShoppingCart — Quản lý danh sách các món hàng (CartItem) của khách hàng.
 *
 * Thể hiện nguyên lý Hợp thành (Composition) và Đóng gói (Encapsulation).
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class ShoppingCart {

    private List<CartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    @throws InsufficientStockException
    public void addItem(Product product, int quantity) throws InsufficientStockException {
        // Kiểm tra tồn kho, cộng dồn nếu đã có hoặc thêm mới CartItem 
         if (quantity <= 0) return;

        int currentInCart = getQuantityInCart(product.getProductId());
        int totalRequested = currentInCart + quantity;
        if (totalRequested > product.getStockQuantity()) {
            throw new InsufficientStockException(product.getName(), totalRequested, product.getStockQuantity());
        }

        for (CartItem item : items) {
            if (item.getProduct().getProductId().equals(product.getProductId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }
    

    public boolean removeItem(String productId) {
        //Xóa sản phẩm khỏi giỏ theo mã
        return items.removeIf(item -> item.getProduct().getProductId().equals(productId));
    }

    public boolean updateQuantity(String productId, int newQty) {
        // TODO: Cập nhật số lượng của món hàng
        return false;
    }
    @return // true thì updaate, false thì ko tìm thấy

    public double getTotalAmount() {
        //Tính tổng tiền của tất cả các món trong giỏ
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
    @return

    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void clear() { items.clear(); }

    public boolean isEmpty() { return items.isEmpty(); }

    public List<CartItem> getItems() {
        // Áp dụng Defensive Copying
        private int getQuantityInCart(String productId) {
        for (CartItem item : items) {
            if (item.getProduct().getProductId().equals(productId)) {
                return item.getQuantity();
            }
        }
        return 0;
    }
    

    public void printCart() {
        //In giao diện giỏ hàng dạng bảng
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
