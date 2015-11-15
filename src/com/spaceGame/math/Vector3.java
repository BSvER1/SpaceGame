package com.spaceGame.math;

public class Vector3 {

	public final float vec[];

	public Vector3() {
		vec = new float[3];
	}

	public Vector3(final float x, final float y, final float z) {
		vec = new float[3];

		vec[0] = x;
		vec[1] = y;
		vec[2] = z;
	}

	public void set(final Vector3 position) {
		for (int i = 0; i < 3; i++) {
			vec[i] = position.vec[i];
		}
	}
}
