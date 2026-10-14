package skeleton.model;

/**
 * Enum OrderStatus — Đại diện cho vòng đời trạng thái của một đơn hàng.
 *
 * Minh họa kiểu dữ liệu liệt kê (Enum) trong Java OOP.
 */
public enum OrderStatus {
    PENDING("Chờ xử lý"),
    CONFIRMED("Đã xác nhận"),
    SHIPPED("Đang giao hàng"),
    DELIVERED("Đã giao thành công"),
    CANCELLED("Đã hủy");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
