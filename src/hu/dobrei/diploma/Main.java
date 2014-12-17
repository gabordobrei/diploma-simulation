package hu.dobrei.diploma;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

public class Main {

	public static void main(String[] args) {

		Stopwatch stopwatch = Stopwatch.createStarted();

		List<Ent> list = Lists.newLinkedList();
		
		new Stat(list);

		/*-
		Simulation simulation = new Simulation();
		simulation.run();
		//*/

		System.out.println("Full time: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
	}
}