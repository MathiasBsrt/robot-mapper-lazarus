package view.settings;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import com.studiohartman.jamepad.ControllerButton;

import model.controller.ControllerButtonPressedEvent;
import model.controller.ControllerWrapper;
import model.settings.ProgramSettings;
import model.settings.Setting;

public class SettingsTable extends JTable implements ControllerButtonPressedEvent, KeyListener {

	private JTableSettingRenderer settingRenderer;

	public SettingsTable() {
		super(new SettingsTableModel());
		super.setCellSelectionEnabled(false);
		super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		super.setRowHeight(50);

		settingRenderer = new JTableSettingRenderer();

		super.getColumnModel().getColumn(1).setCellRenderer(settingRenderer);

		super.addMouseListener(new JTableSettingMouseListener(this));
		super.addKeyListener(this);
		ControllerWrapper.addButtonPressedListener(this);
	}
	
	public void stopEditing() {
		settingRenderer.stopEditing();
	}

	@Override
	public void onControllerButtonPressed(ControllerButton button) {
		if (ButtonAlreadyMappedPane.isOpen())
			return;

		int currentRowIndex = super.getSelectedRow();
		int currentColunmIndex = super.getSelectedColumn();
		int newRowIndex = currentRowIndex;
		int newColumnIndex = currentColunmIndex;

		if (settingRenderer.isEditing()) {
			registerNewControl(button);
			repaint();
			return;
		}

		if (newColumnIndex != 1)
			newColumnIndex = 1;
		switch (button) {
		case DPAD_UP: {
			newRowIndex -= 1;
			break;
		}
		case DPAD_DOWN: {
			newRowIndex += 1;
			break;
		}
		case A: {
			SettingButtonEditor settingButton = (SettingButtonEditor) super.getValueAt(currentRowIndex,
					currentColunmIndex);
			settingButton.doButtonPressed();
			settingRenderer.editRow(currentRowIndex);
			repaint();
		}
		default:
			break;
		}

		newRowIndex = Math.max(newRowIndex, 0);
		newRowIndex = Math.min(newRowIndex, super.getRowCount() - 1);

		newColumnIndex = Math.max(newColumnIndex, 0);
		newColumnIndex = Math.min(newColumnIndex, super.getColumnCount() - 1);

		if (newRowIndex != currentRowIndex || newColumnIndex != currentColunmIndex) {
			settingRenderer.stopEditing();
		}
		this.setSelection(newRowIndex, newColumnIndex);
	}

	public void setSelection(int row, int column) {
		super.setRowSelectionInterval(row, row);
		super.setColumnSelectionInterval(column, column);
	}

	private void registerNewControl(ControllerButton button) {
		int currentRowIndex = super.getSelectedRow();
		int currentColunmIndex = super.getSelectedColumn();
		Setting[] settings = Setting.values();
		Setting setting = settings[currentRowIndex];
		SettingButtonEditor settingButton = (SettingButtonEditor) super.getValueAt(currentRowIndex,
				currentColunmIndex);
		if (setting.getAttributedClass().equals(ControllerButton.class)) {
			ControllerButton oldButton = ControllerButton.valueOf(settingButton.getLabel());
			if(!oldButton.equals(button)) {
				if (ProgramSettings.alreadyExists(button)) {
					Setting mappedSetting = ProgramSettings.getSettingFromValue(button);
					ButtonAlreadyMappedPane.open(button, mappedSetting);
				} else {
					ProgramSettings.register(setting, button);
				}
			}
			settingRenderer.stopEditing();
		} else if (setting.getAttributedClass().equals(double.class)) {
			double value = Double.parseDouble(settingButton.getLabel());
			switch (button) {
			case DPAD_UP: {
				value = Math.min(value + 0.1d, 1.0d);
				value = Math.round(value * 10) / 10d;
				ProgramSettings.register(setting, value);
				break;
			}
			case DPAD_DOWN: {
				value = Math.max(value - 0.1d, 0.1d);
				value = Math.round(value * 10) / 10d;
				ProgramSettings.register(setting, value);
				break;
			}
			default: {
				settingRenderer.stopEditing();
				break;
			}
			}
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int currentRowIndex = super.getSelectedRow();
		int currentColunmIndex = super.getSelectedColumn();
		if(currentRowIndex < 0) return;

		Setting[] settings = Setting.values();
		Setting setting = settings[currentRowIndex];

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			settingRenderer.stopEditing();
		} else if (setting.getAttributedClass().equals(double.class)) {
			SettingButtonEditor settingButton = (SettingButtonEditor) super.getValueAt(currentRowIndex,
					currentColunmIndex);
			double value = Double.parseDouble(settingButton.getLabel());
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				value = Math.min(value + 0.1d, 1.0d);
				value = Math.round(value * 10) / 10d;
				ProgramSettings.register(setting, value);
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				value = Math.max(value - 0.1d, 0.1d);
				value = Math.round(value * 10) / 10d;
				ProgramSettings.register(setting, value);
			}

			repaint();
			e.consume();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
