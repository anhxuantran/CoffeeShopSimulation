package cmsc433_p3;

import java.util.LinkedList;

public class FoodOrder {

	private final int orderNum;
	private final LinkedList<Food> newOrder;
	private boolean done;
	private final Customer customer;

	public FoodOrder(int orderNum, LinkedList<Food> order, Customer customerIn) {
		this.newOrder = order;
		this.orderNum = orderNum;
		this.customer = customerIn;
	}

	public LinkedList<Food> getListOfFood() {
		return this.newOrder;
	}

	public int getOrderNum() {
		return this.orderNum;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public boolean isDone() {
		return this.done;
	}
	
	public String toString() {
		return "Order Number " + this.orderNum + "\n" + this.customer.toString()
				+ "\n" + this.done;
	}
}
