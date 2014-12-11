package hu.dobrei.diploma;

import hu.dobrei.diploma.routing.Simulation;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public class Main {

	public static void main(String[] args) {

		Stopwatch stopwatch = Stopwatch.createStarted();

		Simulation.simulate();

		System.out.println("Full time: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
	}
}