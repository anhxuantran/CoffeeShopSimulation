package cmsc433_p2;

/*Name: Anh Tran
 * UID: 112126346
 * */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Cooks are simulation actors that have at least one field, a name. When
 * running, a cook attempts to retrieve outstanding orders placed by Eaters and
 * process them.
 */
public class Cook implements Runnable {
	private final String name;
	/**
	 * You can feel free modify this constructor. It must take at least the
	 * name, but may take other parameters if you would find adding them useful.
	 * 
	 * @param: the name of the cook
	 */

	LinkedList<FoodOrder> orders;
	static HashMap<Food, Machine> machines;

	public Cook(String name, LinkedList<FoodOrder> orderList,
			HashMap<Food, Machine> machinesIn) {
		this.name = name;
		orders = orderList;
		machines = machinesIn;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows. The cook tries to retrieve orders placed
	 * by Customers. For each order, a List<Food>, the cook submits each Food
	 * item in the List to an appropriate Machine, by calling makeFood(). Once
	 * all machines have produced the desired Food, the order is complete, and
	 * the Customer is notified. The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some other thread
	 * calls the interrupt() method on it, which could raise
	 * InterruptedException if the cook is blocking), then it terminates.
	 */
	// private ReentrantLock lock = new ReentrantLock();

	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while (true) {

				// YOUR CODE GOES HERE...

				// wait for a customer to notify

				LinkedList<Food> newOrder = new LinkedList<Food>();
				Customer temp = null;
				int orderNumber = 0;

				synchronized (orders) {
					/* waits for a customer to notify the cook */
					if (orders.isEmpty()) {
						orders.wait();
					}/*
					 * if a cook was woken up and there is nothing in the list,
					 * then just continue on without doing anything.
					 */
					if (!orders.isEmpty()) {
						orderNumber = orders.getFirst().getOrderNum();
						newOrder = orders.getFirst().getListOfFood();
						temp = orders.getFirst().getCustomer();
						Simulation.logEvent(SimulationEvent.cookReceivedOrder(
								this, newOrder, orderNumber));
						orders.removeFirst();
					}
					orders.notify();
				}

				ArrayList<Future<Food>> future = new ArrayList<Future<Food>>();

				try {
					for (int i = 0; i < newOrder.size(); i++) {
						future.add(machines.get(newOrder.get(i)).makeFood());
						Simulation.logEvent(SimulationEvent.cookStartedFood(
								this, newOrder.get(i), orderNumber));
					}

					for (int i = 0; i < future.size(); i++) {
						Simulation.logEvent(SimulationEvent.cookFinishedFood(
								this, future.get(i).get(), orderNumber));
					}
					/*
					 * This is so the cook does not print out
					 * "Cook completed order 0"
					 */
					if (orderNumber != 0)
						Simulation.logEvent(SimulationEvent.cookCompletedOrder(
								this, orderNumber));
					/*
					 * synchronized (newOrder) { newOrder.notify(); }
					 */

					/*
					 * I had to use this lock instead of notifying the
					 * customer's order. Notifying the customer's order to tell
					 * them that the order is complete gave results where the
					 * customer received the order before the cook had finished
					 * it.
					 */

					synchronized (Simulation.custMap) {
						Simulation.custMap.put(temp, true);
					}

				} catch (ExecutionException e) {

				}

				// future.clear();
			}
		} catch (InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.

			Simulation.logEvent(SimulationEvent.cookEnding(this));
			Thread.currentThread().interrupt();
		}
	}
}