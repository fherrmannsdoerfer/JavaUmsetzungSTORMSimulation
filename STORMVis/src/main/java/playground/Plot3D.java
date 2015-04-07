package playground;

import gui.DataTypeDetector.DataType;

import java.util.ArrayList;
import java.util.List;

import model.DataSet;
import model.LineDataSet;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory.Toolkit;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class Plot3D {

	public List<DataSet> dataSets = new ArrayList<DataSet>();
	public Quality chartQuality;
	
	public Plot3D() {
		
	}
	
	
	public Chart createChart() {
		chartQuality = Quality.Advanced;
		Chart chart = AWTChartComponentFactory.chart(chartQuality, Toolkit.awt.name());
		
		for(DataSet set : dataSets) {
			if(set.dataType == DataType.LINES) {
				LineDataSet lines = (LineDataSet) set;
				Coord3d[] points = new Coord3d[lines.pointNumber.intValue()];
		        Color[] colors = new Color[lines.pointNumber.intValue()];
			}
		}
		
		chart.getView().setBackgroundColor(Color.BLACK);
        chart.getAxeLayout().setMainColor(Color.WHITE);
		chart.addMouseController();
		return chart;
	}
	
}
