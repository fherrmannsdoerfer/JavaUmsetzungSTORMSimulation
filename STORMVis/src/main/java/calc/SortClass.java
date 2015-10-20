package calc;

import java.util.List;
import java.util.ArrayList;

public class SortClass {
	List<float[]> list;

	/**
	 * main method for testing issues
	 * @param args
	 */
	public static void main(String[] args) {
		float[] a = {1, 0, 0, 2, 9};
		float[] b = {7, 0, 0, 1, 10};
		float[] c = {0, 0, 1, 8, 70};
		float[] d = {1, 1, 2, 9, 6};
		float[] e = {5, 5, 5, 50, 7};
		List<float[]> li = new ArrayList<float[]>();
		li.add(a);
		li.add(b);
		li.add(c);
		li.add(d);
		li.add(e);
		SortClass h = new SortClass(li);
		h.quickSort(0, li.size() - 1);
		System.out.println(h.getVal(0, 3));
		System.out.println(h.getVal(1, 3));
		System.out.println(h.getVal(2, 3));
		System.out.println(h.getVal(3, 3));
		System.out.println(h.getVal(4, 3));
		System.out.println("\n");
		System.out.println(h.getVal(0, 0));
		System.out.println(h.getVal(1, 0));
		System.out.println(h.getVal(2, 0));
		System.out.println(h.getVal(3, 0));
		System.out.println(h.getVal(4, 0));
	}
	
	/**
	 * constructor constructs an object of the class
	 * @param pList : list to be sorted
	 */
	public SortClass(List<float[]> pList) {
		list = pList;
	}
	
	/**
	 * method for recursive quicksort algorithm
	 * @param lo : lower domain boundary index
	 * @param hi : upper domain boundary index
	 */
	public void quickSort(int lo, int hi) {
		int i = lo, j = hi;
		float x = list.get( (int) (lo + hi)/2)[3]; //initialise Pivot-element
		
		while (i<=j)
        {    
            while (list.get(i)[3] < x) i++; 
            while (list.get(j)[3] > x) j--;
            if (i<=j)
            {
                exchange(i, j);
                i++; j--;
            }
        }

        // recursion
        if (lo<j) quickSort(lo, j);
        if (i<hi) quickSort(i, hi);
		
		
	}
	
	/**
	 * 
	 * @return
	 */
	public List<float[]> getList () {
		return list;
	}
	
	/**
	 * auxiliary method for quicksort; swaps two elements of the list
	 * @param i : first list index
	 * @param j : second list index
	 */
	private void exchange(int i, int j) {
		float [] tempI = list.get(i);
		float [] tempJ = list.get(j);
		list.remove(i);
		list.add(i, tempJ);
		list.remove(j);
		list.add(j, tempI);
	}
	
	/**
	 * method returns required column value of required list element
	 */
	public float getVal(int e, int col) {
		return list.get(e)[col];
	}

}
