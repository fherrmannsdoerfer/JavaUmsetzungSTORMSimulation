/**
 * auxiliary class for Spline calculation
 * @author Niels Schlusser
 * @date 20.10.2015
 */

package calc;

public class SplineCalculator {
	
	float[][] points;
	
	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args) {
		float[][] a = {{0,1},{1,2},{2,3}};
		SplineCalculator s = new SplineCalculator(a);
		System.out.println(s.factorial(0));
		System.out.println(s.factorial(10));
		System.out.println(s.nOverK(1,0));
		System.out.println(s.nOverK(4,1));
		System.out.println(s.nOverK(4,2));
		System.out.println(s.nOverK(4,3));
		System.out.println(s.nOverK(4,4));
	}
	
	/**
	 * constructor for 
	 * @param pPoints
	 */
	public SplineCalculator(float[][] pPoints) {
		points = pPoints;
	}
	
	
	
	/**
	 * auxiliary method calculates the factorial of integer number n
	 * @param n :  integer number
	 * @return factorial
	 */
	private int factorial(int n) {
		if(n < 0) return -1;
		int ret = 1;
		while (n > 0) {
			ret *= n;
			n--;
		}
		return ret;
	}
	
	/**
	 * auxiliary method computes binomial coefficient of two numbers
	 * @param pN 
	 * @param pK
	 * @return
	 */
	private int nOverK(int pN, int pK) {
		int ret = factorial(pN)/factorial(pK);
		ret /= factorial(pN - pK);
		return ret;
	}
	
}
