package model;

/**
 * ============================================================================
 *  LỚP CON: OrganicProduct (Sản phẩm Hữu cơ — Nhánh Nông Sản & Hàng Sinh Học)
 * ============================================================================
 *
 * 🎯 VAI TRÒ TRONG ĐỀ TÀI ECOSHOP:
 *  - Đại diện cho các mặt hàng hữu cơ, có nguồn gốc từ thiên nhiên, có hạn sử
 *    dụng (perishable) và yêu cầu tiêu chuẩn chứng nhận hữu cơ nghiêm ngặt:
 *    Trà hữu cơ, Hạt dinh dưỡng sinh thái, Tinh dầu tự nhiên, Xà bông sinh học...
 *
 * 🛡️ 4 TRỤ CỘT OOP ĐƯỢC THỂ HIỆN:
 *  ┌────────────────────┬──────────────────────────────────────────────────────────────┐
 *  │ Kế thừa            │ extends Product: Kế thừa mã, tên, giá, số lượng tồn kho,      │
 *  │ (Inheritance)      │ danh mục và bổ sung 3 thuộc tính quản lý sản phẩm hữu cơ.    │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Đóng gói           │ expiryDate, organicCertification, storageInstruction là      │
 *  │ (Encapsulation)    │ private. Chỉ truy xuất qua các Getter/Setter có kiểm tra.     │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Đa hình            │ Cài đặt riêng biệt các phương thức trừu tượng của Product:    │
 *  │ (Polymorphism)     │  + getProductType(): trả về "Sản Phẩm Hữu Cơ (Organic)"      │
 *  │                    │  + getEcoBadge(): định dạng nhãn hữu cơ với biểu tượng 🌿     │
 *  │                    │  + getSpecificInfo(): tóm tắt HSD và bảo quản                │
 *  │                    │  + toJson(): xuất JSON chứa trường type="OrganicProduct"     │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Trừu tượng         │ Hiện thực giao diện JsonSerializable thông qua hợp đồng của   │
 *  │ (Abstraction)      │ Product, che giấu cấu trúc xử lý chi tiết ngày hết hạn.      │
 *  └────────────────────┴──────────────────────────────────────────────────────────────┘
 */
public class OrganicProduct extends Product {

    // ========================================================================
    // PHẦN 1: THUỘC TÍNH ĐẶC THÙ CỦA SẢN PHẨM HỮU CƠ
    // ========================================================================

    /**
     * Hạn sử dụng của sản phẩm theo định dạng YYYY-MM-DD (Ví dụ: "2027-06-30").
     */
    private String expiryDate;

    /**
     * Chứng chỉ hữu cơ được cấp bởi các tổ chức uy tín
     * (Ví dụ: "USDA Organic", "EU Organic", "JAS Japan", "VietGAP Organic").
     */
    private String organicCertification;

    /**
     * Hướng dẫn bảo quản tối ưu để giữ nguyên giá trị dinh dưỡng và độ tươi mới
     * (Ví dụ: "Bảo quản ngăn mát 2-8°C", "Nơi khô ráo thoáng mát, tránh ánh nắng").
     */
    private String storageInstruction;

    // ========================================================================
    // PHẦN 2: CONSTRUCTOR (Hàm khởi tạo đối tượng)
    // ========================================================================

    /**
     * Khởi tạo Sản phẩm Hữu cơ với đầy đủ thông tin cơ bản và thuộc tính hữu cơ.
     *
     * @param productId            Mã sản phẩm (ví dụ: "P005")
     * @param name                 Tên sản phẩm
     * @param description          Mô tả sản phẩm
     * @param price                Giá bán ($)
     * @param stockQuantity        Số lượng tồn kho ban đầu
     * @param category             Danh mục sản phẩm
     * @param expiryDate           Hạn sử dụng (YYYY-MM-DD)
     * @param organicCertification Chứng nhận hữu cơ
     * @param storageInstruction   Hướng dẫn bảo quản
     */
    public OrganicProduct(String productId, String name, String description,
                          double price, int stockQuantity, String category,
                          String expiryDate, String organicCertification,
                          String storageInstruction) {
        // Gọi hàm khởi tạo của lớp cha Product
        super(productId, name, description, price, stockQuantity, category);
        this.expiryDate           = (expiryDate != null && !expiryDate.trim().isEmpty()) ? expiryDate.trim() : "2027-12-31";
        this.organicCertification = (organicCertification != null && !organicCertification.trim().isEmpty()) ? organicCertification.trim() : "Chuẩn Hữu Cơ";
        this.storageInstruction   = (storageInstruction != null && !storageInstruction.trim().isEmpty()) ? storageInstruction.trim() : "Nhiệt độ phòng";
    }

    // ========================================================================
    // PHẦN 3: HIỆN THỰC PHƯƠNG THỨC ĐA HÌNH (Polymorphism Implementation)
    // ========================================================================

    /**
     * ĐA HÌNH: Trả về phân loại dòng sản phẩm.
     */
    @Override
    public String getProductType() {
        return "Sản Phẩm Hữu Cơ (Organic)";
    }

    /**
     * ĐA HÌNH: Định dạng nhãn sinh thái với biểu tượng mầm cây xanh 🌿.
     */
    @Override
    public String getEcoBadge() {
        return String.format("[🌿 Hữu cơ | HSD: %s | CN: %s | BQ: %s]",
            expiryDate, organicCertification, storageInstruction);
    }

    /**
     * ĐA HÌNH: Tóm tắt thông tin đặc thù của sản phẩm hữu cơ.
     */
    @Override
    public String getSpecificInfo() {
        return String.format("HSD: %s | Chứng nhận: %s | Bảo quản: %s",
            expiryDate, organicCertification, storageInstruction);
    }

    // ========================================================================
    // PHẦN 4: GETTER & SETTER (Đóng gói - Encapsulation)
    // ========================================================================

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        if (expiryDate != null && !expiryDate.trim().isEmpty()) {
            this.expiryDate = expiryDate.trim();
        }
    }

    public String getOrganicCertification() {
        return organicCertification;
    }

    public void setOrganicCertification(String organicCertification) {
        if (organicCertification != null && !organicCertification.trim().isEmpty()) {
            this.organicCertification = organicCertification.trim();
        }
    }

    public String getStorageInstruction() {
        return storageInstruction;
    }

    public void setStorageInstruction(String storageInstruction) {
        if (storageInstruction != null && !storageInstruction.trim().isEmpty()) {
            this.storageInstruction = storageInstruction.trim();
        }
    }

    // ========================================================================
    // PHẦN 5: TUẦN TỰ HÓA DỮ LIỆU JSON (Data Persistence)
    // ========================================================================

    /**
     * ĐA HÌNH (Polymorphism):
     * Xuất dữ liệu đối tượng thành chuỗi JSON, bổ sung trường phân loại đa hình
     * "type":"OrganicProduct" để JsonDataManager khôi phục đúng thực thể khi nạp dữ liệu.
     *
     * Chuỗi JSON mẫu:
     * {
     *   "type":"OrganicProduct",
     *   "productId":"P005",
     *   "name":"Trà hoa cúc sấy lạnh hữu cơ",
     *   "description":"Trà thảo mộc hữu cơ thanh nhiệt 100g",
     *   "price":9.90,
     *   "stockQuantity":45,
     *   "category":"Thực phẩm sinh thái",
     *   "expiryDate":"2027-10-15",
     *   "organicCertification":"USDA Organic & EU Organic",
     *   "storageInstruction":"Nơi khô mát, đậy kín sau khi mở"
     * }
     */
    @Override
    public String toJson() {
        return String.format(
            "{\"type\":\"OrganicProduct\",\"productId\":\"%s\",\"name\":\"%s\",\"description\":\"%s\"," +
            "\"price\":%.2f,\"stockQuantity\":%d,\"category\":\"%s\"," +
            "\"expiryDate\":\"%s\",\"organicCertification\":\"%s\",\"storageInstruction\":\"%s\"}",
            productId,
            name.replace("\"", "\\\""),
            description.replace("\"", "\\\""),
            price,
            stockQuantity,
            category.replace("\"", "\\\""),
            expiryDate.replace("\"", "\\\""),
            organicCertification.replace("\"", "\\\""),
            storageInstruction.replace("\"", "\\\"")
        );
    }

    // ========================================================================
    // PHẦN 6: HIỂN THỊ DỮ LIỆU
    // ========================================================================

    @Override
    public String toString() {
        return String.format("  │ %-6s │ %-30s │ $%9.2f │ %8d │ %-16s │ [🌿 HSD: %-10s - %s] │",
            productId, name, price, stockQuantity, category, expiryDate, organicCertification);
    }
}
