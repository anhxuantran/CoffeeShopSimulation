package cmsc433_p2;

/*Name: Anh Tran
 * UID: 112126346
 * */

import java.util.LinkedList;
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
	private static int numTables;

	// static CountDownLatch starterDelay, joinerLatch;
	private static Semaphore tableSpace = null;
	/**
	 * You can feel free modify this constructor. It must take at least the name
	 * and order but may take other parameters if you would find adding them
	 * useful.
	 */


	
	LinkedList<FoodOrder> orders = null;

	public Customer(String name, LinkedList<Food> order, int numTablesIn,
			LinkedList<FoodOrder> orderList) {

		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;

		if (tableSpace == null) {
			numTables = numTablesIn;
			tableSpace = new Semaphore(numTables);
		}
		
		Simulation.custMap.put(this, false);
		orders = orderList;
		

	}

	public String toString() {
		return name;
	}

	public int getOrderNum() {
		return this.orderNum;
	}
	
	public LinkedList<Food> getOrder(){
		return this.order;
	}

	/**
	 * This method defines what an Customer does: The customer attempts to enter
	 * the restaurant (only successful when the restaurant has a free table),
	 * place its order, and then leave the restaurant when the order is
	 * complete.
	 */
//	private ReentrantLock lock = new ReentrantLock();

	public void run() {

		Simulation.logEvent(SimulationEvent.customerStarting(this));
		try {
			/*
			 * Customer attempts to enter the coffee shop here The Semaphore
			 * blocks them when it reaches 0.
			 */
			tableSpace.acquire();
			/*i do not use reentrantlocks between classes anymore because i need 
			 * some way for unique synchronization. 
			 * */
			
			
			/* The simulation logs the event once they have entered in */
			Simulation
					.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
			
			
			synchronized (orders) {
				orders.add(new FoodOrder(this.orderNum, this.order, this));
				Simulation.logEvent(SimulationEvent.customerPlacedOrder(this,
						this.order, this.orderNum));
				orders.notifyAll();
			}
			
			
			/*This customer is notified that cook has completed the 
			 * order. It breaks out when the cook has finally placed 
			 * This notification is unique however.
			 * */
			while(Simulation.custMap.get(this) == false){
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