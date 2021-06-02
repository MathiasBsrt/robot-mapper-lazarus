package decision;

//placeholder class for the status message
public class StatusMessage {
	private float x;
	private float y;
	private float rot; // in radians
	private float s1Dist;
	private float s2Dist;
	private float s3Dist;

	public StatusMessage(float x, float y, float rot, float s1, float s2, float s3) {
		this.x = x;
		this.y = y;
		this.rot = rot; // if robots uses other system, convert to radians
		this.s1Dist = s1;
		this.s2Dist = s2;
		this.s3Dist = s3;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getRot() {
		return rot;
	}

	public void setRot(float rot) {
		this.rot = rot;
	}

	public float getS1Dist() {
		return s1Dist;
	}

	public void setS1Dist(float s1Dist) {
		this.s1Dist = s1Dist;
	}

	public float getS2Dist() {
		return s2Dist;
	}

	public void setS2Dist(float s2Dist) {
		this.s2Dist = s2Dist;
	}

	public float getS3Dist() {
		return s3Dist;
	}

	public void setS3Dist(float s3Dist) {
		this.s3Dist = s3Dist;
	}

	public String toString() {
		return "StatusMessage [x=" + x + ", y=" + y + ", rot=" + rot + ", s1Dist=" + s1Dist + ", s2Dist=" + s2Dist
				+ ", s3Dist=" + s3Dist + "]";
	}
}
