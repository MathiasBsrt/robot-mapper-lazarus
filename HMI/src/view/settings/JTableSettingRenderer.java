package view.settings;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.studiohartman.jamepad.ControllerButton;

import model.settings.Setting;

public class JTableSettingRenderer implements TableCellRenderer {
	
	private int editingRow = -1;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		SettingsTable settingsTable = (SettingsTable) table;
		SettingButtonEditor button = (SettingButtonEditor) value;
		Setting[] settings = Setting.values();
		Setting setting = settings[row];
		if(settingsTable.getSelectedRow() == row && settingsTable.getSelectedColumn() == column) button.setSelected(true);
		if(row == editingRow) {
			if(setting.getAttributedClass().equals(ControllerButton.class)) {
				button.setLabel("...");
			} else if(setting.getAttributedClass().equals(double.class)) {
				button.setLabel("> " + button.getLabel() + " <");
			}
		}
		return button;
	}
	
	public void editRow(int row) {
		editingRow = row;
	}
	
	public void stopEditing() {
		editingRow = -1;
	}
	
	public boolean isEditing() {
		return editingRow != -1;
	}
	
}