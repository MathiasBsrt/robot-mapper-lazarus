package commande;

import decision.Instructions;

public abstract class Commande {
	
	protected Instructions instruction;
	
	public Commande(Instructions instruction) {
		this.instruction = instruction;
	}
	
	public String toString() {
		return "" + instruction.getCode();
	}
	
	public byte[] getBytes() {
		byte commandCharCode = (byte) instruction.getCode();
		return new byte[] { commandCharCode } ;
	}
	
}
