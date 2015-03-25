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

	private String[] columnNames = {"Dataset"};

	public List<DataSet> data = new ArrayList<DataSet>();

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int row, int col) {
		return data.get(row).getName();
	}

	public String getColumnName(int col){
		return columnNames[col];
	}


	public void setValueAt(DataSet value, int row, int col){
		data.set(row,value);
		fireTableCellUpdated(row,col);
	}

	public boolean isCellEditable(int row, int col){
		return false;
	}
	
	public boolean isCellSelectable(int row, int col) {
		return false;
	}
	
	public void addRow(DataSet set) {
		data.add(set);
		fireTableDataChanged();
	}

	//	 public void insertData(Object[] values){
	//		 data.add(new Vector());
	//		 for(int i =0; i<values.length; i++){
	//			 ((Vector) data.get(data.size()-1)).add(values[i]);
	//		 }
	//		 fireTableDataChanged();
	//	 }

	public void removeRow(int row){
		data.remove(row);
		fireTableDataChanged();
	}
}

