package test;
import model.*;
import java.util.List;
/**
 *  KỊCH BẢN KIỂM THỬ: ExtensibilityDemo (Minh chứng Nguyên lý Open/Closed - OCP)
 *  NGUYÊN LÝ OPEN/CLOSED (SOLID - OCP):
 *  - "Mở để mở rộng (Open for extension)": Thêm một dòng sản phẩm sinh thái thứ 3
 *    (Sản phẩm Tái chế - RecycledProduct) bên cạnh ReusableProduct và OrganicProduct.
 *  - "Đóng để sửa đổi (Closed for modification)": Các lớp Product.java, ShoppingCart.java,
 *    Order.java, Customer.java, CartItem.java, Discountable.java được đóng kín,
 *    không có một dòng code nào bị thay đổi.
 */
public class ExtensibilityDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("=================================================================================");
        System.out.println("   ECOSHOP - CHƯƠNG TRÌNH DEMO TÍNH MỞ RỘNG (EXTENSIBILITY DEMO - TUẦN 4)");
        System.out.println("   Minh chứng Nguyên lý Open/Closed (OCP) qua lớp mở rộng mới: RecycledProduct");
        System.out.println("=================================================================================\n");
        // BƯỚC 1: Khởi tạo sản phẩm tái chế mới (RecycledProduct)
        System.out.println("[BƯỚC 1] Khởi tạo đối tượng lớp con mới RecycledProduct:");
        Product recycledBag = new RecycledProduct(
            "P009",
            "Balo sợi tái chế OceanBound",
            "Balo dệt từ 100% sợi polyester rác thải nhựa biển",
            28.00,
            25,
            "Thời trang xanh",
            "Nhựa đại dương rPET",
            100,
            "GRS & OceanBound Certified"
        );
        System.out.println("  -> Tên sản phẩm : " + recycledBag.getName());
        System.out.println("  -> Đa hình Type : " + recycledBag.getProductType());
        System.out.println("  -> Eco Badge    : " + recycledBag.getEcoBadge());
        System.out.println("  -> Tồn kho gốc  : " + recycledBag.getStockQuantity() + " cái\n");
        // BƯỚC 2: Thêm vào Giỏ hàng hiện hữu (ShoppingCart) mà không sửa ShoppingCart
        System.out.println("[BƯỚC 2] Thêm RecycledProduct vào ShoppingCart (Không sửa class ShoppingCart):");
        Customer vip = new VIPCustomer("C002", "bob", "pass123", "Da Nang", "0905999888", 2);
        ShoppingCart cart = vip.getCart();
        try {
            cart.addItem(recycledBag, 3);
            System.out.println("  [✓] Thêm thành công 3 sản phẩm vào giỏ!");
        } catch (Exception e) {
            System.out.println("  [x] Lỗi: " + e.getMessage());
        }
        System.out.println("  -> Danh sách giỏ hàng hiện tại:");
        cart.printCart();
        System.out.printf("  -> Tổng tạm tính giỏ hàng: $%.2f%n%n", cart.getTotalAmount());
        // BƯỚC 3: Đa hình tính chiết khấu cho khách hàng VIP qua Interface Discountable
        System.out.println("[BƯỚC 3] Tính chiết khấu đơn hàng qua đa hình Interface Discountable:");
        double subtotal = cart.getTotalAmount();
        double discount = vip.calculateDiscount(subtotal);
        double finalAmount = subtotal - discount;
        System.out.printf("  -> Khách hàng       : %s [%s]%n", vip.getUsername(), vip.getCustomerType());
        System.out.printf("  -> Tỷ lệ chiết khấu : %.0f%%%n", ((VIPCustomer) vip).getDiscountRate() * 100);
        System.out.printf("  -> Tiền giảm giá    : -$%.2f%n", discount);
        System.out.printf("  -> Thực thanh toán  : $%.2f%n%n", finalAmount);
        // BƯỚC 4: Tạo Đơn hàng (Order) chứa RecycledProduct mà không sửa class Order
        System.out.println("[BƯỚC 4] Đóng gói đơn hàng Order (Không sửa class Order):");
        Order order = vip.placeOrder();
        System.out.println("  [✓] Đơn hàng đã được tạo thành công:");
        System.out.println("  " + order);
        System.out.println();
        // BƯỚC 5: Trừ kho tự động qua Đa hình trong PaymentProcessor mà không sửa PaymentProcessor
        System.out.println("[BƯỚC 5] Giả lập thanh toán & Trừ kho tự động qua đa hình Product.reduceStock():");
        try {
            for (CartItem item : order.getItems()) {
                item.getProduct().reduceStock(item.getQuantity());
                System.out.printf("  -> Đã trừ %d cái '%s' khỏi kho! Tồn kho mới: %d cái%n",
                    item.getQuantity(), item.getProduct().getName(), item.getProduct().getStockQuantity());
            }
            order.setStatus(OrderStatus.CONFIRMED);
            System.out.println("  [✓] Đơn hàng chuyển sang trạng thái: " + order.getStatus());
        } catch (Exception e) {
            System.out.println("  [x] Lỗi: " + e.getMessage());
        }
        // BƯỚC 6: Xuất chuỗi JSON đa hình qua Interface JsonSerializable
        System.out.println("\n[BƯỚC 6] Xuất dữ liệu JSON đa hình (toJson()):");
        System.out.println("  " + recycledBag.toJson());

        System.out.println("\n=================================================================================");
        System.out.println("   KẾT LUẬN: ĐÃ CHỨNG MINH THÀNH CÔNG NGUYÊN LÝ OPEN/CLOSED (SOLID - OCP)!");
        System.out.println("   Lớp RecycledProduct hoạt động hoàn hảo 100% với hệ thống mà không phải sửa");
        System.out.println("   đổi bất kỳ lớp thực thể cốt lõi cũ nào (Product, ShoppingCart, Order...)");
        System.out.println("=================================================================================");
    }
}