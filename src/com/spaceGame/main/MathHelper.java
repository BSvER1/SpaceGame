package com.spaceGame.main;

import java.util.Random;

import org.apache.commons.math3.analysis.function.Pow;
import org.apache.commons.math3.analysis.function.Sqrt;

public class MathHelper {

	private static Sqrt sqrt = new Sqrt();
	private static Pow pow = new Pow();
	
	private static Random r = new Random();
	
	public static double sqrt(double value) {
		return sqrt.value(value);
	}
	
	public static double pow(double value, double power) {
		return pow.value(value, power);
	}
	
	public static double randomInt(int bound) {
		return r.nextInt(bound);
	}
}
