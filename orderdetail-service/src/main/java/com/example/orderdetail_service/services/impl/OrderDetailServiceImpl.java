package com.example.orderdetail_service.services.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orderdetail_service.clients.OrderClient;
import com.example.orderdetail_service.clients.ProductClient;
import com.example.orderdetail_service.dtos.request.OrderDetailRequest;
import com.example.orderdetail_service.dtos.response.ApiResponse;
import com.example.orderdetail_service.dtos.response.OrderDetailResponse;
import com.example.orderdetail_service.dtos.response.OrderResponse;
import com.example.orderdetail_service.dtos.response.ProductResponse;
import com.example.orderdetail_service.dtos.response.TopProductStatResponse;
import com.example.orderdetail_service.mappers.OrderDetailMapper;
import com.example.orderdetail_service.models.OrderDetail;
import com.example.orderdetail_service.services.OrderDetailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "ORDER_DETAIL_SERVICE")
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final ProductClient productClient;
    private final OrderClient orderClient;
    private final OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public int createOrderDetail(OrderDetailRequest request) {
        log.info("Creating order detail with request: {}", request);

        ApiResponse<ProductResponse> response = productClient.getProductById(request.getProductId());
        ProductResponse product = response.getData();

        int qty = request.getNumberOfProducts();
        int stock = product.getStockQuantity();

        if(qty > stock){
            log.info("luong hang da het");
            throw new RuntimeException("luong hang da het");
        }

        BigDecimal unitPrice = product.getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(request.getNumberOfProducts()));

        OrderDetail orderDetail = OrderDetail.builder()
                .orderId(request.getOrderId())
                .productId(request.getProductId())
                .price(unitPrice)
                .totalMoney(lineTotal)
                .numberOfProducts(request.getNumberOfProducts())
                .color(request.getColor())
                .build();
        // tao order detail
        int rows = orderDetailMapper.insert(orderDetail);

        // tru kho 
        ApiResponse<String> res = productClient.decreaseStockProduct(request.getProductId(), qty);
        String result = res.getData();

        // cap nhat tong tien order theo orderId
        orderClient.increaseTotal(request.getOrderId(), lineTotal);
        return rows;
    }

    @Override
    public OrderDetailResponse getDetailById(Integer id) {
        return orderDetailMapper.getById(id);
    }

    @Override
    @Transactional
    public OrderDetailResponse updateDetailById(Integer id, OrderDetailRequest request) {

    // 1. Lấy order detail cũ
    OrderDetailResponse orderDetail = orderDetailMapper.getById(id);
    if (orderDetail == null) {
        throw new RuntimeException("OrderDetail not found with id: " + id);
    }

    // 2. Lấy thông tin sản phẩm
    ApiResponse<ProductResponse> response = productClient.getProductById(request.getProductId());
        ProductResponse product = response.getData();

        int oldNumberProduct = orderDetail.getNumberOfProducts();
        int newNumberProduct = request.getNumberOfProducts();
        int delta = newNumberProduct - oldNumberProduct;

        BigDecimal unitPrice = product.getPrice();
        BigDecimal oldLineTotal = orderDetail.getTotalMoney();
        BigDecimal newLineTotal = unitPrice.multiply(BigDecimal.valueOf(newNumberProduct));
        BigDecimal deltaMoney = newLineTotal.subtract(oldLineTotal);

        // 3. Cập nhật stock
        if (delta > 0) {
            ApiResponse<String> res = productClient.decreaseStockProduct(request.getProductId(), delta);
            String result = res.getData();
            log.info("Status {}", result);
        } else if (delta < 0) {
            ApiResponse<String> res = productClient.increaseStockProduct(request.getProductId(), -delta);
            String result = res.getData();
            log.info("Status {}", result);
        }

        // 4. Cập nhật totalMoney của order
        if (deltaMoney.compareTo(BigDecimal.ZERO) > 0) {
            ApiResponse<String> r = orderClient.increaseTotal(orderDetail.getOrderId(), deltaMoney);
            String r1 = r.getData();
        } else if (deltaMoney.compareTo(BigDecimal.ZERO) < 0) {
            ApiResponse<String> r2 = orderClient.decreaseTotal(orderDetail.getOrderId(), deltaMoney.abs());
            String r3 = r2.getData();
        }

        // 5. Cập nhật order_detail trong DB
        OrderDetail orderDetail1 = OrderDetail.builder()
                .id(id)
                .orderId(orderDetail.getOrderId()) // giữ nguyên orderId cũ
                .productId(request.getProductId())
                .numberOfProducts(newNumberProduct)
                .totalMoney(newLineTotal)
                .color(request.getColor())
                .build();

        int rows = orderDetailMapper.updateById(orderDetail1);
        log.info("rows {}", rows);

        // 6. Trả về response mới
        return orderDetailMapper.getById(id);
    }


    @Override
    public int deleteDetailById(Integer id) {
        // kiem tra order detail
        OrderDetailResponse orderDetail = orderDetailMapper.getById(id);

        BigDecimal totalMoney = orderDetail.getTotalMoney();
        int numberProduct = orderDetail.getNumberOfProducts();

        ApiResponse<String> r2 = orderClient.decreaseTotal(orderDetail.getOrderId(), totalMoney);
        String r = r2.getData();
        log.info("message {}", r);

        ApiResponse<String> res = productClient.increaseStockProduct(orderDetail.getProductId(), numberProduct);
        String result = res.getData();
        log.info("message {}", result);

        int rows = orderDetailMapper.deleteById(id);
        
        return rows;
    }

    @Override
    public List<TopProductStatResponse> getTopProduct() {   
        return orderDetailMapper.getTopProduct();
    }

    @Override
    public void deleteAllOrderDetail(Integer orderId) {
        // kiem tra order
        ApiResponse<OrderResponse> response = orderClient.getOrderById(orderId);
        OrderResponse result = response.getData();

        List<OrderDetailResponse> detail = orderDetailMapper.findDetailResponses(orderId);

        for(OrderDetailResponse details : detail){
            ApiResponse<String> res = productClient.increaseStockProduct(details.getOrderId(), details.getNumberOfProducts());
        }

        orderDetailMapper.deleteByOrder(orderId);

        ApiResponse<Integer> del = orderClient.deleteOrder(orderId);
    }

}
