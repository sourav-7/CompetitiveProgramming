package karatCodePractice.orderDeliverySystem;

/*
1) Order Delivery System
We are building a program to manage a food delivery platform. The platform has multiple restaurants,
customers place orders, and those orders move through statuses:
PLACED PREPARING OUT_FOR_DELIVERY DELIVERED, or CANCELED.
Definitions:
* An "order" has: orderld, restaurantld, customerld, orderValue, distanceKm, status.
* "OrderManager" manages orders and provides order statistics.
To begin with, we present you with two tasks:
1-1) Read through and understand the code below. Feel free to run it.
1-2) The test for OrderManager is not passing due to a bug in the code.
Make the necessary changes to OrderManager to fix the bug.

2) We are updating our system to include delivery session information for orders.
We introduce a Delivery class:
    - Each Delivery has a unique deliveryld
    - startMinute and end Minute represent minutes from the start of the day (same day)
    - duration endMinute - startMinute
Add two functions to OrderManager:
2.1) addDelivery(orderld, delivery): Associate a delivery with an order. If the order does not exist, ignore.
2.2) getAverageDelivery Time By Restaurant():
Compute the average delivery duration (minutes) per restaurantid. Count ALL deliveries for that restaurant (across orders).
Return: Map<Integer, Double> restaurantid > average Duration.
To assist you in testing these new functions, we have provided the testGetAverageDelivery Time By Restaurant and assertAlmost' functions.
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum OrderStatus {
    PLACED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED

}

class Delivery {
    int deliveryId;
    int startMinute;
    int endMinute;

    Delivery(int deliveryId, int startMinute, int endMinute) {
        this.deliveryId = deliveryId;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }

    int getDurationMinutes() {
        return endMinute - startMinute;
    }
}

class Order {
    int orderId;
    int restaurantId;
    int customerId;
    double orderValue;
    double distanceKm;
    OrderStatus status;
    // Store deliveries for this order
    List<Delivery> deliveries;

    Order(int orderId, int restaurantId, int customerId, double orderValue, double distanceKm, OrderStatus status) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.orderValue = orderValue;
        this.distanceKm = distanceKm;
        this.status = status;
        deliveries = new ArrayList<>();
    }
}

class OrderStats {
    int totalOrders;
    int activeOrders;
    int closedOrders;

    OrderStats(int totalOrders, int activeOrders, int closedOrders) {
        this.totalOrders = totalOrders;
        this.activeOrders = activeOrders;
        this.closedOrders = closedOrders;
    }


}

class OrderManager {
    List<Order> orders;

    OrderManager() {
        orders = new ArrayList<>();
    }

    // Add order
    void addOrder(Order order) {
        orders.add(order);
    }

    // Update order status
    void updateOrderStatus(int orderId, OrderStatus newStatus) {
        for (Order o : orders) {
            if (o.orderId == orderId) {
                o.status = newStatus;
                return;
            }
        }
    }

    // Get statistics
    OrderStats getOrderStatistics() {
        int total = orders.size();
        int active = 0;
        for (Order o : orders) {
            if (o.status == OrderStatus.PLACED
                    || o.status == OrderStatus.PREPARING
                    || o.status == OrderStatus.OUT_FOR_DELIVERY) {
                active++;
            }
        }
        int closed = 0;
        for (Order o : orders) {
            if (o.status == OrderStatus.DELIVERED
                    || o.status == OrderStatus.CANCELLED) {
                closed++;
            }
        }
        return new OrderStats(total, active, closed);
    }

    void addDelivery(int orderId, Delivery delivery) {
        for (Order o : orders) {
            if (o.orderId == orderId) {
                o.deliveries.add(delivery);
                return;
            }
        }
    }

    Map<Integer, Double> getAverageDeliveryTimeByRestaurant() {
        Map<Integer, Double> avgDeliveryMap = new HashMap<>();
        Map<Integer, Integer> totalDurationByRestaurantMap = new HashMap<>();
        Map<Integer, Integer> countByRestaurantMap = new HashMap<>();

        // Key restaurantId, average
        for (Order o : orders) {
            for (Delivery d : o.deliveries) {
                totalDurationByRestaurantMap.merge(o.restaurantId, d.getDurationMinutes(), Integer::sum);
                countByRestaurantMap.merge(o.restaurantId, 1, Integer::sum);
            }
        }
        for (Map.Entry<Integer, Integer> entry : totalDurationByRestaurantMap.entrySet()) {
            int restaurantId = entry.getKey();
            int totalDuration = entry.getValue();
            double averageDuration = (double) totalDuration / countByRestaurantMap.get(restaurantId);
            avgDeliveryMap.put(restaurantId, averageDuration);
        }
        return avgDeliveryMap;
    }
}

public class OrderDeliverySolution {

    public static void main(String[] args) {
        testOrderManager();
        testGetAverageDeliveryTimeByRestaurant();
        System.out.println("All tests passed.");
    }

    //CUSTOM ASSERT EQUALS
    static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new RuntimeException("Assertion Failed! Expected = " + expected + " Actual = " + actual);
        }
    }

    // CUSTOM ASSERT ALMOST
    static void assertAlmost(double expected, double actual, double eps) {
        if (Math.abs(expected - actual) > eps) {
            throw new RuntimeException("Assertion Failed! Expected = " + expected + " Actual = " + actual);
        }
    }

    // TEST 1
    public static void testOrderManager() {
        System.out.println("Running testOrderManager");
        OrderManager om = new OrderManager();
        om.addOrder(new Order(1, 10, 100, 25.0, 3.2, OrderStatus.PLACED));
        om.addOrder(new Order(2, 10, 101, 55.0, 1.4, OrderStatus.PREPARING));
        om.addOrder(new Order(3, 11, 102, 15.0, 6.0, OrderStatus.OUT_FOR_DELIVERY));
        om.addOrder(new Order(4, 11, 103, 40.0, 2.0, OrderStatus.DELIVERED));
        om.addOrder(new Order(5, 12, 104, 18.0, 4.5, OrderStatus.CANCELLED));
        OrderStats stats = om.getOrderStatistics();
        assertEquals(5, stats.totalOrders);
        assertEquals(3, stats.activeOrders);
        assertEquals(2, stats.closedOrders);
        System.out.println("test OrderManager Passed");
    }

    // TEST 2
    public static void testGetAverageDeliveryTimeByRestaurant() {
        System.out.println("Running testGetAverage DeliveryTimeByRestaurant");
        OrderManager om = new OrderManager();
        om.addOrder(new Order(1, 10, 100, 25.0, 3.2, OrderStatus.DELIVERED));
        om.addOrder(new Order(2, 10, 101, 55.0, 1.4, OrderStatus.DELIVERED));
        om.addOrder(new Order(3, 11, 102, 15.0, 6.0, OrderStatus.DELIVERED));
        // Add deliveries
        om.addDelivery(1, new Delivery(101, 10, 40)); // 30 mins
        om.addDelivery(2, new Delivery(102, 50, 80)); // 30 mins
        om.addDelivery(2, new Delivery(103, 90, 150)); // 60 mins
        om.addDelivery(3, new Delivery(104, 20, 50)); // 30 mins
        // Unknown order -> ignored
        om.addDelivery(999, new Delivery(105, 0, 10));
        Map<Integer, Double> avg = om.getAverageDeliveryTimeByRestaurant();

        // Restaurant 10:
        // durations = [30, 30, 60]
        // average = 40
        assertAlmost(40.0, avg.get(10), 0.0001);
        // Restaurant 11:
        // durations = [30]
        // average = 30
        assertAlmost(30.0, avg.get(11), 0.0001);
        System.out.println("test GetAverage DeliveryTimeByRestaurant Passed");
    }
}