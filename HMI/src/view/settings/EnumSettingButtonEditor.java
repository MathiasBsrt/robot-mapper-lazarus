package view.settings;

public class EnumSettingButtonEditor extends SettingButtonEditor {

	public EnumSettingButtonEditor(String label) {
		super(label);
	}

	@Override
	public void onClick() {
		
	}

	@Override
	public void onButtonPressed() {
		super.setLabel("...");
	}

	@Override
	public void drawAfterAClick() {
		
	}

	@Override
	public void drawAfterButtonPressed() {
		
	}

}
