package model;

/**
 * ============================================================================
 * LỚP CON MỞ RỘNG (TUẦN 4): RecycledProduct (Sản phẩm Tái chế — Minh chứng OCP)
 * ============================================================================
 *
 * VAI TRÒ TRONG ĐỀ TÀI ECOSHOP (EXTENSIBILITY DEMO):
 * - Đại diện cho dòng sản phẩm sinh thái được sản xuất từ vật liệu rác thải tái
 * chế:
 * Balo dệt từ sợi rác thải đại dương (rPET), Sổ tay bìa bã cà phê, Giày sợi dệt
 * tái chế...
 * - ĐÓNG VAI TRÒ LÀ MINH CHỨNG SỐNG CHO NGUYÊN LÝ OPEN/CLOSED (SOLID - OCP):
 * + "Open for extension": Hệ thống EcoShop dễ dàng mở rộng thêm dòng sản phẩm
 * mới
 * bằng cách tạo lớp con RecycledProduct kế thừa Product.
 * + "Closed for modification": Tuyệt đối KHÔNG cần chỉnh sửa hay can thiệp vào
 * mã
 * nguồn của các lớp cốt lõi có sẵn: Product, ShoppingCart, Order,
 * PaymentProcessor.
 *
 * 4 TRỤ CỘT OOP ĐƯỢC THỂ HIỆN:
 * ┌────────────────────┬──────────────────────────────────────────────────────────────┐
 * │ Kế thừa │ extends Product: Kế thừa toàn bộ thuộc tính chung │
 * │ (Inheritance) │ (productId, name, description, price, stockQuantity,
 * category)│
 * │ │ và mở rộng thêm 3 thuộc tính sinh thái đặc thù. │
 * ├────────────────────┼──────────────────────────────────────────────────────────────┤
 * │ Đóng gói │ recycledMaterial, recycledPercentage, recycleStandard là │
 * │ (Encapsulation) │ private. Kiểm tra chặt chẽ điều kiện bất biến
 * (Invariant): │
 * │ │ tỷ lệ tái chế bắt buộc nằm trong khoảng [1..100]%. │
 * ├────────────────────┼──────────────────────────────────────────────────────────────┤
 * │ Đa hình │ Override các phương thức trừu tượng của Product: │
 * │ (Polymorphism) │ + getProductType(): trả về "Sản Phẩm Tái Chế (Recycled)" │
 * │ │ + getEcoBadge(): định dạng nhãn xanh với biểu tượng ♻️ │
 * │ │ + getSpecificInfo(): tóm tắt tỷ lệ và chuẩn tái chế quốc tế │
 * │ │ + toJson(): xuất cấu trúc JSON có trường type="RecycledProduct"
 * ├────────────────────┼──────────────────────────────────────────────────────────────┤
 * │ Trừu tượng │ Cài đặt tường minh toàn bộ giao ước trừu tượng mà Product │
 * │ (Abstraction) │ và giao diện JsonSerializable yêu cầu. │
 * └────────────────────┴──────────────────────────────────────────────────────────────┘
 */
public class RecycledProduct extends Product {

    // ========================================================================
    // PHẦN 1: THUỘC TÍNH ĐẶC THÙ CỦA SẢN PHẨM TÁI CHẾ (Encapsulation)
    // ========================================================================

    /**
     * Chất liệu rác thải tái chế đầu vào
     * (Ví dụ: "Nhựa đại dương rPET", "Bã cà phê & Sợi hữu cơ", "Sợi bông tái chế").
     */
    private String recycledMaterial;

    /**
     * Tỷ lệ thành phần tái chế trong sản phẩm (tính theo %).
     * Điều kiện bất biến (Invariant): 1 <= recycledPercentage <= 100.
     */
    private int recycledPercentage;

    /**
     * Tiêu chuẩn chứng nhận tái chế quốc tế
     * (Ví dụ: "GRS - Global Recycled Standard", "RCS Certified", "OceanBound
     * Certified").
     */
    private String recycleStandard;

    // ========================================================================
    // PHẦN 2: CONSTRUCTOR (Hàm khởi tạo đối tượng)
    // ========================================================================

    /**
     * Khởi tạo một Sản phẩm Tái chế với đầy đủ thông tin cơ bản và thuộc tính tái
     * chế.
     *
     * @param productId          Mã sản phẩm (ví dụ: "P009")
     * @param name               Tên sản phẩm
     * @param description        Mô tả sản phẩm
     * @param price              Giá bán niêm yết ($)
     * @param stockQuantity      Số lượng tồn kho ban đầu
     * @param category           Danh mục sản phẩm
     * @param recycledMaterial   Chất liệu tái chế đầu vào
     * @param recycledPercentage Tỷ lệ thành phần tái chế (1-100%)
     * @param recycleStandard    Tiêu chuẩn kiểm định tái chế quốc tế
     */
    public RecycledProduct(String productId, String name, String description,
            double price, int stockQuantity, String category,
            String recycledMaterial, int recycledPercentage, String recycleStandard) {
        // Gọi constructor của lớp cha Product ở dòng đầu tiên
        super(productId, name, description, price, stockQuantity, category);

        this.recycledMaterial = (recycledMaterial != null && !recycledMaterial.trim().isEmpty())
                ? recycledMaterial.trim()
                : "Vật liệu tái chế";
        // Bảo vệ bất biến: tỷ lệ tái chế từ 1% đến 100%
        if (recycledPercentage < 1) {
            this.recycledPercentage = 1;
        } else if (recycledPercentage > 100) {
            this.recycledPercentage = 100;
        } else {
            this.recycledPercentage = recycledPercentage;
        }

        this.recycleStandard = (recycleStandard != null && !recycleStandard.trim().isEmpty())
                ? recycleStandard.trim()
                : "GRS Certified";
    }

    // ========================================================================
    // PHẦN 3: HIỆN THỰC PHƯƠNG THỨC ĐA HÌNH (Polymorphism Implementation)
    // ========================================================================

    /**
     * ĐA HÌNH: Trả về tên phân loại dòng sản phẩm.
     */
    @Override
    public String getProductType() {
        return "Sản Phẩm Tái Chế (Recycled)";
    }

    /**
     * ĐA HÌNH: Định dạng nhãn sinh thái với biểu tượng tái chế xanh ♻️ kèm tỷ lệ %.
     */
    @Override
    public String getEcoBadge() {
        return String.format("[♻️ Tái chế | Vật liệu: %s | Tỷ lệ: %d%% | Tiêu chuẩn: %s]",
                recycledMaterial, recycledPercentage, recycleStandard);
    }

    /**
     * ĐA HÌNH: Tóm tắt thông số kỹ thuật đặc thù của sản phẩm tái chế.
     */
    @Override
    public String getSpecificInfo() {
        return String.format("Vật liệu tái chế: %s (%d%%) | Tiêu chuẩn: %s",
                recycledMaterial, recycledPercentage, recycleStandard);
    }

    // ========================================================================
    // PHẦN 4: GETTER & SETTER (Đóng gói - Encapsulation & Invariants)
    // ========================================================================

    public String getRecycledMaterial() {
        return recycledMaterial;
    }

    public void setRecycledMaterial(String recycledMaterial) {
        if (recycledMaterial != null && !recycledMaterial.trim().isEmpty()) {
            this.recycledMaterial = recycledMaterial.trim();
        }
    }

    public int getRecycledPercentage() {
        return recycledPercentage;
    }

    /**
     * Setter bảo vệ bất biến: Không cho phép gán tỷ lệ tái chế < 1% hoặc > 100%.
     */
    public void setRecycledPercentage(int recycledPercentage) {
        if (recycledPercentage >= 1 && recycledPercentage <= 100) {
            this.recycledPercentage = recycledPercentage;
        }
    }

    public String getRecycleStandard() {
        return recycleStandard;
    }

    public void setRecycleStandard(String recycleStandard) {
        if (recycleStandard != null && !recycleStandard.trim().isEmpty()) {
            this.recycleStandard = recycleStandard.trim();
        }
    }

    // ========================================================================
    // PHẦN 5: TUẦN TỰ HÓA DỮ LIỆU JSON (Data Persistence)
    // ========================================================================

    /**
     * ĐA HÌNH (Polymorphism):
     * Xuất dữ liệu đối tượng thành chuỗi JSON, bổ sung trường phân loại đa hình
     * "type":"RecycledProduct" để JsonDataManager tái tạo đúng lớp con khi đọc
     * file.
     */
    @Override
    public String toJson() {
        return String.format(
                "{\"type\":\"RecycledProduct\",\"productId\":\"%s\",\"name\":\"%s\",\"description\":\"%s\"," +
                        "\"price\":%.2f,\"stockQuantity\":%d,\"category\":\"%s\"," +
                        "\"recycledMaterial\":\"%s\",\"recycledPercentage\":%d,\"recycleStandard\":\"%s\"}",
                productId,
                name.replace("\"", "\\\""),
                description.replace("\"", "\\\""),
                price,
                stockQuantity,
                category.replace("\"", "\\\""),
                recycledMaterial.replace("\"", "\\\""),
                recycledPercentage,
                recycleStandard.replace("\"", "\\\""));
    }

    // ========================================================================
    // PHẦN 6: HIỂN THỊ DỮ LIỆU
    // ========================================================================

    @Override
    public String toString() {
        return String.format("  │ %-6s │ %-34s │ $%9.2f │ %8d │ %-20s │ [♻️ Tái chế %3d%% - %s] │",
                productId, name, price, stockQuantity, category, recycledPercentage, recycledMaterial);
    }
}
