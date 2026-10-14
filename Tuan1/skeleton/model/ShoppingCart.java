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

    public void addItem(Product product, int quantity) throws InsufficientStockException {
        // TODO: Kiểm tra tồn kho, cộng dồn nếu đã có hoặc thêm mới CartItem trong Tuần 1
    }

    public boolean removeItem(String productId) {
        // TODO: Xóa sản phẩm khỏi giỏ theo mã
        return false;
    }

    public boolean updateQuantity(String productId, int newQty) {
        // TODO: Cập nhật số lượng của món hàng
        return false;
    }

    public double getTotalAmount() {
        // TODO: Tính tổng tiền của tất cả các món trong giỏ
        return 0.0;
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CartItem> getItems() {
        // Áp dụng Defensive Copying
        return new ArrayList<>(items);
    }

    public void printCart() {
        // TODO: In giao diện giỏ hàng dạng bảng
    }
}
