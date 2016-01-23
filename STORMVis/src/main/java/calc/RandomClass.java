/**
 * auxiliary class for calculation of Poisson distributed numbers
 * @author Niels Schlusser
 * @date 20.10.2015
 */

package calc;

import java.util.Random;

public class RandomClass {

	/**
	 * constructor
	 */
	public RandomClass() {

	}
	
	
	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

	/**
	 * method creates Poissonian-distributed random number 
	 * @param lambda mean value of the distribution
	 * @return
	 */
	public static int poissonNumber(double lambda,Random rnd) {
		
		if (lambda>50){
			int retVal =(int) Math.ceil(Math.abs(Math.round(rnd.nextGaussian()*Math.sqrt(lambda)+lambda)));
			return retVal;
		}
	  double L = Math.exp(-lambda);
	  double p = 1.0;
	  int k = 0;
	
	  do {
	    k++;
	    p *= rnd.nextDouble();
	  } while (p > L);
	  return k - 1;
		}
	
	/**
	 * method returns a binomial distributed positive integer number, if the probability is
	 * @param maxNumber : number of tries
	 * @param probability : probability for requested events 
	 * @return
	 */
	public static int binomialNumber(int maxNumber, double probability) {
		int ret = 0;
		if (probability <= 100) {
			for (int i = 0; i < maxNumber; i++) {
				double r =  Math.random();
				if (r >= probability) ret++;
			}
			return ret;
		}
		else {
			return -1;
		}
	}
}
