package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderRepostiory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepostiory orderRepostiory;

    @Test
    public void 상품주문() throws Exception {
        Member member = createMember();

        Book book = createBook("시골 jpa", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order getOrder = orderRepostiory.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다", 10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고수량이 줄어야 한다.", 8, book.getStockQuantity());

    }



    @Test
    public void 주문취소() throws Exception {

        Member member = createMember();
        Item item = createBook("시골 jpa", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        orderService.cancelOrder(orderId);

        Order getOrder = orderRepostiory.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL이다다", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());

   }

    @Test(expected = NotEnoughStockException.class)//수량초과하면 낫이너프 메서드 실행되야됨
    public void 상품주문_재고수량초과() throws Exception {

        Member member = createMember();
        Item item = createBook("시골 jpa", 10000, 10);
        int orderCount = 11;

        orderService.order(member.getId(), item.getId(), orderCount);

        fail("재고수량 부족 예외 발생해야 한다");


    }
    //밑 내용 그대로 일일이 대입했는데 alt+enter extract method 하면 밑처럼 만들수있다
   private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);//ctrl + alt + p 하면 book.setName("살 jpa") 이거를 book.setName(name)바꿔줌
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
    //이거 남아있으면 되돌리기 실패


}