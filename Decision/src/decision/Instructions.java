package decision;

public enum Instructions {
	
	DRIVE((char) 'D'),
	FORWARD((char) 'F'),
	BACKWARDS((char) 'B'),
	TURN((char) 'T'),
	STOP((char) 'S'),
	STATUS((char) 'R'),
	MANUAL((char) 'M'),
	AUTOMATIC((char) 'A'),
	POWER((char) 'P');
	
	char code;
	private Instructions(char code) {
		this.code = code;
	}
	
	public char getCode() {
		return code;
	}
}
