package com.spaceGame.assets;

import javax.vecmath.Vector3d;

public class GameObject {

	Vector3d pos, vel;




	public GameObject(Vector3d pos) {
		this.pos = pos;
	}

	public Vector3d getPos() {
		return pos;
	}

	public void setPos(Vector3d pos) {
		this.pos = pos;
	}

	public Vector3d getVel() {
		return vel;
	}

	public void setVel(Vector3d vel) {
		this.vel = vel;
	}
}
