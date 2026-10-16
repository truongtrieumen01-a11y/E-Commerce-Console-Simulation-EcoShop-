package skeleton.model;
 
/**
 * Lớp Product — Đại diện cho một sản phẩm trong kho hàng EcoShop.
 *
 * Thể hiện nguyên lý Đóng gói (Encapsulation) với kiểm tra ràng buộc giá và tồn kho.
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class Product implements JsonSerializable {

    private String productId;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
    private String category;

    public Product(String productId, String name, String description,
                   double price, int stockQuantity, String category) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public boolean isAvailable() {
        // TODO: Kiểm tra số lượng tồn kho > 0
        return false;
    }

    public void reduceStock(int quantity) {
        // TODO: Trừ tồn kho và ném ngoại lệ nếu không đủ hàng
    }

    public void addStock(int quantity) {
        // TODO: Cộng thêm số lượng tồn kho hợp lệ
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setPrice(double price) {
        // TODO: Kiểm tra giá bán >= 0
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        // TODO: Kiểm tra tồn kho >= 0
        this.stockQuantity = stockQuantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toJson() {
        // TODO: Xuất chuỗi JSON sản phẩm
        return "";
    }

    @Override
    public String toString() {
        return String.format("  │ %-6s │ %-30s │ $%9.2f │ %8d │ %-16s │",
            productId, name, price, stockQuantity, category);
    }
}
