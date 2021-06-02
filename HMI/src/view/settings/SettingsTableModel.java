package view.settings;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import com.studiohartman.jamepad.ControllerButton;

import model.settings.ProgramSettings;
import model.settings.Setting;

public class SettingsTableModel extends AbstractTableModel {

	private final String[] entetes = { "Setting", "Value" };

	@Override
	public int getRowCount() {
		return Setting.values().length;
	}

	@Override
	public int getColumnCount() {
		return entetes.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return entetes[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Setting[] settings = Setting.values();
		switch (columnIndex) {
		case 0: {
			return settings[rowIndex].getLabel();
		}
		case 1: {
			String label = String.valueOf(ProgramSettings.get(settings[rowIndex]));
			SettingButtonEditor button = SettingButtonEditor.createFromClass(settings[rowIndex].getAttributedClass(), label);
			return button;
		}
		default: {
			return "";
		}
		}
	}

	@Override
	public Class getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return SettingButtonEditor.class;
		default:
			return Object.class;
		}
	}

}
