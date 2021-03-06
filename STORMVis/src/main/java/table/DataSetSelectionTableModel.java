package table;

import gui.DataTypeDetector.DataType;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.DataSet;

public class DataSetSelectionTableModel extends AbstractTableModel {
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
	
	public boolean isCellEditable(int row, int col){
		return false;
	}
	
	public boolean isCellSelectable(int row, int col) {
		if(data.get(row).dataType == selectableDataType) {
			return true;
		}
		else {
			return false;
		}
	}	
	
}

