package skeleton.model;

import skeleton.exception.EcoShopException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp trừu tượng Customer — Kế thừa User, hiện thực giao diện Discountable.
 *
 * Đại diện cho nhóm người dùng mua sắm.
 * Không yêu cầu thuộc tính email, tập trung vào địa chỉ và số điện thoại.
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public abstract class Customer extends User implements Discountable {

    protected String address;
    protected String phoneNumber;
    protected ShoppingCart cart;
    protected List<Order> orderHistory;

    public Customer(String userId, String username, String password,
                    String address, String phoneNumber) {
        super(userId, username, password);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.cart = new ShoppingCart();
        this.orderHistory = new ArrayList<>();
    }

    /**
     * Phương thức trừu tượng tính chiết khấu đa hình.
     * RegularCustomer và VIPCustomer sẽ ghi đè với logic khác nhau.
     */
    @Override
    public abstract double calculateDiscount(double totalAmount);

    /**
     * Trả về tên hiển thị loại khách hàng (VD: Khách Thường, VIP Vàng...).
     */
    public abstract String getCustomerType();

    /**
     * Tạo đơn hàng mới từ các mặt hàng trong giỏ hàng hiện tại.
     *
     * @return đơn hàng Order vừa tạo
     * @throws EcoShopException nếu giỏ hàng rỗng hoặc tồn kho không đủ
     */
    public Order placeOrder() throws EcoShopException {
        // TODO: Kiểm tra giỏ hàng, trừ tồn kho và tạo đối tượng Order trong Tuần 1
        return null;
    }

    // Getters and Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }
}
