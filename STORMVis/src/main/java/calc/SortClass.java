/**
 * auxiliary class for quicksort algorithm
 * @author Niels Schlusser
 * @date 20.10.2015
 */

package calc;

import java.util.List;
import java.util.ArrayList;

public class SortClass {
	
	List<float[]> list;
	
	float[][] arr;

	/**
	 * main method for testing issues
	 * @param args
	 */
	public static void main(String[] args) {
		float[][] a = {{1, 0, 0, 2, 9},{7, 0, 0, 1, 10},{0, 0, 1, 8, 70},{1, 1, 2, 9, 6},{5, 5, 5, 50, 7}};
		SortClass h = new SortClass(a);
		h.quickSortA(0, a.length - 1);
		System.out.println(h.getValA(0, 3));
		System.out.println(h.getValA(1, 3));
		System.out.println(h.getValA(2, 3));
		System.out.println(h.getValA(3, 3));
		System.out.println(h.getValA(4, 3));
		System.out.println("\n");
		System.out.println(h.getValA(0, 0));
		System.out.println(h.getValA(1, 0));
		System.out.println(h.getValA(2, 0));
		System.out.println(h.getValA(3, 0));
		System.out.println(h.getValA(4, 0));
	}
	
	/**
	 * constructor constructs an object of the class
	 * @param pList : list to be sorted
	 */
	public SortClass(List<float[]> pList) {
		list = pList;
	}
	
	/**
	 * constructor constructs an object of the class
	 * @param pList : array to be sorted
	 */
	public SortClass(float[][] pArr) {
		arr = pArr;
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
	 * method for recursive quicksort algorithm
	 * @param lo : lower domain boundary index
	 * @param hi : upper domain boundary index
	 */
	public void quickSortA(int lo, int hi) {
		int i = lo, j = hi;
		float x = arr[(int) (lo + hi)/2][0]; //initialise Pivot-element
		
		while (i<=j)
        {    
            while (arr[i][0] < x) i++; 
            while (arr[j][0] > x) j--;
            if (i<=j)
            {
                exchangeA(i, j);
                i++; j--;
            }
        }

        // recursion
        if (lo<j) quickSortA(lo, j);
        if (i<hi) quickSortA(i, hi);
		
		
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
	 * auxiliary method for quicksort; swaps two elements of an array
	 * @param i : first array index
	 * @param j : second array index
	 */
	private void exchangeA(int i, int j) {
		float [] tempI = new float[arr[0].length];
		for (int k = 0; k < arr[0].length; k++) {
			tempI[k] = arr[i][k];
		}
		for (int k = 0; k < arr[0].length; k++) {
			 arr[i][k] = arr[j][k];
		}
		for (int k = 0; k < arr[0].length; k++) {
			 arr[j][k] = tempI[k];
		}
	}
	
	/**
	 * method returns required column value of required list element
	 */
	public float getVal(int e, int col) {
		return list.get(e)[col];
	}
	
	/**
	 * method returns required value of the arrray
	 */
	public float getValA(int line, int col) {
		return arr[line][col];
	}

}

