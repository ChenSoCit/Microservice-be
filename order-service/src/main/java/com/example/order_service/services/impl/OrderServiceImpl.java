package com.example.order_service.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order_service.commons.OrderStatus;
import com.example.order_service.dtos.request.OrderRequest;
import com.example.order_service.dtos.request.OrderStatisRequestUp;
import com.example.order_service.dtos.request.OrderStatisticsRequest;
import com.example.order_service.dtos.response.CntOrderResponse;
import com.example.order_service.dtos.response.DailyStatsResponse;
import com.example.order_service.dtos.response.OrderDetailResponse;
import com.example.order_service.dtos.response.OrderItemResponse;
import com.example.order_service.dtos.response.OrderStatisticsResponse;
import com.example.order_service.dtos.response.WeeklyStatsResponse;
import com.example.order_service.dtos.response.WeeklyUpdateResponse;
import com.example.order_service.exceptions.DatabaseException;
import com.example.order_service.exceptions.EmptyOrderItemsException;
import com.example.order_service.exceptions.InvalidOrderStatusException;
import com.example.order_service.exceptions.OrderNotFoundException;
import com.example.order_service.mappers.OrderDetailMapper;
import com.example.order_service.mappers.OrderMapper;
import com.example.order_service.models.Order;
import com.example.order_service.models.OrderDetail;
import com.example.order_service.services.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public OrderDetailResponse createOrder(OrderRequest request) {
        log.info("Request to create Order: {}", request);
        
        // Validate request
        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.error("Order items cannot be empty");
            throw new EmptyOrderItemsException();
        }
        
        
        // Tính tổng tiền từ các items
        BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Tạo Order
        Order order = Order.builder()
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .shippingAddress(request.getShippingAddress())
                .orderDate(LocalDateTime.now())
                .note(request.getNote())
                .build();
        
        int orderCreated = orderMapper.createOrder(order);
        if (orderCreated <= 0) {
            log.error("Failed to create order");
            throw new RuntimeException("Failed to create order");
        }
        
        log.info("Order created with id: {}", order.getId());
        
        // Tạo Order Details cho từng sản phẩm
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (var item : request.getItems()) {
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            
            OrderDetail orderDetail = OrderDetail.builder()
                    .orderId(order.getId())
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .subtotal(subtotal)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            int detailCreated = orderDetailMapper.createOrderDetail(orderDetail);
            if (detailCreated <= 0) {
                log.error("Failed to create order detail for product {}", item.getProductId());
                throw new RuntimeException("Failed to create order detail");
            }


            
            log.info("Order detail created with id: {}", orderDetail.getId());
            
            // Tạo response cho item
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setId(Math.toIntExact(orderDetail.getId()));
            itemResponse.setOrderId(Math.toIntExact(orderDetail.getOrderId()));
            itemResponse.setProductId(item.getProductId());
            itemResponse.setPrice(item.getPrice());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setSubTotal(subtotal);
            
            orderItemResponses.add(itemResponse);
        }
        
        // Tạo response
        OrderDetailResponse response = OrderDetailResponse.builder()
                .id(Math.toIntExact(order.getId()))
                .userId(Math.toIntExact(order.getUserId()))
                .fullName(order.getFullName())
                .phone(order.getPhone())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .orderItems(orderItemResponses)
                .build();
        
        log.info("Order created successfully with {} items, total: {}", 
                orderItemResponses.size(), totalAmount);
        
        return response;
    }

    @Override
    @Transactional
    public OrderDetailResponse getOrderById(int id) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) {
            log.error("Order with id {} not found", id);
            throw new OrderNotFoundException(id);
        }

        // Lấy danh sách order items
        List<OrderItemResponse> orderItems = orderDetailMapper.getOrderItemsByOrderId(id);

        return OrderDetailResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .fullName(order.getFullName())
                .phone(order.getPhone())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .orderItems(orderItems)
                .build();
    }

    @Override
    @Transactional
    public List<OrderDetailResponse> getOrderByUserId(int userId) {
        List<Order> orders = orderMapper.getAllOrderByUser(userId);
        if (orders == null || orders.isEmpty()) {
            log.error("No orders found for user id {}", userId);
            throw new OrderNotFoundException("No orders found for user id: " + userId);
        }

        List<OrderDetailResponse> orderResponses = orders.stream().map(order -> {
            // Lấy danh sách order items cho mỗi order
            List<OrderItemResponse> orderItems = orderDetailMapper.getOrderItemsByOrderId(order.getId());

            return OrderDetailResponse.builder()
                    .id(order.getId())
                    .userId(order.getUserId())
                    .fullName(order.getFullName())
                    .phone(order.getPhone())
                    .shippingAddress(order.getShippingAddress())
                    .paymentMethod(order.getPaymentMethod())
                    .orderDate(order.getOrderDate())
                    .status(order.getStatus())
                    .totalAmount(order.getTotalAmount())
                    .note(order.getNote())
                    .orderItems(orderItems)
                    .build();
        })
        .collect(Collectors.toList());
        
        return orderResponses;
    }

    @Override
    @Transactional
    public CntOrderResponse statisOrder(int userId) {
        CntOrderResponse response = orderMapper.statisOrder(userId);

        // Nếu user chưa có order nào
        if (response == null || response.getTotalOrder() == null) {
        log.info("User {} has no orders yet", userId);
        return CntOrderResponse.builder()
                .totalOrder(0)
                .statusPending(0)
                .statusDelivering(0)
                .statusCompleted(0)
                .totalMoney(BigDecimal.ZERO)
                .build();
            }

        return response;
    }

    @Override
    public int deleteUser(Integer id) {
        log.info("Request to delete Orders of User with id: {}", id);
        Order order = orderMapper.getOrderById(id);

        if(order != null && !"PENDING".equals(order.getStatus().toString())){
            log.warn("Order has been sent, cannot cancel");
            throw new InvalidOrderStatusException("Order has been sent, cannot cancel");
        }

        // Xóa orders và order details (cascade)
        int rows = orderMapper.deleteUser(id);
        log.info("Deleted {} orders of User with id: {}", rows, id);
        return rows;
    }

    @Override
    public int deleteOrder(Integer id){
        return orderMapper.deleteOrder(id);
    }

    @Override
    public String increaseTotalMoney(Integer orderId, BigDecimal totalMoney) {
        orderMapper.increaseTotalMoney(orderId, totalMoney);
        return "Success";
    }

    @Override
    public String decreaseTotalMoney(Integer orderId, BigDecimal totalMoney) {
       orderMapper.decreaseTotalMoney(orderId, totalMoney);
       return "Success";
    }

    @Override
    public Order updateOrder(Integer id, OrderRequest req) {
        Order existingOrder = orderMapper.getOrderById(id);
        if(existingOrder == null){
            log.error("Order not found with id: {}", id);
            throw new OrderNotFoundException(id);
        }

        // Tính lại tổng tiền từ items
        BigDecimal totalAmount = req.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order update = Order.builder()
            .id((id))
            .userId(req.getUserId())
            .fullName(req.getFullName())
            .shippingAddress(req.getShippingAddress())
            .phone(req.getPhone())
            .orderDate(existingOrder.getOrderDate())
            .status(existingOrder.getStatus())
            .totalAmount(totalAmount)
            .paymentMethod(req.getPaymentMethod())
            .note(req.getNote())
        .build();

        int rows = orderMapper.updateOrder(update);
        log.info("Updated {} rows", rows);

        // Trả về order đã cập nhật
        return orderMapper.getOrderById(id);
    }

    @Override
    @Transactional
    public Order updateStatus(Integer id, String status) {
        Order order = orderMapper.getOrderById(id);
        if(order == null){
            log.error("Order not found with id: {}", id);
            throw new OrderNotFoundException(id);
        }
        if (status == null || status.isBlank()) {
            throw new InvalidOrderStatusException("Status cannot be null or empty");
        }

        // Chuyển in hoa
        String st = status.toUpperCase();

        // Kiểm tra giá trị hợp lệ
        if (!st.equals("PENDING") &&
                !st.equals("SHIPPED") &&
                !st.equals("DELIVERED") &&
                !st.equals("CANCELLED")) {
            throw new InvalidOrderStatusException("Invalid status: " + status);
        }

        // Cập nhật
        int rows = orderMapper.updateStatus(id, st);
        log.info("rows affected = {}", rows);

        // Lấy lại order đã cập nhật
        return orderMapper.getOrderById(id);
    }

    @Override
    public OrderDetailResponse cancelOrder(int orderId, String reason) {
        log.info("request to cancel order:{} with reason:{}", orderId, reason);
        // kt order ton tai
        Order order = orderMapper.getOrderById(orderId);
        if(order == null){
            log.error("Order not found with id: {}", orderId);
            throw new OrderNotFoundException(orderId);
        }

        // Kiểm tra trạng thái đơn hàng
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new InvalidOrderStatusException("Cannot cancel order with status: " + order.getStatus());
        }

        // Cập nhật trạng thái đơn hàng
        int rows = orderMapper.updateStatus(orderId, OrderStatus.CANCELLED.name());
        log.info("rows affected = {}", rows);

        // Trả về thông tin đơn hàng đã hủy
        return getOrderById(orderId);
    }

    @Override
    public OrderStatisticsResponse getWeeklyStatics(OrderStatisticsRequest request) {
        // Query dữ liệu
        List<Map<String, Object>> dailyRawDate;
        try {
            dailyRawDate = orderMapper.getDailyStatistics(request.getStartDate(), request.getEndDate());
            log.info("Successfully fetched daily statistics from {} to {}", request.getStartDate(), request.getEndDate());
        } catch (Exception e) {
            log.error("Error fetching daily statistics from DB: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to fetch daily statistics from database", e);
        }

        List<DailyStatsResponse> detail = new ArrayList<>();
        // Khởi tạo tổng tiền tuần
        BigDecimal totalWeek = BigDecimal.ZERO;

        for(Map<String, Object> map : dailyRawDate){
            LocalDate date = LocalDate.parse(map.get("date").toString());
            Integer orderCount = ((Number) map.get("order_count")).intValue();
            BigDecimal totalAmount = new BigDecimal(map.get("total_amount").toString());
            totalWeek = totalWeek.add(totalAmount);

            detail.add(DailyStatsResponse.builder()
                    .date(date)
                    .orderCount(orderCount)
                    .totalAmount(totalAmount)
                    .build());
        }

        // Tính % tổng tiền từng ngày
        for(DailyStatsResponse d : detail){
            if(totalWeek.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal percent = d.getTotalAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalWeek, 2, RoundingMode.HALF_UP);
                d.setPercentOfWeek(percent.doubleValue());
            }else {
                d.setPercentOfWeek(0.0);
            }
        }

        return OrderStatisticsResponse.builder()
                .totalAmount(totalWeek)
                .details(detail)
                .build();
    }

    @Override
    public OrderStatisticsResponse getUpdatedStatics(OrderStatisRequestUp request) {
        String type = request.getType();
        Integer month = request.getMonth();
        Integer week = request.getWeek();
        int year = LocalDate.now().getYear();

        //  Xác định ngày đầu và cuối tháng
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        //  Type=W: Thống kê theo NGÀY trong tuần cụ thể (DailyStatsResponse)
        if ("W".equalsIgnoreCase(type) && week != 0) {
            log.info("Getting daily statistics for week {} of month {}", week, month);

            // Tính ngày bắt đầu và kết thúc của tuần đó
            int daysInMonth = lastDay.getDayOfMonth();
            int startDay = (week - 1) * 7 + 1;
            int endDay = Math.min(startDay + 6, daysInMonth);

            LocalDate weekStartDate = LocalDate.of(year, month, startDay);
            LocalDate weekEndDate = LocalDate.of(year, month, endDay);

            // Gọi method getWeek để lấy thống kê theo ngày
            OrderStatisticsRequest weekRequest = OrderStatisticsRequest.builder()
                    .startDate(weekStartDate)
                    .endDate(weekEndDate)
                    .type("W")
                    .build();
            
            OrderStatisticsResponse response = getWeek(weekRequest);
            response.setType("WEEK " + week + " - MONTH " + month);
            
            return response;
        }

        //  Type=M: Thống kê theo TUẦN trong tháng (WeeklyUpdateResponse)
        if ("M".equalsIgnoreCase(type) && month != null) {
            log.info("Getting weekly statistics for month {}", month);
            
            // Query toàn bộ dữ liệu của tháng
            List<Map<String, Object>> dailyRawData;
            try {
                dailyRawData = orderMapper.getDailyStatistics(firstDay, lastDay);
                log.info("Successfully fetched daily statistics for month {}", month);
            } catch (Exception e) {
                log.error("Error fetching daily statistics: {}", e.getMessage(), e);
                throw new DatabaseException("Failed to fetch daily statistics", e);
            }

            // Map lưu dữ liệu thật db -> response theo ngày
            Map<LocalDate, WeeklyUpdateResponse> dailyStats = new HashMap<>();
            BigDecimal totalMonth = BigDecimal.ZERO;

            for (Map<String, Object> row : dailyRawData) {
                LocalDate date = LocalDate.parse(row.get("date").toString());
                int orderCount = ((Number) row.get("order_count")).intValue();
                BigDecimal totalAmount = new BigDecimal(row.get("total_amount").toString());
                totalMonth = totalMonth.add(totalAmount);

                dailyStats.put(date, WeeklyUpdateResponse.builder()
                        .orderCount(orderCount)
                        .totalAmount(totalAmount)
                        .build());
            }

            //  Duyệt từng tuần trong tháng (mỗi tuần 7 ngày)
            List<WeeklyUpdateResponse> weeklyDetails = new ArrayList<>();
            LocalDate currentStart = firstDay;
        

            while (!currentStart.isAfter(lastDay)) {
                LocalDate currentEnd = currentStart.plusDays(6);
                if (currentEnd.isAfter(lastDay)) currentEnd = lastDay;

                int weeklyOrderCount = 0;
                BigDecimal weeklyTotalAmount = BigDecimal.ZERO;

                // Duyệt từng ngày trong tuần
                LocalDate day = currentStart;
                while (!day.isAfter(currentEnd)) {
                    WeeklyUpdateResponse dayStat = dailyStats.get(day);
                    if (dayStat != null) {
                        weeklyOrderCount += dayStat.getOrderCount();
                        weeklyTotalAmount = weeklyTotalAmount.add(dayStat.getTotalAmount());
                    }
                    day = day.plusDays(1);
                }

                WeeklyUpdateResponse weekStat = WeeklyUpdateResponse.builder()
                        .startDate(currentStart)
                        .endDate(currentEnd)
                        .orderCount(weeklyOrderCount)
                        .totalAmount(weeklyTotalAmount)
                        .percentOfMonth(0.0) // Sẽ tính sau
                        .build();

                weeklyDetails.add(weekStat);
                currentStart = currentEnd.plusDays(1);
            }

            Integer totalOrder = 0;
            //  Tính phần trăm của từng tuần trong tháng
            for (WeeklyUpdateResponse w : weeklyDetails) {
                if (totalMonth.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percent = w.getTotalAmount()
                            .multiply(BigDecimal.valueOf(100))
                            .divide(totalMonth, 2, RoundingMode.HALF_UP);
                    totalOrder += w.getOrderCount();
                    w.setPercentOfMonth(percent.doubleValue());
                } else {
                    w.setPercentOfMonth(0.0);
                }
            }

            return OrderStatisticsResponse.builder()
                    .type("MONTH " + month)
                    .totalAmount(totalMonth)
                    .totalOrders(totalOrder)
                    .details(weeklyDetails)
                    .build();
        }

        // Default: Trả về empty response nếu thiếu parameters
        return OrderStatisticsResponse.builder()
                .type("INVALID")
                .totalAmount(BigDecimal.ZERO)
                .totalOrders(0)
                .details(new ArrayList<>())
                .build();
    }


    @Override
    @Transactional
    public OrderStatisticsResponse getWeek(OrderStatisticsRequest request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        //  Query dữ liệu thật từ DB
        List<Map<String, Object>> dailyRawData;
        try {
            dailyRawData = orderMapper.getDailyStatistics(startDate, endDate);
            log.info("Successfully fetched daily statistics for week from {} to {}", startDate, endDate);
        } catch (Exception e) {
            log.error("Error fetching daily statistics from database: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to fetch daily statistics from database", e);
        }

        // Map dữ liệu thật ra HashMap để tra nhanh theo ngày
        Map<LocalDate, Map<String, Object>> actualData = new HashMap<>();
        for (Map<String, Object> row : dailyRawData) {
            LocalDate date = LocalDate.parse(row.get("date").toString());
            actualData.put(date, row);
        }

        //  Khởi tạo danh sách kết quả đủ ngày
        List<DailyStatsResponse> details = new ArrayList<>();
        BigDecimal totalWeek = BigDecimal.ZERO;

        // Duyệt từng ngày trong khoảng thời gian
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            Map<String, Object> row = actualData.get(current);

            Integer orderCount = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;

            if (row != null) {
                orderCount = ((Number) row.get("order_count")).intValue();
                totalAmount = new BigDecimal(row.get("total_amount").toString());
            }

            totalWeek = totalWeek.add(totalAmount);

            details.add(DailyStatsResponse.builder()
                    .date(current)
                    .orderCount(orderCount)
                    .totalAmount(totalAmount)
                    .percentOfWeek(0.0)
                    .build());

            current = current.plusDays(1);
        }
        Integer totalOrder = 0;
        // Tính phần trăm tổng tiền từng ngày
        for (DailyStatsResponse d : details) {
            if (totalWeek.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percent = d.getTotalAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalWeek, 2, RoundingMode.HALF_UP);
                d.setPercentOfWeek(percent.doubleValue());
                totalOrder += d.getOrderCount();
            } else {
                d.setPercentOfWeek(0.0);
            }
        }

        // Trả response
        return OrderStatisticsResponse.builder()
                .type("WEEK " + startDate + " → " + endDate)
                .totalAmount(totalWeek)
                .totalOrders(totalOrder)
                .details(details)
                .build();
    }


    @Override
    @Transactional
    public OrderStatisticsResponse getMonth(OrderStatisticsRequest request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        // Lấy dữ liệu từng ngày từ DB
        List<Map<String, Object>> dailyRawData;
        try {
            dailyRawData = orderMapper.getDailyStatistics(startDate, endDate);
            log.info("Successfully fetched daily statistics for month from {} to {}", startDate, endDate);
        } catch (Exception e) {
            log.error("Error fetching daily statistics from database: {}", e.getMessage(), e);
            throw new DatabaseException("Failed to fetch daily statistics from database", e);
        }

        //  Tạo map để nhóm theo tuần trong tháng
        Map<Integer, WeeklyStatsResponse> weekMap = new HashMap<>();
        BigDecimal totalMonthAmount = BigDecimal.ZERO;

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (Map<String, Object> row : dailyRawData) {
            LocalDate date = LocalDate.parse(row.get("date").toString());
            Integer orderCount = ((Number) row.get("order_count")).intValue();
            BigDecimal totalAmount = new BigDecimal(row.get("total_amount").toString());

            // Tính tuần trong tháng
            int weekOfMonth = date.get(weekFields.weekOfMonth());

            // Gom nhóm theo tuần
            WeeklyStatsResponse stat = weekMap.getOrDefault(
                    weekOfMonth,
                    WeeklyStatsResponse.builder()
                            .weekNumber(weekOfMonth)
                            .orderCount(0)
                            .totalAmount(BigDecimal.ZERO)
                            .percentOfMonth(0.0)
                            .build()
            );

            // Cộng dồn dữ liệu
            stat.setOrderCount(stat.getOrderCount() + orderCount);
            stat.setTotalAmount(stat.getTotalAmount().add(totalAmount));

            weekMap.put(weekOfMonth, stat);

            totalMonthAmount = totalMonthAmount.add(totalAmount);
        }

        //  Xác định tổng số tuần trong tháng
        int totalWeeks = endDate.get(weekFields.weekOfMonth());

        //  Đảm bảo tuần trống vẫn có
        List<WeeklyStatsResponse> weeklyList = new ArrayList<>();
        for (int i = 1; i <= totalWeeks; i++) {
            WeeklyStatsResponse stat = weekMap.getOrDefault(
                    i,
                    WeeklyStatsResponse.builder()
                            .weekNumber(i)
                            .orderCount(0)
                            .totalAmount(BigDecimal.ZERO)
                            .percentOfMonth(0.0)
                            .build()
            );
            weeklyList.add(stat);
        }

        // Tính % doanh thu từng tuần
        for (WeeklyStatsResponse w : weeklyList) {
            if (totalMonthAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percent = w.getTotalAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalMonthAmount, 2, RoundingMode.HALF_UP);
                w.setPercentOfMonth(percent.doubleValue());
            } else {
                w.setPercentOfMonth(0.0);
            }
        }

        //  Trả kết quả
        return OrderStatisticsResponse.builder()
                .type("MONTH " + startDate.getMonthValue())
                .totalAmount(totalMonthAmount)
                .details(weeklyList)
                .build();
    }

}
