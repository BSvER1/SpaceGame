package com.spaceGame.main;

import java.util.Random;

import javax.vecmath.Vector3d;

import com.spaceGame.assets.GameObject;
import com.spaceGame.main.graphics.GameWindow;
import com.spaceGame.world.Octree;

public class Driver {

	private static boolean TRACING = true;
	
	public static void main(String[] args) {
		Driver.trace("Hello SpaceGame!");
		
		Random r = new Random();

		Octree universe = new Octree(new Vector3d(100,100,100), new Vector3d(0,0,0));
		
		for (int i = 0; i < 150; i++) {
			universe.addObject(new GameObject(new Vector3d(r.nextInt(100), r.nextInt(100), r.nextInt(100))));
			//universe.printInternalSizes();
		}
		
		Driver.trace("finished");
		
		universe.printInternalSizes();
		universe.printObjectLocations();
		new GameWindow().run();
		
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				
//				
//			}
//		});
		
	}
	
	public static void trace(String msg) {
		if (TRACING) {
			System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName().substring(
					Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.')+1)+"."
					+ "" + Thread.currentThread().getStackTrace()[2].getMethodName() + "()]: " + msg);
		}
	}

}
