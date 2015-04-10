package table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DataSetSelectionTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataSetSelectionTable(DataSetSelectionTableModel model) {
		super(model);
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		if (!isCellSelectable(row, column)) {
			c.setBackground(Color.LIGHT_GRAY);
		}
		else {
			c.setBackground(Color.WHITE);
			c.setForeground(Color.BLACK);
		}
		return c;
	}
	
	public boolean isCellSelectable(int row, int column) {
		return ((DataSetSelectionTableModel) getModel()).isCellSelectable(row, column);
	}
}
