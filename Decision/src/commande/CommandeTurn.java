package commande;

import decision.Instructions;

public class CommandeTurn extends CommandeParams {
	
	public CommandeTurn(float angle) {
		super(Instructions.TURN, angle);
	}
	
}
