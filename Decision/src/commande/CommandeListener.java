package commande;

import decision.StatusMessage;

public interface CommandeListener{	
	
	public void onReceiveData(StatusMessage m);
	
}
