package commande;

import decision.Instructions;

public class CommandeDrive extends CommandeParams {
	public CommandeDrive(float distance) {
		super(Instructions.DRIVE, distance);
	}
}
