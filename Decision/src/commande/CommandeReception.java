package commande;

import decision.Instructions;

public class CommandeReception  extends Commande{

	private float frontDistance;
	private float frontLeftDistance;
	private float rearLeftDistance;
	
	private float X;
	private float Y;
	
	public CommandeReception(float[] arguments) {
		super(Instructions.STATUS);
		frontDistance=arguments[0];
		frontLeftDistance=arguments[1];
		rearLeftDistance=arguments[2];
		X=arguments[3];
		Y=arguments[4];
	}

	public float getFrontDistance() {
		return frontDistance;
	}

	public float getFrontLeftDistance() {
		return frontLeftDistance;
	}

	public float getRearLeftDistance() {
		return rearLeftDistance;
	}

	public float getX() {
		return X;
	}

	public float getY() {
		return Y;
	}
	
	
	
}
