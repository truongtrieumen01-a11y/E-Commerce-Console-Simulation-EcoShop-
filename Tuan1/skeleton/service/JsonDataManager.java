package skeleton.service;
 
import skeleton.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp JsonDataManager — Hiện thực IDataManager với cơ chế đọc/ghi tệp JSON thuần Java SE.
 *
 * Hoàn toàn không sử dụng thư viện ngoài (Gson, Jackson) theo yêu cầu học phần OOP.
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class JsonDataManager {

    private final String dataDirectory;
    private final String usersFile;
    private final String productsFile;
    private final String ordersFile;

    public JsonDataManager(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.usersFile    = dataDirectory + "/users.json";
        this.productsFile = dataDirectory + "/products.json";
        this.ordersFile   = dataDirectory + "/orders.json";
    }

    public List<User> loadUsers() {
        // TODO: Đọc tệp users.json và parse mảng đa hình trong Tuần 1
        return new ArrayList<>();
    }

    public void saveUsers(List<User> users) {
        // TODO: Chuyển đổi danh sách users sang mảng JSON và ghi tệp trong Tuần 1
    }

    public List<Product> loadProducts() {
        // TODO: Đọc tệp products.json và parse danh mục sản phẩm trong Tuần 1
        return new ArrayList<>();
    }

    public void saveProducts(List<Product> products) {
        // TODO: Ghi danh sách products xuống tệp JSON trong Tuần 1
    }

    public List<Order> loadOrders(List<Customer> customers, List<Product> products) {
        // TODO: Đọc danh sách đơn hàng từ tệp orders.json và liên kết khách hàng, sản phẩm
        return new ArrayList<>();
    }

    public void saveOrders(List<Order> orders) {
        // TODO: Ghi danh sách orders lồng ghép items xuống tệp JSON trong Tuần 1
    }
}
