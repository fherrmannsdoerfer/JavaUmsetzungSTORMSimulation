package playground;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.DataSet;

class DataSetTableModel extends AbstractTableModel {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Dataset", "Visible"};

	public List<DataSet> data = new ArrayList<DataSet>();
	public List<Boolean> visibleSets = new ArrayList<Boolean>();

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int row, int col) {
		if(col == 0) {
			return data.get(row).getName();
		}
		else {
			return visibleSets.get(row);
		}
	}

	public String getColumnName(int col){
		return columnNames[col];
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex == 1) {
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex);
	}


	public void setValueAt(Object value, int row, int col){
		if(col == 0) {
			data.set(row,(DataSet) value);
			fireTableCellUpdated(row,col);
		}
		else {
			visibleSets.set(row, (Boolean) value);
			fireTableCellUpdated(row,col);
		}
	}

	public boolean isCellEditable(int row, int col){
		if(col == 1) {
			return true;
		}
		return false;
	}
	
	public boolean isCellSelectable(int row, int col) {
		return false;
	}
	
	public void addRow(DataSet set) {
		data.add(set);
		fireTableDataChanged();
	}

	public void removeRow(int row){
		data.remove(row);
		fireTableDataChanged();
	}
	
}

