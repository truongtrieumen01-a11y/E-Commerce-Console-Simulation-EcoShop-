package model;

/**
 * ============================================================================
 *  LỚP CON: ReusableProduct (Sản phẩm Tái sử dụng — Nhánh Sinh Thái Bền Vững)
 * ============================================================================
 *
 * 🎯 VAI TRÒ TRONG ĐỀ TÀI ECOSHOP:
 *  - Đại diện cho các mặt hàng sinh thái có khả năng tái sử dụng nhiều lần để
 *    thay thế đồ nhựa dùng một lần: Bình giữ nhiệt, Túi vải Canvas, Hộp thủy tinh,
 *    Ống hút inox, Bộ muỗng nĩa gỗ tre...
 *
 * 🛡️ 4 TRỤ CỘT OOP ĐƯỢC THỂ HIỆN:
 *  ┌────────────────────┬──────────────────────────────────────────────────────────────┐
 *  │ Kế thừa            │ extends Product: Tái sử dụng toàn bộ thuộc tính chung        │
 *  │ (Inheritance)      │ (productId, name, description, price, stockQuantity, category)│
 *  │                    │ và mở rộng thêm 3 thuộc tính sinh thái đặc thù.              │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Đóng gói           │ material, estimatedReuseCount, ecoCertificate là private.    │
 *  │ (Encapsulation)    │ Bên ngoài muốn truy xuất/chỉnh sửa phải thông qua Getter     │
 *  │                    │ và Setter với kiểm tra hợp lệ (số lần tái sử dụng >= 1).     │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Đa hình            │ Override các phương thức trừu tượng của Product:              │
 *  │ (Polymorphism)     │  + getProductType(): trả về "Sản Phẩm Tái Sử Dụng"           │
 *  │                    │  + getEcoBadge(): định dạng nhãn xanh với biểu tượng ♻️       │
 *  │                    │  + getSpecificInfo(): tóm tắt chất liệu và số lần tái dùng  │
 *  │                    │  + toJson(): xuất cấu trúc JSON có trường type="ReusableProduct"
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Trừu tượng         │ Cài đặt cụ thể các hành vi mà Product và JsonSerializable    │
 *  │ (Abstraction)      │ yêu cầu, ẩn đi chi tiết đóng gói chuỗi JSON bên trong.       │
 *  └────────────────────┴──────────────────────────────────────────────────────────────┘
 */
public class ReusableProduct extends Product {

    // ========================================================================
    // PHẦN 1: THUỘC TÍNH ĐẶC THÙ CỦA SẢN PHẨM TÁI SỬ DỤNG
    // ========================================================================

    /**
     * Chất liệu bền vững / thân thiện với môi trường của sản phẩm
     * (Ví dụ: "Thép không gỉ 304", "Thủy tinh Borosilicate", "Gỗ tre tự nhiên", "Vải Canvas hữu cơ").
     */
    private String material;

    /**
     * Số lần tái sử dụng ước tính theo chứng nhận của nhà sản xuất
     * (Ví dụ: 500 lần, 1000 lần, 2000 lần...). Luôn luôn >= 1.
     */
    private int estimatedReuseCount;

    /**
     * Chứng chỉ môi trường hoặc tiêu chuẩn an toàn thực phẩm
     * (Ví dụ: "BPA-Free", "FSC Certified", "Cradle to Cradle", "ISO 14001", "FDA Approved").
     */
    private String ecoCertificate;

    // ========================================================================
    // PHẦN 2: CONSTRUCTOR (Hàm khởi tạo đối tượng)
    // ========================================================================

    /**
     * Khởi tạo một Sản phẩm Tái sử dụng với đầy đủ thông tin cơ bản và thuộc tính sinh thái.
     *
     * @param productId           Mã sản phẩm (ví dụ: "P001")
     * @param name                Tên sản phẩm
     * @param description         Mô tả sản phẩm
     * @param price               Giá bán niêm yết ($)
     * @param stockQuantity       Số lượng tồn kho ban đầu
     * @param category            Danh mục sản phẩm
     * @param material            Chất liệu chế tạo
     * @param estimatedReuseCount Số lần tái sử dụng ước tính
     * @param ecoCertificate      Chứng chỉ sinh thái / Tiêu chuẩn xanh
     */
    public ReusableProduct(String productId, String name, String description,
                           double price, int stockQuantity, String category,
                           String material, int estimatedReuseCount, String ecoCertificate) {
        // Bắt buộc gọi constructor của lớp cha Product ở dòng đầu tiên
        super(productId, name, description, price, stockQuantity, category);
        this.material            = (material != null && !material.trim().isEmpty()) ? material.trim() : "Chất liệu tái chế";
        this.estimatedReuseCount = (estimatedReuseCount >= 1) ? estimatedReuseCount : 100;
        this.ecoCertificate      = (ecoCertificate != null && !ecoCertificate.trim().isEmpty()) ? ecoCertificate.trim() : "Tiêu chuẩn Eco";
    }

    // ========================================================================
    // PHẦN 3: HIỆN THỰC PHƯƠNG THỨC ĐA HÌNH (Polymorphism Implementation)
    // ========================================================================

    /**
     * ĐA HÌNH: Trả về phân loại dòng sản phẩm.
     */
    @Override
    public String getProductType() {
        return "Sản Phẩm Tái Sử Dụng (Reusable)";
    }

    /**
     * ĐA HÌNH: Định dạng nhãn sinh thái với biểu tượng tái chế xanh ♻️.
     */
    @Override
    public String getEcoBadge() {
        return String.format("[♻️ Tái sử dụng | Chất liệu: %s | Độ bền: ~%d lần | CN: %s]",
            material, estimatedReuseCount, ecoCertificate);
    }

    /**
     * ĐA HÌNH: Tóm tắt thông số kỹ thuật đặc thù của sản phẩm tái sử dụng.
     */
    @Override
    public String getSpecificInfo() {
        return String.format("Chất liệu: %s | Tái dùng: ~%d lần | Chứng nhận: %s",
            material, estimatedReuseCount, ecoCertificate);
    }

    // ========================================================================
    // PHẦN 4: GETTER & SETTER (Đóng gói - Encapsulation)
    // ========================================================================

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        if (material != null && !material.trim().isEmpty()) {
            this.material = material.trim();
        }
    }

    public int getEstimatedReuseCount() {
        return estimatedReuseCount;
    }

    public void setEstimatedReuseCount(int estimatedReuseCount) {
        if (estimatedReuseCount >= 1) {
            this.estimatedReuseCount = estimatedReuseCount;
        }
    }

    public String getEcoCertificate() {
        return ecoCertificate;
    }

    public void setEcoCertificate(String ecoCertificate) {
        if (ecoCertificate != null && !ecoCertificate.trim().isEmpty()) {
            this.ecoCertificate = ecoCertificate.trim();
        }
    }

    // ========================================================================
    // PHẦN 5: TUẦN TỰ HÓA DỮ LIỆU JSON (Data Persistence)
    // ========================================================================

    /**
     * ĐA HÌNH (Polymorphism):
     * Xuất dữ liệu đối tượng thành chuỗi JSON, bổ sung trường phân loại đa hình
     * "type":"ReusableProduct" để JsonDataManager tái tạo đúng lớp con khi đọc file.
     *
     * Chuỗi JSON mẫu:
     * {
     *   "type":"ReusableProduct",
     *   "productId":"P001",
     *   "name":"Bình giữ nhiệt Inox 304",
     *   "description":"Bình giữ nhiệt cách nhiệt chân không 500ml",
     *   "price":18.50,
     *   "stockQuantity":35,
     *   "category":"Gia dụng xanh",
     *   "material":"Inox 304 & Nắp tre",
     *   "estimatedReuseCount":2000,
     *   "ecoCertificate":"BPA-Free & FDA"
     * }
     */
    @Override
    public String toJson() {
        return String.format(
            "{\"type\":\"ReusableProduct\",\"productId\":\"%s\",\"name\":\"%s\",\"description\":\"%s\"," +
            "\"price\":%.2f,\"stockQuantity\":%d,\"category\":\"%s\"," +
            "\"material\":\"%s\",\"estimatedReuseCount\":%d,\"ecoCertificate\":\"%s\"}",
            productId,
            name.replace("\"", "\\\""),
            description.replace("\"", "\\\""),
            price,
            stockQuantity,
            category.replace("\"", "\\\""),
            material.replace("\"", "\\\""),
            estimatedReuseCount,
            ecoCertificate.replace("\"", "\\\"")
        );
    }

    // ========================================================================
    // PHẦN 6: HIỂN THỊ DỮ LIỆU
    // ========================================================================

    @Override
    public String toString() {
        return String.format("  │ %-6s │ %-30s │ $%9.2f │ %8d │ %-16s │ [♻️ %-12s - %4d lần] │",
            productId, name, price, stockQuantity, category, material, estimatedReuseCount);
    }
}
