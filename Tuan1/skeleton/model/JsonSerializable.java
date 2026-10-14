package skeleton.model;

/**
 * Giao diện JsonSerializable — Định nghĩa giao ước chuyển đổi đối tượng sang chuỗi JSON.
 *
 * Phục vụ lưu trữ dữ liệu bền vững (Data Persistence).
 */
public interface JsonSerializable {

    /**
     * Chuyển đổi trạng thái đối tượng thành chuỗi định dạng JSON.
     *
     * @return chuỗi JSON đại diện cho đối tượng
     */
    String toJson();
}
