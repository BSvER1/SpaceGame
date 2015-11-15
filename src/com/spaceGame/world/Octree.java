package com.spaceGame.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector3d;

import org.apache.commons.math3.analysis.function.Pow;

import com.spaceGame.assets.GameObject;
import com.spaceGame.main.Driver;
import com.spaceGame.main.MathHelper;

public class Octree {

	private boolean octTrace = false;
	
	private final int LEFT = 0;
	private final int RIGHT = 1;
	private final int ABOVE = 2;
	private final int BELOW = 3;
	private final int BACK = 4;
	private final int FRONT = 5;
	
	private final int INTERNAL_FRONT = 0;
	private final int INTERNAL_BACK = 1;
	private final int INTERNAL_BOTTOM = 0;
	private final int INTERNAL_TOP = 1;
	private final int INTERNAL_LEFT = 0;
	private final int INTERNAL_RIGHT = 1;
	
	
	
	private Octree[] adjNodes;
	private Octree[][][] internals;
	private ArrayList<GameObject> objList;
	
	private static Octree root = null;
	
	private Random r;
	private Color col;
	
	//private boolean div = false;
	
	private Vector3d topRight, btmLeft;
	
	
	public Octree(Vector3d topRight, Vector3d btmLeft) {
		if (topRight.x > btmLeft.x && topRight.y > btmLeft.y && topRight.z > btmLeft.z) {
			this.topRight = topRight;
			this.btmLeft = btmLeft;
		} else if (topRight.x < btmLeft.x && topRight.y < btmLeft.y && topRight.z < btmLeft.z) {
			this.topRight = btmLeft;
			this.btmLeft = topRight;
		} else {
			Driver.trace("fucked up somehow..." + topRight.toString()+btmLeft.toString());
			System.exit(0);
		}
		adjNodes = new Octree[6];
		for (int i = 0; i < adjNodes.length; i++) {
			adjNodes[i] = null;
		}
		internals = null;
		
		objList = new ArrayList<GameObject>();
		objList.clear();
		
		if (root == null) {
			root = this;
			Driver.trace("setting root!");
		}
	
		r = new Random();
		col = new Color(r.nextInt(127)+127, r.nextInt(127)+127, r.nextInt(127)+127);
		//Driver.trace("pos: "+ btmLeft.toString() +", " + topRight.toString());
	}
	
	public ArrayList<GameObject> getAdjacentObjects(double dist) {
		boolean hasChanged;
		ArrayList<GameObject> list = new ArrayList<GameObject>();
		
		list.add(getObject());
		
		do {
			hasChanged = false;
			for (GameObject obj : list) {
				for (int i = 0; i < 8; i++) {
					if (!obj.equals(getObject())) {
						if (getDistance(getAdj(i).getObject()) < dist) {
							list.add(getAdj(i).getObject());
							hasChanged = true;
							break;
						}
					}
				}
				if (hasChanged)
					break;
			}
		} while (hasChanged);
		
		list.remove(getObject());
		
		return list;
	}
	
	private double getDistance(GameObject target) {
		
		if (target.getPos().x == objList.get(0).getPos().x &&
				target.getPos().y == objList.get(0).getPos().y &&
				target.getPos().z == objList.get(0).getPos().z) {
			return Double.MAX_VALUE;
		}
		
		double dist = MathHelper.pow(target.getPos().x-objList.get(0).getPos().x, 2);
		dist += MathHelper.pow(target.getPos().y-objList.get(0).getPos().y, 2);
		dist += MathHelper.pow(target.getPos().z-objList.get(0).getPos().z, 2);
		
		dist = MathHelper.sqrt(dist);
		
		return dist;
	}
	
	public void printObjectLocations() {
		if (!objList.isEmpty()) {
			Driver.trace("found "+objList.get(0).getPos().toString()+ " in "+this.toString());
		}
		
		if (internals != null) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					for (int k = 0; k < 2; k++) {
						if (internals[i][j][k] != null)
							internals[i][j][k].printObjectLocations();
					}
				}
			}
		}

	}
	
	public void printInternalSizes() {
		Driver.trace(""+btmLeft.toString()+":"+topRight.toString());
		if (internals != null) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					for (int k = 0; k < 2; k++) {
						if (internals[i][j][k] != null)
							internals[i][j][k].printInternalSizes();
					}
				}
			}
		}
	}

//	@Deprecated
//	private void addObjectToOctree(GameObject obj) {
//		if (objList.isEmpty()) {
//			objList.add(obj);
//			Driver.trace("placing obj at "+obj.getPos().toString()+" in "+btmLeft.toString()+":"+topRight.toString());
//			//div = true;
//		} else if (internals == null) {
//			Driver.trace("performing subdivision in order to place at "+obj.getPos().toString() + " somewhere in "+ btmLeft.toString()+":"+topRight.toString());
//			//Do division
//			createInternals();
//			setAdjacencies();
//			
//			//place objects into internals
//			placeIntoInternal(obj);
//			if (!objList.isEmpty()) 
//				placeIntoInternal(objList.get(0));
//			Driver.trace("clearing "+ this.toString());
//			this.objList.clear();
//		} else {
//			placeIntoInternal(obj);
//		}
//	}
	
	public GameObject getObject() {
		return objList.get(0);
	}
	
	public Octree getInternal(int lr, int bt, int fb) {
		return internals[lr][bt][fb];
	}
	
//	public boolean isDivided() {
//		return div;
//	}
	
	public void setAdj(int dir, Octree node) {
		adjNodes[dir] = node;
	}
	
	public Octree getAdj(int dir) {
		return adjNodes[dir];
	}
	
	public Octree getPossibleInternal(int lr, int bt, int fb) {
		if (internals != null && internals[lr][bt][fb] != null)
			return internals[lr][bt][fb];
		else
			return this;
	}
	
	public void addObject(GameObject obj) {
		if (obj.getPos().x < btmLeft.x || obj.getPos().x >= topRight.x) {
			Driver.trace("x somehow out of place when adding "+ obj.getPos().toString());
			System.exit(0);
		}
		if (obj.getPos().y < btmLeft.y || obj.getPos().y >= topRight.y) {
			Driver.trace("y somehow out of place when adding "+ obj.getPos().toString());
			System.exit(0);
		}
		if (obj.getPos().z < btmLeft.z || obj.getPos().z >= topRight.z) {
			Driver.trace("z somehow out of place when adding "+ obj.getPos().toString());
			System.exit(0);
		}
		
		if (internals != null) {
			//if (octTrace) 
				Driver.trace("recursing down as obj is "+obj.getPos().toString()+" and area is divided");
			placeIntoInternal(obj);
		} else {
			if (objList.isEmpty()) {
				//if (octTrace) 
					Driver.trace("placing object at "+obj.getPos().toString()+" in "+btmLeft.toString()+":"+topRight.toString());
				objList.add(obj);
			} else {
				if (objList.get(0).getPos().equals(obj.getPos())) {
					Driver.trace("got duplicate! big issue!");
					System.exit(0);
				}
				if (internals == null) {
					//if (octTrace) 
						Driver.trace("performing subdivision in order to place at "+obj.getPos().toString() + " in "+ btmLeft.toString()+":"+topRight.toString());
					//Do division
					createInternals();
					setAdjacencies();
					
					//place objects into internals
					placeIntoInternal(obj);
					placeIntoTree(objList.get(0));
					if (octTrace) Driver.trace("clearing ");
					this.objList.clear();
				} else {
					Driver.trace("huh?! how did we get here");
					System.exit(0);
				}
			}
		}
	}
	
	private void placeIntoTree(GameObject obj) {
		root.addObject(obj);
	}
	
	private void placeIntoInternal(GameObject obj) {
		int posX, posY, posZ;
		
		if (obj.getPos().x < btmLeft.x+(topRight.x - btmLeft.x)/2) {
			posX = INTERNAL_LEFT;
		} else {
			posX = INTERNAL_RIGHT;
		}
		if (obj.getPos().y < btmLeft.y+(topRight.y - btmLeft.y)/2) {
			posY = INTERNAL_BOTTOM;
		} else {
			posY = INTERNAL_TOP;
		}
		if (obj.getPos().z < btmLeft.z+(topRight.z - btmLeft.z)/2) {
			posZ = INTERNAL_FRONT;
		} else {
			posZ = INTERNAL_BACK;
		}
		
		//Driver.trace("placing object at "+obj.getPos().toString()+" into " +internals[posX][posY][posZ]);
		
		internals[posX][posY][posZ].addObject(obj);
	}
	
	private void createInternals() {
		internals = new Octree[2][2][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < 2; k++) {
					internals[i][j][k] = null;
				}
			}
		}
		//Driver.trace("creating bfl");
		internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT] = new Octree(
				new Vector3d(btmLeft.x+(topRight.x - btmLeft.x)/2, btmLeft.y+(topRight.y - btmLeft.y)/2, btmLeft.z+(topRight.z - btmLeft.z)/2), 
				btmLeft);
		//Driver.trace("creating bbl");
		internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK] = new Octree(
				new Vector3d(btmLeft.x+(topRight.x - btmLeft.x)/2, btmLeft.y+(topRight.y - btmLeft.y)/2, topRight.z), 
				new Vector3d(btmLeft.x, btmLeft.y, btmLeft.z+(topRight.z- btmLeft.z)/2));
		
		//Driver.trace("creating bfr");
		internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT] = new Octree(
				new Vector3d(topRight.x, btmLeft.y+(topRight.y - btmLeft.y)/2, btmLeft.z+(topRight.z - btmLeft.z)/2), 
				new Vector3d(btmLeft.x+(topRight.x - btmLeft.x)/2, btmLeft.y, btmLeft.z));
		Driver.trace("creating bbr");
		internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK] = new Octree(
				new Vector3d(topRight.x, btmLeft.y+(topRight.y - btmLeft.y)/2, topRight.z), 
				new Vector3d(btmLeft.x+(topRight.x - btmLeft.x)/2, btmLeft.y, btmLeft.z));
		
		//Driver.trace("creating tfl");
		internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT] = new Octree(
				new Vector3d(btmLeft.x+(topRight.x - btmLeft.x)/2, topRight.y, btmLeft.z+(topRight.z - btmLeft.z)/2), 
				new Vector3d(btmLeft.x, btmLeft.y+(topRight.y - btmLeft.y)/2, btmLeft.z));
		//Driver.trace("creating tbl");
		internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK] = new Octree(
				new Vector3d(btmLeft.x+(topRight.x - btmLeft.x)/2, topRight.y, topRight.z),
				new Vector3d(btmLeft.x, btmLeft.y+(topRight.y - btmLeft.y)/2, btmLeft.z+(topRight.z - btmLeft.z)/2));
		
		//Driver.trace("creating tfr");
		internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT] = new Octree(
				new Vector3d(topRight.x, topRight.y, btmLeft.z+(topRight.z - btmLeft.z)/2), 
				new Vector3d(btmLeft.x+(topRight.x - btmLeft.x)/2, btmLeft.y+(topRight.y - btmLeft.y)/2, btmLeft.z));
		//Driver.trace("creating tbr");
		internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK] = new Octree(
				topRight, 
				new Vector3d(btmLeft.x+(topRight.x - btmLeft.x)/2, btmLeft.y+(topRight.y - btmLeft.y)/2, btmLeft.z+(topRight.z - btmLeft.z)/2));
	}
	
	private void setAdjacencies() {
		internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(ABOVE, internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT]);
		if (adjNodes[BELOW] != null)
			internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(BELOW, adjNodes[BELOW].getPossibleInternal(INTERNAL_LEFT, INTERNAL_TOP, INTERNAL_FRONT));
		if (adjNodes[LEFT] != null)
			internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(LEFT, adjNodes[LEFT].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_BOTTOM, INTERNAL_FRONT));
		internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(RIGHT, internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT]);
		if (adjNodes[FRONT] != null)
			internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(FRONT, adjNodes[FRONT].getPossibleInternal(INTERNAL_LEFT, INTERNAL_BOTTOM, INTERNAL_BACK));
		internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(BACK, internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK]);
		
		internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(ABOVE, internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK]);
		if (adjNodes[BELOW] != null)
			internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(BELOW, adjNodes[BELOW].getPossibleInternal(INTERNAL_LEFT, INTERNAL_TOP, INTERNAL_BACK));
		if (adjNodes[LEFT] != null)
			internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(LEFT, adjNodes[LEFT].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_BOTTOM, INTERNAL_BACK));
		internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(RIGHT, internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK]);
		internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(FRONT, internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT]);
		if (adjNodes[BACK] != null)
			internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(BACK, adjNodes[BACK].getPossibleInternal(INTERNAL_LEFT, INTERNAL_FRONT, INTERNAL_BACK));
		
		internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(ABOVE, internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT]);
		if (adjNodes[BELOW] != null)
			internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(BELOW, adjNodes[BELOW].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_TOP, INTERNAL_FRONT));
		internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(LEFT, internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT]);
		if (adjNodes[RIGHT] != null)
			internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(RIGHT, adjNodes[RIGHT].getPossibleInternal(INTERNAL_LEFT, INTERNAL_BOTTOM, INTERNAL_FRONT));
		if (adjNodes[FRONT] != null)
			internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(FRONT, adjNodes[FRONT].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_BOTTOM, INTERNAL_BACK));
		internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT].setAdj(BACK, internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK]);
		
		internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(ABOVE, internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK]);
		if (adjNodes[BELOW] != null)
			internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(BELOW, adjNodes[BELOW].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_TOP, INTERNAL_BACK));
		internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(LEFT, internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK]);
		if (adjNodes[RIGHT] != null)
			internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(RIGHT, adjNodes[RIGHT].getPossibleInternal(INTERNAL_LEFT, INTERNAL_BOTTOM, INTERNAL_BACK));
		internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(FRONT, internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT]);
		if (adjNodes[BACK] != null)
			internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK].setAdj(BACK, adjNodes[BACK].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_BOTTOM, INTERNAL_FRONT));
		
		if (adjNodes[ABOVE] != null) 
			internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(ABOVE, adjNodes[ABOVE].getPossibleInternal(INTERNAL_LEFT, INTERNAL_BOTTOM, INTERNAL_FRONT));
		internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(BELOW, internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_FRONT]);
		if (adjNodes[LEFT] != null)
			internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(LEFT, adjNodes[LEFT].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_TOP, INTERNAL_FRONT));
		internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(RIGHT, internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT]);
		if (adjNodes[FRONT] != null)
			internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(FRONT, adjNodes[FRONT].getPossibleInternal(INTERNAL_LEFT, INTERNAL_TOP, INTERNAL_BACK));
		internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(BACK, internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK]);
		
		if (adjNodes[ABOVE] != null)
			internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK].setAdj(ABOVE, adjNodes[ABOVE].getPossibleInternal(INTERNAL_LEFT, INTERNAL_BOTTOM, INTERNAL_BACK));
		internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK].setAdj(BELOW, internals[INTERNAL_LEFT][INTERNAL_BOTTOM][INTERNAL_BACK]);
		if (adjNodes[LEFT] != null)
			internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK].setAdj(LEFT, adjNodes[LEFT].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_TOP, INTERNAL_BACK));
		internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK].setAdj(RIGHT, internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK]);
		internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK].setAdj(FRONT, internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT]);
		if (adjNodes[BACK] != null)
			internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK].setAdj(BACK, adjNodes[BACK].getPossibleInternal(INTERNAL_LEFT, INTERNAL_TOP, INTERNAL_FRONT));
		
		if (adjNodes[ABOVE] != null)
			internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(ABOVE, adjNodes[ABOVE].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_BOTTOM, INTERNAL_FRONT));
		internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(BELOW, internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_FRONT]);
		internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(LEFT, internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_FRONT]);
		if (adjNodes[RIGHT] != null)
			internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(RIGHT, adjNodes[RIGHT].getPossibleInternal(INTERNAL_LEFT, INTERNAL_TOP, INTERNAL_FRONT));
		if (adjNodes[FRONT] != null)
			internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(FRONT, adjNodes[FRONT].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_TOP, INTERNAL_LEFT));
		internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT].setAdj(BACK, internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK]);
		
		if (adjNodes[ABOVE] != null)
			internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK].setAdj(ABOVE, adjNodes[ABOVE].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_BOTTOM, INTERNAL_BACK));
		internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK].setAdj(BELOW, internals[INTERNAL_RIGHT][INTERNAL_BOTTOM][INTERNAL_BACK]);
		internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK].setAdj(LEFT, internals[INTERNAL_LEFT][INTERNAL_TOP][INTERNAL_BACK]);
		if (adjNodes[RIGHT] != null)
			internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK].setAdj(RIGHT, adjNodes[RIGHT].getPossibleInternal(INTERNAL_LEFT, INTERNAL_TOP, INTERNAL_BACK));
		internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK].setAdj(FRONT, internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_FRONT]);
		if (adjNodes[BACK] != null)
			internals[INTERNAL_RIGHT][INTERNAL_TOP][INTERNAL_BACK].setAdj(BACK, adjNodes[BACK].getPossibleInternal(INTERNAL_RIGHT, INTERNAL_TOP, INTERNAL_FRONT));
	}
	
}
