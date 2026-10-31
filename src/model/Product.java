package model;

/**
 * ============================================================================
 *  LỚP TRỪU TƯỢNG: Product (Sản phẩm cơ sở - Lớp cha của toàn bộ hàng hóa)
 * ============================================================================
 *
 * 🎯 VAI TRÒ TRONG ĐỀ TÀI ECOSHOP:
 *  - Đại diện cho thông tin chung của tất cả các sản phẩm sinh thái trong cửa hàng.
 *  - Ở Tuần 2, theo định hướng của GVHD và cam kết kiến trúc, lớp Product được
 *    chuyển thành LỚP TRỪU TƯỢNG (abstract class) và làm gốc cho 2 nhánh kế thừa:
 *      1. ReusableProduct (Sản phẩm tái sử dụng: bình nước, túi vải, ống hút inox...)
 *      2. OrganicProduct  (Sản phẩm hữu cơ: nông sản sinh thái, trà, xà phòng tự nhiên...)
 *
 * 🛡️ 4 TRỤ CỘT LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG (OOP):
 *  ┌────────────────────┬──────────────────────────────────────────────────────────────┐
 *  │ Đóng gói           │ Tất cả thuộc tính đều khai báo protected/private, chỉ cho    │
 *  │ (Encapsulation)    │ phép truy xuất qua getter và setter có kiểm tra tính hợp lệ  │
 *  │                    │ (giá tiền >= 0, số lượng tồn kho >= 0).                      │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Trừu tượng         │ Khai báo abstract class Product và các phương thức thuần ảo: │
 *  │ (Abstraction)      │ getProductType(), getEcoBadge(), getSpecificInfo().          │
 *  │                    │ Implements giao diện JsonSerializable.                       │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Kế thừa            │ ReusableProduct và OrganicProduct kế thừa toàn bộ thuộc tính │
 *  │ (Inheritance)      │ mã, tên, giá, tồn kho, danh mục và phương thức quản lý kho.   │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Đa hình            │ Cùng gọi product.getEcoBadge() hoặc product.toJson(), nhưng  │
 *  │ (Polymorphism)     │ ReusableProduct và OrganicProduct xuất dữ liệu hoàn toàn khác│
 *  │                    │ nhau dựa trên kiểu đối tượng thực tế lúc chạy (Runtime).     │
 *  └────────────────────┴──────────────────────────────────────────────────────────────┘
 */
public abstract class Product implements JsonSerializable {

    // ========================================================================
    // PHẦN 1: THUỘC TÍNH (Thuộc tính đóng gói - Encapsulation)
    // ========================================================================

    /**
     * Mã định danh duy nhất của sản phẩm (ví dụ: "P001", "P002").
     * Khai báo protected để lớp con có thể truy xuất trực tiếp khi cần.
     */
    protected String productId;

    /**
     * Tên sản phẩm hiển thị trên danh mục (ví dụ: "Bình giữ nhiệt Inox 304").
     */
    protected String name;

    /**
     * Mô tả chi tiết về sản phẩm, xuất xứ hoặc công dụng bảo vệ môi trường.
     */
    protected String description;

    /**
     * Giá bán niêm yết tính bằng USD ($). Luôn luôn >= 0.
     */
    protected double price;

    /**
     * Số lượng hàng hóa hiện có sẵn trong kho. Luôn luôn >= 0.
     */
    protected int stockQuantity;

    /**
     * Phân loại danh mục (ví dụ: "Gia dụng xanh", "Đồ dùng tái chế", "Thực phẩm sinh thái").
     */
    protected String category;

    // ========================================================================
    // PHẦN 2: CONSTRUCTOR (Hàm khởi tạo lớp cha)
    // ========================================================================

    /**
     * Khởi tạo một đối tượng Product cơ sở.
     * Phương thức này được các lớp con (ReusableProduct, OrganicProduct) gọi
     * thông qua từ khóa `super(...)`.
     *
     * @param productId     Mã sản phẩm (duy nhất)
     * @param name          Tên sản phẩm
     * @param description   Mô tả sản phẩm
     * @param price         Giá bán ($) - phải >= 0
     * @param stockQuantity Số lượng tồn kho - phải >= 0
     * @param category      Danh mục sản phẩm
     */
    public Product(String productId, String name, String description,
                   double price, int stockQuantity, String category) {
        this.productId     = productId;
        this.name          = name;
        this.description   = description;
        this.price         = (price >= 0) ? price : 0.0;
        this.stockQuantity = (stockQuantity >= 0) ? stockQuantity : 0;
        this.category      = category;
    }

    // ========================================================================
    // PHẦN 3: PHƯƠNG THỨC TRỪU TƯỢNG (Abstract Methods - Tính Đa Hình)
    // ========================================================================

    /**
     * ĐA HÌNH (Polymorphism):
     * Phương thức trừu tượng trả về tên phân loại dòng sản phẩm.
     * Lớp con BUỘC phải override phương thức này:
     *   - ReusableProduct trả về: "Sản Phẩm Tái Sử Dụng"
     *   - OrganicProduct trả về: "Sản Phẩm Hữu Cơ"
     *
     * @return Tên loại sản phẩm bằng tiếng Việt có dấu
     */
    public abstract String getProductType();

    /**
     * ĐA HÌNH (Polymorphism):
     * Trả về huy hiệu sinh thái / nhãn xanh của sản phẩm để hiển thị trên Console.
     *   - ReusableProduct: hiển thị chất liệu, số lần tái sử dụng, chứng chỉ xanh.
     *   - OrganicProduct: hiển thị hạn sử dụng, chứng chỉ hữu cơ, hướng dẫn bảo quản.
     *
     * @return Chuỗi mô tả huy hiệu sinh thái
     */
    public abstract String getEcoBadge();

    /**
     * ĐA HÌNH (Polymorphism):
     * Trả về thông tin đặc thù của dòng sản phẩm (dùng khi in bảng hoặc chi tiết).
     *
     * @return Chuỗi tóm tắt thông số kỹ thuật đặc thù
     */
    public abstract String getSpecificInfo();

    // ========================================================================
    // PHẦN 4: NGHIỆP VỤ KHO HÀNG (Business Logic - Quản lý tồn kho)
    // ========================================================================

    /**
     * Kiểm tra sản phẩm có còn hàng trong kho để bán không.
     *
     * @return true nếu tồn kho > 0, false nếu hết hàng
     */
    public boolean isAvailable() {
        return stockQuantity > 0;
    }

    /**
     * Trừ số lượng tồn kho khi khách hàng đặt mua thành công.
     * Có cơ chế phòng thủ (Guard Clause) nhằm chống lại lỗi tồn kho âm.
     *
     * @param quantity Số lượng cần xuất bán
     * @throws IllegalArgumentException Nếu số lượng yêu cầu vượt quá tồn kho thực tế
     */
    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            return;
        }
        if (quantity > this.stockQuantity) {
            throw new IllegalArgumentException(
                String.format("Không thể trừ tồn kho! Sản phẩm '%s' chỉ còn %d cái trong kho (yêu cầu: %d).",
                    name, stockQuantity, quantity)
            );
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Tăng số lượng tồn kho khi Quản trị viên nhập thêm hàng hoặc khi khách hủy đơn.
     *
     * @param quantity Số lượng nhập thêm (phải > 0)
     */
    public void addStock(int quantity) {
        if (quantity > 0) {
            this.stockQuantity += quantity;
        }
    }

    // ========================================================================
    // PHẦN 5: GETTER & SETTER (Đóng gói dữ liệu - Encapsulation)
    // ========================================================================

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    /**
     * Cập nhật giá bán mới. Ràng buộc nghiệp vụ: giá bán không được âm (< 0).
     *
     * @param price Giá bán mới ($)
     */
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Cập nhật số lượng tồn kho. Ràng buộc nghiệp vụ: số lượng tồn kho không được âm.
     *
     * @param stockQuantity Số lượng tồn mới
     */
    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity >= 0) {
            this.stockQuantity = stockQuantity;
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            this.category = category.trim();
        }
    }

    // ========================================================================
    // PHẦN 6: HIỂN THỊ DỮ LIỆU
    // ========================================================================

    /**
     * Định dạng chuỗi hiển thị sản phẩm trên danh sách Console theo định dạng bảng.
     */
    @Override
    public String toString() {
        return String.format("  │ %-6s │ %-30s │ $%9.2f │ %8d │ %-16s │",
            productId, name, price, stockQuantity, category);
    }
}
