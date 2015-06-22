package cmsc433_p3;

/*Name: Anh Tran
 * UID: 112126346
 * */

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Customers are simulation actors that have two fields: a name, and a list of
 * Food items that constitute the Customer's order. When running, an customer
 * attempts to enter the restaurant (only successful if the restaurant has a
 * free table), place its order, and then leave the restaurant when the order is
 * complete.
 */
public class Customer implements Runnable {
	// JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final LinkedList<Food> order;
	private final int orderNum;

	private static int runningCounter = 0;

	// static CountDownLatch starterDelay, joinerLatch;
	private static Semaphore tableSpace;

	/**
	 * You can feel free modify this constructor. It must take at least the name
	 * and order but may take other parameters if you would find adding them
	 * useful.
	 */

	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = (LinkedList<Food>) order;
		this.orderNum = ++runningCounter;

		Simulation.custMap.put(this, false);

		tableSpace = new Semaphore(Simulation.numberTables);
	}

	public String toString() {
		return name;
	}

	public int getOrderNum() {
		return this.orderNum;
	}

	public List<Food> getOrder() {
		return this.order;
	}

	/**
	 * This method defines what an Customer does: The customer attempts to enter
	 * the restaurant (only successful when the restaurant has a free table),
	 * place its order, and then leave the restaurant when the order is
	 * complete.
	 */

	public void run() {

		Simulation.logEvent(SimulationEvent.customerStarting(this));
		try {

			tableSpace.acquire();

			Simulation
					.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));

			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this,
					this.order, this.orderNum));

			Simulation.orders
					.put(new FoodOrder(this.orderNum, this.order, this));

			
			while (Simulation.custMap.get(this) == false) {
				Thread.sleep(15);
			}

			Simulation.logEvent(SimulationEvent.customerReceivedOrder(this,
					this.order, this.orderNum));

			Simulation
					.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));

			tableSpace.release();

		} catch (InterruptedException e) {

		}
	}
}