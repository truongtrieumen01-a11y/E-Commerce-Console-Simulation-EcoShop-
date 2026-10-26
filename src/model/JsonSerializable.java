package model;

/**
 * ============================================================
 *  Giao diện JsonSerializable — Hợp đồng lưu trữ dữ liệu JSON.
 * ============================================================
 *
 * KHÁI NIỆM "INTERFACE" TRONG OOP:
 *   Interface là một "hợp đồng" (contract) — bất kỳ lớp nào implements
 *   nó đều phải cung cấp cài đặt cho TẤT CẢ phương thức trong interface.
 *
 * TẠI SAO DÙNG INTERFACE THAY VÌ ABSTRACT CLASS?
 *   - Interface chỉ khai báo "phải làm gì" (what), không quy định "làm thế nào" (how).
 *   - Một lớp chỉ extends được 1 abstract class, nhưng implements được NHIỀU interface.
 *   - Ví dụ: Customer extends User AND implements Discountable (2 interface cùng lúc).
 *
 * AI IMPLEMENTS GIAO DIỆN NÀY?
 *   User (và các lớp con: Admin, RegularCustomer, VIPCustomer) → implements JsonSerializable
 *   Product → implements JsonSerializable
 *   Order   → implements JsonSerializable
 *
 * MỤC ĐÍCH:
 *   JsonDataManager có thể gọi obj.toJson() mà KHÔNG cần biết obj là gì —
 *   chỉ cần biết obj implements JsonSerializable là đủ.
 *   Đây là ví dụ của: Abstraction (trừu tượng hóa) + Polymorphism (đa hình).
 *
 *   Ví dụ trong JsonDataManager.saveUsers():
 *     for (User user : users) {
 *         sb.append(user.toJson());  // Gọi toJson() mà không biết là Admin hay Customer
 *     }
 *
 * TRỤ CỘT OOP ĐƯỢC THỂ HIỆN:
 *  - Trừu tượng (Abstraction): ẩn chi tiết cài đặt JSON của từng lớp
 *  - Đa hình (Polymorphism)  : toJson() trả về kết quả khác nhau tùy loại đối tượng
 */
public interface JsonSerializable {

    /**
     * Chuyển đối tượng thành chuỗi JSON để ghi vào file.
     *
     * Mỗi lớp implements sẽ tự quyết định format JSON của mình:
     *   - Admin.toJson()           → {"type":"Admin", "adminLevel":1, ...}
     *   - RegularCustomer.toJson() → {"type":"RegularCustomer", "loyaltyPoints":500, ...}
     *   - VIPCustomer.toJson()     → {"type":"VIPCustomer", "vipLevel":2, ...}
     *   - Product.toJson()         → {"productId":"P001", "price":1299.99, ...}
     *   - Order.toJson()           → {"orderId":"ORD-C001-...", "status":"PENDING", ...}
     *
     * Lưu ý: KHÔNG dùng thư viện ngoài (Gson, Jackson...) — tự xây chuỗi JSON thủ công
     * bằng String.format() hoặc StringBuilder để đảm bảo project chạy không cần dependency.
     *
     * @return Chuỗi JSON đại diện cho đối tượng này
     */
    String toJson();
}
