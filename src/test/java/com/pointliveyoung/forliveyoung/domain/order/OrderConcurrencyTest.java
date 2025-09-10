package com.pointliveyoung.forliveyoung.domain.order;

import com.pointliveyoung.forliveyoung.domain.order.dto.request.PurchaseRequest;
import com.pointliveyoung.forliveyoung.domain.order.dto.response.PurchaseResponse;
import com.pointliveyoung.forliveyoung.domain.order.repository.OrderRepository;
import com.pointliveyoung.forliveyoung.domain.order.service.OrderService;
import com.pointliveyoung.forliveyoung.domain.point.entity.PolicyType;
import com.pointliveyoung.forliveyoung.domain.point.service.UserPointService;
import com.pointliveyoung.forliveyoung.domain.product.entity.Product;
import com.pointliveyoung.forliveyoung.domain.product.repository.ProductRepository;
import com.pointliveyoung.forliveyoung.domain.product.service.ProductService;
import com.pointliveyoung.forliveyoung.domain.user.entity.User;
import com.pointliveyoung.forliveyoung.domain.user.repository.UserRepository;
import com.pointliveyoung.forliveyoung.domain.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class OrderConcurrencyTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserPointService userPointService;



    @DisplayName("오버셀 재현 - 재고 3, 스레드 50 → 성공이 4건 이상이면 레이스 발생, (20번 테스트)")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Timeout(20)
    @RepeatedTest(20)
    void concurrent_purchase_stock1_onlyOneSucceeds() throws Exception {
        List<User> userList = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            User user = userService.getUserById(i);
            userPointService.create(user, PolicyType.NORMAL);
            userList.add(user);
        }


        Product product = productService.getById(1);
        product.changeStock(3);
        productRepository.saveAndFlush(product);

        PurchaseRequest purchaseRequest = new PurchaseRequest(product.getId(), 1, 1000);

        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        CyclicBarrier barrier = new CyclicBarrier(threads);

        List<Integer> successOrderIds = new CopyOnWriteArrayList<>();
        List<Throwable> failures = new CopyOnWriteArrayList<>();


        for (int i = 0; i < threads; i++) {
            final int idx = i;
            pool.submit(() -> {
                try {
                    start.await();

                    productService.getById(product.getId());
                    Thread.sleep(ThreadLocalRandom.current().nextInt(0, 4));
                    barrier.await();

                    User u = userList.get(idx);
                    PurchaseResponse res = orderService.purchaseProducts(u.getId(), purchaseRequest);
                    successOrderIds.add(res.orderId());
                } catch (Throwable t) {
                    failures.add(t);
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(20, TimeUnit.SECONDS), "시간 내 완료");
        pool.shutdown();


        int successes = successOrderIds.size();
        assertTrue(successes >= 4,
                "오버셀(동시 다수 성공) 재현 기대: 성공=" + successes + ", 실패=" + failures.size());
    }

    @Test
    @DisplayName("동시 취소 - 같은 상품에 대한 서로 다른 10개 주문 동시 취소 → 재고 복원 합계 정확")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Timeout(20)
    void concurrent_cancel_manyOrders_sameProduct_inventoryIsExact() throws Exception {
        List<User> userList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            User user = userService.getUserById(i);
            userPointService.create(user, PolicyType.NORMAL);
            userList.add(user);
        }

        int orders = 10;
        int expectedTotalQty = orders;

        Product product = productService.getById(1);
        product.changeStock(expectedTotalQty);
        productRepository.saveAndFlush(product);
        int initialStock = product.getStock();

        record OrderCtx(int orderId, int userId) {
        }
        List<OrderCtx> orderCtxList = new ArrayList<>();
        for (int i = 0; i < orders; i++) {
            User u = userList.get(i);
            PurchaseRequest req = new PurchaseRequest(product.getId(), 1, 1000);
            PurchaseResponse res = orderService.purchaseProducts(u.getId(), req);
            orderCtxList.add(new OrderCtx(res.orderId(), u.getId()));
        }

        assertEquals(0, productService.getById(product.getId()).getStock());

        int threads = orders;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);


        List<Integer> cancelSuccess = new CopyOnWriteArrayList<>();
        List<Throwable> cancelFailures = new CopyOnWriteArrayList<>();
        for (int i = 0; i < threads; i++) {
            final OrderCtx ctx = orderCtxList.get(i);
            pool.submit(() -> {
                try {
                    start.await();
                    Thread.sleep(ThreadLocalRandom.current().nextInt(0, 4));
                    orderService.cancel(ctx.userId(), ctx.orderId());
                    cancelSuccess.add(ctx.orderId());
                } catch (Throwable t) {
                    cancelFailures.add(t);
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        pool.shutdown();


        assertEquals(threads, cancelSuccess.size(), "취소 성공 건수");
        assertEquals(0, cancelFailures.size(), "취소 실패 없어야 함");

        Product reloaded = productService.getById(product.getId());
        assertEquals(initialStock, reloaded.getStock(), "최종 재고는 10으로 정확 복원");
    }
}


