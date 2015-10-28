/**
 * auxiliary class for Spline calculation
 * @author Niels Schlusser
 * @date 24.10.2015
 */

package calc;

public class SplineCalculator {
	
	float[][] points; //calibration table[#calibrationPoints][3]
	
	float[][] splineX; //table[points.length-1][4] saves the sigX-Spline coeffs 
	float[][] splineY; //table[points.length-1][4] saves the sigY-Spline coeffs 
	
	
	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args) {
		float[][] a = {{0,3.2f,2},{1,5.3f,2},{2,6.9f,2},{3,0.9f,2},{4,2.1f,2}};
		SplineCalculator s = new SplineCalculator(a);
		s.splines();
		float[] r = s.getSig(3.5f);
		System.out.println("\n");
		System.out.println(r[0]);
		System.out.println(r[1]);
		
		
//		float[][] k = {{2.8f, 1, 0, 1, 1},{0, 1, 1, 0, 2},{0, 1, -1, 0, 3},{0, 0, 0, 19.5f, 4}};
//		float[] h = s.solveGauss(k);
//		for(int i = 0; i < h.length; i++) {
//			System.out.println(h[i]);
//		}
		
	}
	
	/**
	 * constructor for 
	 * @param pPoints : calibration file
	 */
	public SplineCalculator(float[][] pPoints) {
		points = pPoints;
		splineX = new float[points.length-1][4];
		splineY = new float[points.length-1][4];
	}
	
	/**
	 * calculates a vector of spline coefficients;
	 * degree of spline is dependent on the number of coefficients
	 */
	public void splines() {
		//sort calibration file
		SortClass s = new SortClass(points);
		s.quickSortA(0, points.length - 1);
		
		if(points.length >= 2) {
			// first step
			float z0 = points[0][0];

			float[][] in1 = { { 1, z0, (float) Math.pow(z0, 2), (float) Math.pow(z0, 3), points[0][1] },
					{ 1, points[1][0], (float) Math.pow(points[1][0], 2), (float) Math.pow(points[1][0], 3),
							points[1][1] },
					{ 0, 1, (float) 2 * z0, (float) ((float) 3 * Math.pow(z0, 2)), 0 }, //bdry. cond. f'=0
					{ 0, 0, 2, 6 * z0, 0 } }; //bdry. cond. f''=0
			float[][] in2 = { { 1, z0, (float) Math.pow(z0, 2), (float) Math.pow(z0, 3), points[1][2] },
					{ 1, points[1][0], (float) Math.pow(points[1][0], 2), (float) Math.pow(points[1][0], 3),
							points[1][2] },
					{ 0, 1, (float) 2 * z0, (float) ((float) 3 * Math.pow(z0, 2)),
							(float) (points[1][2] - points[0][2]) / (points[1][0] - points[0][0]) },
					{ 0, 0, 2, 6 * z0, 0 } };

			// writing results in solution vector
			for (int j = 0; j < 4; j++) {
				float[] tmpX = solveGauss(in1);
				float[] tmpY = solveGauss(in2);
				splineX[0][j] = tmpX[j];
				splineY[0][j] = tmpY[j];
			}

			// spline for sigmaX and sigmaY
			for (int i = 2; i <= splineX.length; i++) {
				// calculate spline-coeffs
				float pZ = points[i - 1][0]; // position on the left edge
				// input matrices for both of the splines
				float[][] inX = { { 1, pZ, (float) Math.pow(pZ, 2), (float) Math.pow(pZ, 3), points[i - 1][1] },
						{ 1, points[i][0], (float) Math.pow(points[i][0], 2), (float) Math.pow(points[i][0], 3),
								points[i][1] },
						{ 0, 1, (float) 2 * pZ, (float) ((float) 3 * Math.pow(pZ, 2)),
								(float) (splineX[i - 1][1] + 2 * splineX[i - 1][2] * pZ
										+ 3 * splineX[i - 1][3] * Math.pow(pZ, 2)) },
						{ 0, 0, 2, 6 * pZ, (float) (2 * splineX[i - 1][2] + 6 * splineX[i - 1][3] * pZ) } };
				float[][] inY = { { 1, pZ, (float) Math.pow(pZ, 2), (float) Math.pow(pZ, 3), points[i - 1][2] },
						{ 1, points[i][0], (float) Math.pow(points[i][0], 2), (float) Math.pow(points[i][0], 3),
								points[i][2] },
						{ 0, 1, (float) 2 * pZ, (float) ((float) 3 * Math.pow(pZ, 2)),
								(float) (splineY[i - 1][1] + 2 * splineY[i - 1][2] * pZ
										+ 3 * splineY[i - 1][3] * Math.pow(pZ, 2)) },
						{ 0, 0, 2, 6 * pZ, (float) (2 * splineY[i - 1][2] + 6 * splineY[i - 1][3] * pZ) } };

				// writing results in solution vector
				for (int j = 0; j < 4; j++) {
					float[] tmpX = solveGauss(inX);
					float[] tmpY = solveGauss(inY);
					splineX[i-1][j] = tmpX[j];
					splineY[i-1][j] = tmpY[j];
				}
			}
		
		}
		else {
			System.out.println("not enough data points for calibration");
		}	
		
	}
	
	/**
	 * method returns {sigX,sigY} for given z-value of the PSF
	 * @param z : z-coordinate
	 * @return array {sigX,sigY}
	 */
	public float[] getSig(float z) {
		float[] ret = new float[2];
		
		if(z > points[0][0] && z < points[points.length-1][0]) { //z in range?
			//find out index
			int ind = 0;
			while (ind < points.length - 1 && points[ind+1][0] < z) ind++; 
			
			// calculate sigmas out of spline coeff's
			for (int i = 0; i < splineX[0].length; i++) {
				ret[0] += splineX[ind][i]*Math.pow(z, i);
				ret[1] += splineY[ind][i]*Math.pow(z, i);
			}
			return ret;
		}
		else {
			System.out.println("z-value not in calibration range");
			return null;
		}
		
	}
	
	
	
	/**
	 * auxiliary method solves lin. system of eqn's by Gauss' algorithm
	 * @param lgs : linear system of equations
	 * @return solution vector
	 */
	private float[] solveGauss(float[][] lgs) {
		if(lgs.length + 1 == lgs[0].length) {
			for(int i = 0; i < lgs.length; i++) {
				//normalise line
				float a = lgs[i][i];
				for(int j = 0; j < lgs[i].length; j++) {
					lgs[i][j] /= a;
				}
				
				//forward elimination
				if(i != lgs.length - 1) {
					for(int j = i + 1; j < lgs.length; j++) {
							float h = lgs[j][i];
							for (int k = j - 1; k < lgs[j].length; k++) { //runs trough each line
								lgs[j][k] -= h*lgs[i][k];
							}
					}
				}
			}
			
			//backwards substitution
			for(int i = lgs.length - 1; i > 0; i--) {
				float res = lgs[i][lgs[i].length-1];
				for(int j = i - 1; j >= 0; j--) {
					float h = lgs[j][i];
					lgs[j][i] -= lgs[i][i]*h;
					lgs[j][lgs[i].length-1] -= res*h;
				}
			}
		
			//return solution vector
			float[] ret = new float[lgs.length];		
			for(int i = 0; i < lgs.length; i++) {
				ret[i] = lgs[i][lgs[0].length-1];
			}
			return ret;
		}
		else {
			return null;
		}	
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
