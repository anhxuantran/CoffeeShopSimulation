package cmsc433_p3;

/*Name: Anh Tran
 * UID: 112126346
 * */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
	 * 
	 * 
	 */
	private HashMap<Food, Machine> machines;

	public Cook(String name) {
		this.name = name;
		machines = Simulation.machines;
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

	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while (true) {

				// YOUR CODE GOES HERE...
				
				FoodOrder curr = Simulation.orders.take();

				Customer customer = curr.getCustomer();
				LinkedList<Food> o = curr.getListOfFood();
				int orderNum = curr.getOrderNum();
				Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, o,
						orderNum));
				
				
				ArrayList<Future<Food>> future = new ArrayList<Future<Food>>();

				for (int i = 0; i < o.size(); i++) {
					Simulation.logEvent(SimulationEvent.cookStartedFood(this,
							o.get(i), orderNum));
					future.add(machines.get(o.get(i)).makeFood());
				}

				try {
					for (int i = 0; i < future.size(); i++) {
						Food result = future.get(i).get();
						Simulation.logEvent(SimulationEvent.cookFinishedFood(
								this, result, orderNum));
					}
				} catch (ExecutionException e) {
					
				}

				if (customer != null) {
					Simulation.logEvent(SimulationEvent.cookCompletedOrder(
							this, orderNum));
					Simulation.custMap.put(customer, true);
				}
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