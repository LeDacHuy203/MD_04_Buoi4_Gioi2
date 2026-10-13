package com.example.controller;

import com.example.dto.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient = RestClient.create();

    @GetMapping("/getProduct/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable String productId) {
        Product product = getProductFromProductService(productId);
        return ResponseEntity.ok(product);
    }

    public Product getProductFromProductService(String productId) {

        // BƯỚC A: Hỏi Eureka lấy danh sách các máy chủ đang chạy ProductService
        // Lưu ý: Tên service lấy trong file application.properties
        List<ServiceInstance> instances = discoveryClient.getInstances("ProductService");

        // BƯỚC B: Xử lý lỗi (Theo yêu cầu bài tập: Trả về 503 nếu rỗng)
        if (instances == null || instances.isEmpty()) {
            // Bạn có thể throw ra một Exception tuỳ chỉnh, ở đây dùng tạm ResponseStatusException
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "PRODUCT-SERVICE hiện không khả dụng (Không tìm thấy instance nào)"
            );
        }

        // BƯỚC C: Lấy Instance đầu tiên trong danh sách (Tương lai bạn có thể viết logic random để cân bằng tải)
        ServiceInstance productInstance = instances.get(0);

        // BƯỚC D: Lấy URL gốc (Ví dụ: http://localhost:9090) từ instance
        String baseUrl = productInstance.getUri().toString();

        // BƯỚC E: Nối chuỗi để tạo ra API URL hoàn chỉnh
        // Ví dụ: http://localhost:9090/api/v1/products/123
        String targetUrl = "http://ProductService/api/v1/products/" + productId;

        // BƯỚC F: Thực hiện gọi API lấy về sản phẩm
        return restClient.get()
                .uri(targetUrl)
                .retrieve()
                .body(Product.class);
    }
}
