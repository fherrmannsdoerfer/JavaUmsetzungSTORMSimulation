package playground;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DataSetSelectionTable extends JTable {

	public DataSetSelectionTable(DataSetSelectionTableModel model) {
		super(model);
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,
			int column) {
		// TODO Auto-generated method stub
		Component c = super.prepareRenderer(renderer, row, column);
		if (!isCellSelectable(row, column)) {
			c.setBackground(Color.GRAY);
		}
		return c;
	}
	
	public boolean isCellSelectable(int row, int column) {
		return ((DataSetSelectionTableModel) getModel()).isCellSelectable(row, column);
	}
}
