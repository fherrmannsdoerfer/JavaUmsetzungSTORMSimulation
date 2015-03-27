package playground;

import gui.DataTypeDetector.DataType;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.DataSet;

class DataSetSelectionTableModel extends AbstractTableModel {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Dataset"};

	public List<DataSet> data = new ArrayList<DataSet>();
	public DataType selectableDataType;


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
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if(columnIndex == 1) {
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex);
	}


	public void setValueAt(Object value, int row, int col){
		data.set(row,(DataSet) value);
		fireTableCellUpdated(row,col);
	}

	public boolean isCellEditable(int row, int col){
		return false;
	}
	
	public boolean isCellSelectable(int row, int col) {
		if(data.get(row).dataType == selectableDataType) {
			return true;
		}
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

