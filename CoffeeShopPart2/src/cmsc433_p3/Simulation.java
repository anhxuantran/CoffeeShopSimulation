package cmsc433_p3;

/*Name: Anh Tran
 * UID: 112126346
 * */
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Simulation is the main class used to run the simulation. You may add any
 * fields (static or instance) or any methods you wish.
 */
public class Simulation {
	// List to track simulation events during simulation
	public static List<SimulationEvent> events;
	public static ConcurrentHashMap<Customer, Boolean> custMap = new ConcurrentHashMap<Customer, Boolean>();
	public static LinkedBlockingQueue<FoodOrder> orders;
	public static HashMap<Food, Machine> machines = new HashMap<Food, Machine>();
	public static int numberTables;

	/**
	 * Used by other classes in the simulation to log events
	 * 
	 * @param event
	 */
	public static void logEvent(SimulationEvent event) {
		events.add(event);
		System.out.println(event);
	}

	/**
	 * Function responsible for performing the simulation. Returns a List of
	 * SimulationEvent objects, constructed any way you see fit. This List will
	 * be validated by a call to Validate.validateSimulation. This method is
	 * called from Simulation.main(). We should be able to test your code by
	 * only calling runSimulation.
	 * 
	 * Parameters:
	 * 
	 * @param numCustomers
	 *            the number of customers wanting to enter the restaurant
	 * @param numCooks
	 *            the number of cooks in the simulation
	 * @param numTables
	 *            the number of tables in the restaurant (i.e. restaurant
	 *            capacity)
	 * @param machineCapacity
	 *            the capacity of all machines in the restaurant
	 * @param randomOrders
	 *            a flag say whether or not to give each customer a random order
	 * 
	 */
	public static List<SimulationEvent> runSimulation(int numCustomers,
			int numCooks, int numTables, int machineCapacity,
			boolean randomOrders) {

		// This method's signature MUST NOT CHANGE.

		// We are providing this events list object for you.
		// It is the ONLY PLACE where a concurrent collection object is
		// allowed to be used.
		events = Collections.synchronizedList(new ArrayList<SimulationEvent>());

		// Start the simulation
		logEvent(SimulationEvent.startSimulation(numCustomers, numCooks,
				numTables, machineCapacity));

		/*
		 * Added extra parameters to customers here
		 */

		// Set things up you might need

		// Start up machines
		machines.put(FoodType.burger, new Machine("Grill", FoodType.burger,
				machineCapacity));
		machines.put(FoodType.fries, new Machine("Fryer", FoodType.fries,
				machineCapacity));
		machines.put(FoodType.coffee, new Machine("CoffeeMaker2000",
				FoodType.coffee, machineCapacity));

		for (Machine i : machines.values()) {
			logEvent(SimulationEvent.machineStarting(i, i.machineFoodType,
					machineCapacity));
		}
		// Let cooks in

		Thread[] cooks = new Thread[numCooks];
		orders = new LinkedBlockingQueue<FoodOrder>(numTables);
		numberTables = numTables;
		// Build the customers.
		Thread[] customers = new Thread[numCustomers];
		LinkedList<Food> order;
		
		if (!randomOrders) {
			order = new LinkedList<Food>();
			order.add(FoodType.burger);
			order.add(FoodType.fries);
			order.add(FoodType.fries);
			order.add(FoodType.coffee);
			for (int i = 0; i < customers.length; i++) {
				customers[i] = new Thread(new Customer("Customer " + (i + 1),
						order));
			}
		} else {
			Random rnd = new Random(27);
			for (int i = 0; i < customers.length; i++) {

				int burgerCount = rnd.nextInt(3);
				int friesCount = rnd.nextInt(3);
				int coffeeCount = rnd.nextInt(3);
				order = new LinkedList<Food>();
				for (int b = 0; b < burgerCount; b++) {
					order.add(FoodType.burger);
				}
				for (int f = 0; f < friesCount; f++) {
					order.add(FoodType.fries);
				}
				for (int c = 0; c < coffeeCount; c++) {
					order.add(FoodType.coffee);
				}
				customers[i] = new Thread(new Customer("Customer " + (i + 1),
						order));
			}
		}
		

		for (int i = 0; i < numCooks; i++) {
			cooks[i] = new Thread(new Cook("Cook " + i));
		}

		for (int i = 0; i < cooks.length; i++) {
			cooks[i].start();
		}
		// Now "let the customers know the shop is open" by
		// starting them running in their own thread.
		for (int i = 0; i < customers.length; i++) {
			customers[i].start();
			// NOTE: Starting the customer does NOT mean they get to go
			// right into the shop. There has to be a table for
			// them. The Customer class' run method has many jobs
			// to do - one of theses is waiting for an available
			// table...
		}

		try {
			// Wait for customers to finish
			// -- you need to add some code here...

			for (int i = 0; i < customers.length; i++) {
				customers[i].join();
			}

			// Then send cooks home...
			// The easiest way to do this might be the following, where
			// we interrupt their threads. There are other approaches
			// though, so you can change this if you want to.
			for (int i = 0; i < cooks.length; i++)
				cooks[i].interrupt();
			for (int i = 0; i < cooks.length; i++)
				cooks[i].join();

		} catch (InterruptedException e) {
			System.out.println("Simulation thread interrupted.");
		}

		// Shut down machines
		for (Machine i : machines.values()) {
			logEvent(SimulationEvent.machineEnding(i));
		}
		// Done with simulation
		logEvent(SimulationEvent.endSimulation());

		return events;
	}

	/**
	 * Entry point for the simulation.
	 * 
	 * @param args
	 *            the command-line arguments for the simulation. There should be
	 *            exactly four arguments: the first is the number of customers,
	 *            the second is the number of cooks, the third is the number of
	 *            tables in the restaurant, and the fourth is the number of
	 *            items each cooking machine can make at the same time.
	 */
	public static void main(String args[]) throws InterruptedException {
		// Parameters to the simulation
		/*
		 * if (args.length != 4) { System.err .println(
		 * "usage: java Simulation <#customers> <#cooks> <#tables> <capacity> <randomorders"
		 * ); System.exit(1); } int numCustomers = new
		 * Integer(args[0]).intValue(); int numCooks = new
		 * Integer(args[1]).intValue(); int numTables = new
		 * Integer(args[2]).intValue(); int machineCapacity = new
		 * Integer(args[3]).intValue(); boolean randomOrders = new
		 * Boolean(args[4]);
		 */

		int numCustomers = 100;
		int numCooks = 58;
		int numTables = 75;
		int machineCapacity = 40;
		boolean randomOrders = false;

		// Run the simulation and then
		// feed the result into the method to validate simulation.

		System.out.println("Did it work? "
				+ Validate.validateSimulation(runSimulation(numCustomers,
						numCooks, numTables, machineCapacity, randomOrders)));

	}

}
