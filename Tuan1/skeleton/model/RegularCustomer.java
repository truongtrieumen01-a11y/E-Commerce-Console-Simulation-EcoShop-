package skeleton.model;

/**
 * Lớp RegularCustomer — Khách hàng thông thường, tích lũy điểm theo đơn hàng.
 *
 * Cơ chế chiết khấu:
 *  - >= 1000 điểm: giảm 5%
 *  - >= 500 điểm: giảm 3%
 *  - >= 100 điểm: giảm 1%
 *  - < 100 điểm: không giảm
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class RegularCustomer extends Customer {

    private int loyaltyPoints;

    public RegularCustomer(String userId, String username, String password,
                           String address, String phoneNumber) {
        super(userId, username, password, address, phoneNumber);
        this.loyaltyPoints = 0;
    }

    @Override
    public double calculateDiscount(double totalAmount) {
        // TODO: Tính chiết khấu dựa trên loyaltyPoints trong Tuần 1
        return 0.0;
    }

    @Override
    public String getCustomerType() {
        return "Khách Thường";
    }

    @Override
    public String getRole() {
        return "KHÁCH HÀNG";
    }

    public void addLoyaltyPoints(int points) {
        // TODO: Cộng dồn điểm tích lũy hợp lệ
    }

    public void earnPointsFromPurchase(double amount) {
        // TODO: Quy đổi tiền thanh toán thành điểm tích lũy ($10 = 1 điểm)
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    @Override
    public String toJson() {
        // TODO: Xuất chuỗi JSON đối tượng Khách Thường
        return "";
    }
}
