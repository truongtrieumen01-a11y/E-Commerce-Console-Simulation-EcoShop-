package skeleton.model;

/**
 * Lớp CartItem — Đại diện cho một món hàng kèm số lượng trong giỏ hàng.
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class CartItem {

    private Product product;
    private int     quantity;

    // ------------------------------------------------------------------ constructors
    public CartItem(Product product, int quantity) {
        this.product  = product;
        this.quantity = quantity;
    }

    // ------------------------------------------------------------------ business methods
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    // ------------------------------------------------------------------ getters / setters
    public Product getProduct()  { return product;  }
    public int     getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity > 0) this.quantity = quantity;
    }

    // ------------------------------------------------------------------ display
    @Override
    public String toString() {
        return String.format("  │ %-30s │ SL: %2d │ Đơn giá: $%8.2f │ Thành tiền: $%9.2f │",
            product.getName(), quantity, product.getPrice(), getSubtotal());
    }
}

