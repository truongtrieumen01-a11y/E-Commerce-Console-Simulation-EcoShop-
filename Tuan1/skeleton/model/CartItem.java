package skeleton.model;

/**
 * Lớp CartItem — Đại diện cho một món hàng kèm số lượng trong giỏ hàng.
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class CartItem {

    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getSubtotal() {
        // TODO: Tính thành tiền = đơn giá * số lượng
        return 0.0;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        // TODO: Kiểm tra số lượng > 0
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("  │ %-30s │ SL: %2d │ Đơn giá: $%8.2f │ Thành tiền: $%9.2f │",
            product != null ? product.getName() : "", quantity,
            product != null ? product.getPrice() : 0.0, getSubtotal());
    }
}
