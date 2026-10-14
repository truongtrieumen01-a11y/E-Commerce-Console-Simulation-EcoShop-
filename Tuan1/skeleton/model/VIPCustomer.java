package skeleton.model;

import java.time.LocalDate;

/**
 * Lớp VIPCustomer — Khách hàng thân thiết VIP, nhận chiết khấu trực tiếp theo cấp bậc.
 *
 * Cấp bậc chiết khấu:
 *  - Cấp 1 (VIP Bạc): giảm 10%
 *  - Cấp 2 (VIP Vàng): giảm 15%
 *  - Cấp 3 (VIP Bạch Kim): giảm 20%
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class VIPCustomer extends Customer {

    private int vipLevel;
    private LocalDate memberSince;

    public VIPCustomer(String userId, String username, String password,
                       String address, String phoneNumber, int vipLevel) {
        super(userId, username, password, address, phoneNumber);
        this.vipLevel = (vipLevel >= 1 && vipLevel <= 3) ? vipLevel : 1;
        this.memberSince = LocalDate.now();
    }

    @Override
    public double calculateDiscount(double totalAmount) {
        // TODO: Tính toán giảm giá theo vipLevel trong Tuần 1
        return 0.0;
    }

    @Override
    public String getCustomerType() {
        // TODO: Trả về chuỗi VIP Bạc, VIP Vàng hoặc VIP Bạch Kim
        return "VIP";
    }

    @Override
    public String getRole() {
        return "KHÁCH HÀNG";
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int level) {
        // TODO: Kiểm tra phạm vi 1-3 trước khi gán
        this.vipLevel = level;
    }

    public double getDiscountRate() {
        // TODO: Trả về tỉ lệ 0.10, 0.15 hoặc 0.20
        return 0.10;
    }

    public LocalDate getMemberSince() {
        return memberSince;
    }

    @Override
    public String toJson() {
        // TODO: Xuất chuỗi JSON đối tượng Khách VIP
        return "";
    }
}
