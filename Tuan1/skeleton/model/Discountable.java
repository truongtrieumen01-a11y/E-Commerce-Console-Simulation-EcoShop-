package skeleton.model;

/**
 * Giao diện Discountable — Định nghĩa giao ước tính chiết khấu (Đa hình).
 *
 * Áp dụng nguyên lý Trừu tượng (Abstraction): Bất kỳ thực thể nào có thể hưởng
 * chiết khấu đều phải hiện thực phương thức này.
 */
public interface Discountable {

    /**
     * Tính số tiền được giảm dựa trên tổng giá trị đơn hàng.
     *
     * @param totalAmount tổng tiền trước giảm
     * @return số tiền được giảm giá
     */
    double calculateDiscount(double totalAmount);
}
