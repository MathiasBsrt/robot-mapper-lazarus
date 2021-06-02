package view.settings;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class JTableSettingMouseListener extends MouseAdapter {
	
	private final JTable table;

	public JTableSettingMouseListener(JTable table) {
		this.table = table;
	}

	public void mouseClicked(MouseEvent e) {
		TableCellRenderer cellRenderer = table.getColumnModel().getColumn(1).getCellRenderer();
		JTableSettingRenderer settingRenderer = (JTableSettingRenderer) cellRenderer;
		if(e.getClickCount() == 1) {
			settingRenderer.stopEditing();
			table.repaint();
		} else if(e.getClickCount() == 2) {
			int column = table.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
			int row = e.getY() / table.getRowHeight(); // get the row of the button

			/* Checking the row or column is valid or not */
			if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
				Object value = table.getValueAt(row, column);
				if (value instanceof SettingButtonEditor) {
					/* perform a click event */
					settingRenderer.editRow(row);
					table.repaint();
					((SettingButtonEditor) value).doClick();
				}
			}
		}
	}
	
}
