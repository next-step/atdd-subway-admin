package nextstep.subway.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Order {

    private int order;

    protected Order() {
    }

    public Order(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isPreOrder(Order target) {
        return this.order < target.order;
    }

    public boolean isEqualsOrPreOrder(Order target) {
        return this.order <= target.order;
    }

    public Order plusOne() {
        return new Order(order + 1);
    }

    public boolean isEqualsOrLastOrder(Order target) {
        return this.order >= target.order;
    }
}