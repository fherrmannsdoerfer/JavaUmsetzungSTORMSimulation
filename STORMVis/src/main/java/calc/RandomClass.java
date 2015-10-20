package calc;

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
		System.out.println(poissonNumber(20));
	}

	/**
	 * method creates Poissonian-distributed random number 
	 * @param lambda mean value of the distribution
	 * @return
	 */
	public static int poissonNumber(double lambda) {
		  double L = Math.exp(-lambda);
		  double p = 1.0;
		  int k = 0;

		  do {
		    k++;
		    p *= Math.random();
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
