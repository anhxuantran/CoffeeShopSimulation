package cmsc433_p3;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.junit.Test;

import cmsc433_p3.Cook;
import cmsc433_p3.Customer;
import cmsc433_p3.Food;
import cmsc433_p3.FoodType;
import cmsc433_p3.Machine;
import cmsc433_p3.Simulation;
import cmsc433_p3.SimulationEvent;
import cmsc433_p3.Validate;

public class ExampleValidateTests {
	
	public static void logEvent(
			List<SimulationEvent> events, SimulationEvent event) {
		events.add(event);
		System.out.println(event);
	}
	@Test
	public void testThatShouldGiveFalse() {
		List<SimulationEvent> events = new ArrayList<SimulationEvent>();
		
		int numCustomers = 2;
		int numCooks = 2;
		int numTables = 1;
		int machineCapacity = 1;
		
		logEvent(events, SimulationEvent.startSimulation(
				numCustomers, numCooks, numTables, machineCapacity
		));
		assertFalse(Validate.validateSimulation(events));
	}
	
	@Test
	public void testStudentValidateSuccess() {
		List<SimulationEvent> events = new ArrayList<SimulationEvent>();
		
		int numCustomers = 2;
		int numCooks = 2;
		int numTables = 1;
		int machineCapacity = 1;
		
		logEvent(events, SimulationEvent.startSimulation(
				numCustomers, numCooks, numTables, machineCapacity
		));

		Machine grill, frier, percolator;
		String name = "Grill";
		grill = new Machine(name,FoodType.burger,machineCapacity);
		logEvent(events, SimulationEvent.machineStarting(grill,
				FoodType.burger,
				machineCapacity));

		name = "Fryer";
		frier = new Machine(name,FoodType.fries,machineCapacity);
		logEvent(events, SimulationEvent.machineStarting(frier,
				FoodType.fries,
				machineCapacity));

		name = "CoffeeMaker2000";
		percolator = new Machine(name,FoodType.coffee,machineCapacity);
		logEvent(events, SimulationEvent.machineStarting(percolator,
				FoodType.coffee,
				machineCapacity));

		
		Cook cook1 = new Cook("Cook 0");
		logEvent(events, SimulationEvent.cookStarting(cook1));
		Cook cook2 = new Cook("Cook 1");
		logEvent(events, SimulationEvent.cookStarting(cook2));
		
		
		
		LinkedList<Food> order = new LinkedList<Food>();
		order.add(FoodType.burger);
		order.add(FoodType.fries);
		order.add(FoodType.fries);
		order.add(FoodType.coffee);
		Customer customer1 = new Customer("Customer 0", order);
		logEvent(events, SimulationEvent.customerStarting(customer1));
		Customer customer2 = new Customer("Customer 1", order);
		logEvent(events, SimulationEvent.customerStarting(customer2));

		
		
		logEvent(events, SimulationEvent.customerEnteredCoffeeShop(customer1));
		logEvent(events, SimulationEvent.customerPlacedOrder(customer1, order, 1));
		
		logEvent(events, SimulationEvent.cookReceivedOrder(cook1, order, 1));
		
		logEvent(events, SimulationEvent.cookStartedFood(cook1, FoodType.burger, 1));
		logEvent(events, SimulationEvent.machineCookingFood(grill, FoodType.burger));
		logEvent(events, SimulationEvent.machineDoneFood(grill, FoodType.burger));
		logEvent(events, SimulationEvent.cookFinishedFood(cook1, FoodType.burger, 1));

		logEvent(events, SimulationEvent.cookStartedFood(cook1, FoodType.fries, 1));
		logEvent(events, SimulationEvent.machineCookingFood(frier, FoodType.fries));
		logEvent(events, SimulationEvent.machineDoneFood(frier, FoodType.fries));
		logEvent(events, SimulationEvent.cookFinishedFood(cook1, FoodType.fries, 1));

		logEvent(events, SimulationEvent.cookStartedFood(cook1, FoodType.coffee, 1));
		logEvent(events, SimulationEvent.machineCookingFood(percolator, FoodType.coffee));
		logEvent(events, SimulationEvent.machineDoneFood(percolator, FoodType.coffee));
		logEvent(events, SimulationEvent.cookFinishedFood(cook1, FoodType.coffee, 1));
		
		logEvent(events, SimulationEvent.cookStartedFood(cook1, FoodType.fries, 1));
		logEvent(events, SimulationEvent.machineCookingFood(frier, FoodType.fries));
		logEvent(events, SimulationEvent.machineDoneFood(frier, FoodType.fries));
		logEvent(events, SimulationEvent.cookFinishedFood(cook1, FoodType.fries, 1));
		
		logEvent(events, SimulationEvent.cookCompletedOrder(cook1, 1));
		
		logEvent(events, SimulationEvent.customerReceivedOrder(customer1, order, 1));
		logEvent(events, SimulationEvent.customerLeavingCoffeeShop(customer1));
		
		


		
		
		logEvent(events, SimulationEvent.customerEnteredCoffeeShop(customer2));
		logEvent(events, SimulationEvent.customerPlacedOrder(customer2, order, 2));
		
		logEvent(events, SimulationEvent.cookReceivedOrder(cook1, order, 2));
		
		logEvent(events, SimulationEvent.cookStartedFood(cook1, FoodType.burger, 2));
		logEvent(events, SimulationEvent.machineCookingFood(grill, FoodType.burger));
		logEvent(events, SimulationEvent.machineDoneFood(grill, FoodType.burger));
		logEvent(events, SimulationEvent.cookFinishedFood(cook1, FoodType.burger, 2));

		logEvent(events, SimulationEvent.cookStartedFood(cook1, FoodType.fries, 2));
		logEvent(events, SimulationEvent.machineCookingFood(frier, FoodType.fries));
		logEvent(events, SimulationEvent.machineDoneFood(frier, FoodType.fries));
		logEvent(events, SimulationEvent.cookFinishedFood(cook1, FoodType.fries, 2));

		logEvent(events, SimulationEvent.cookStartedFood(cook1, FoodType.coffee, 2));
		logEvent(events, SimulationEvent.machineCookingFood(percolator, FoodType.coffee));
		logEvent(events, SimulationEvent.machineDoneFood(percolator, FoodType.coffee));
		logEvent(events, SimulationEvent.cookFinishedFood(cook1, FoodType.coffee, 2));
		
		logEvent(events, SimulationEvent.cookStartedFood(cook1, FoodType.fries, 2));
		logEvent(events, SimulationEvent.machineCookingFood(frier, FoodType.fries));
		logEvent(events, SimulationEvent.machineDoneFood(frier, FoodType.fries));
		logEvent(events, SimulationEvent.cookFinishedFood(cook1, FoodType.fries, 2));
		
		logEvent(events, SimulationEvent.cookCompletedOrder(cook1, 2));
		
		logEvent(events, SimulationEvent.customerReceivedOrder(customer2, order, 2));
		logEvent(events, SimulationEvent.customerLeavingCoffeeShop(customer2));
		
		


		
		logEvent(events, SimulationEvent.cookEnding(cook1));
		logEvent(events, SimulationEvent.cookEnding(cook2));
		
		logEvent(events, SimulationEvent.machineEnding(grill));
		logEvent(events, SimulationEvent.machineEnding(frier));
		logEvent(events, SimulationEvent.machineEnding(percolator));

		logEvent(events, SimulationEvent.endSimulation());
		
		
		
		
		
		assertTrue(Validate.validateSimulation(events));
	}
	
}
