package commande;

import decision.Instructions;

public class CommandePower extends CommandeParams {
	
	public CommandePower(float speed) {
		super(Instructions.POWER, speed);
	}
	
}
