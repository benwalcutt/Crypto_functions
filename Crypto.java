import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.math.BigInteger;

public class Crypto {
	static long x1;
	static long y1;
	static long x2;
	static long y2;
	
	public static long[] ext_euclidean(long a, long b) {
		long[] result = new long [3];
		
		// base case to end recursion
		if (b == 0) {
			result[0] = a;
			result[1] = 1;
			result[2] = 0;
		}
		else {
			// mod division to get integer portion
			long q = a / b;
			// recursive call
			result = ext_euclidean(b, a % b);
			
			// determine u and v
			long temp = result[1] - result[2] * q;
			result[1] = result[2];
			result[2] = temp;
		}
		return result;
	}
	
	public static int fast_power(int base_in, int expon, int mod_in) {
		// algorithm from the book
		String expon_binary = "";
		BigInteger base = BigInteger.valueOf(0);
		BigInteger result = BigInteger.valueOf(1);
		BigInteger mod = BigInteger.valueOf(0);
		BigInteger output = BigInteger.valueOf(0);
		
		expon_binary = Integer.toBinaryString(expon);
		base = BigInteger.valueOf(base_in);
		mod = BigInteger.valueOf(mod_in);
		
		for (int i = expon_binary.length() - 1; i >= 0; i--) {
			if (expon_binary.charAt(i) == '1') {
				result = result.multiply(base);
				base = base.pow(2);
			}
			else {
				base = base.pow(2);
			}
		}

		output = result.mod(mod);
		return output.intValue();
	}
	
	public static int determine_order(int g, int p) {
		// loop until the pattern repeats; number of iterations is order
		int temp = 1;
		
		for (int i = 1; i < p; i++) {
			temp = (temp * g) % p;
			if (temp == 1) {
				return i;
			}
		}
		
		return (p - 1);
	}
	
	public static boolean is_root(int p, int a) {		
		int [] root_table = new int [p];
		BigInteger temp;
		BigInteger mod = BigInteger.valueOf(p);
		
		for (int i = 0; i < (p - 1); i++) {
			temp = BigInteger.valueOf(a);
			temp = temp.pow(i);
			temp = temp.mod(mod);
			root_table[temp.intValue()] = 1;
		}
			
		for (int i = 1; i < (p - 1); i++) {
			if (root_table[i] == 0) {
				return false;
			}
		}
		return true;
	} // end boolean loop
	
	public static long shanks(long g, long h, long p) {
		long big_n = 0;
		int n = (int)p - 1;		// order of g but it is assumed g is a generator so order is p - 1
		
		long[] g_list = new long[n + 1];
		long[] h_list = new long[n + 1];
		
		long g_inv = 0;
		long temp = 1;			// temp variable used throughout
		long x = 0;
		
		// determine the order
		big_n = (long)determine_order((int)g, (int)p);
		n = (int)Math.sqrt(big_n*1.0) + 1;	// modified n
		
		temp = 1;
		// need to determine what the inverse of g is
		// TODO: change to implement separate function
		for (int i = 1; i < p; i++) {
			temp = (i * g) % p;
			if (temp == 1) {
				g_inv = i;
				break;
			}
		}

		temp = (g_inv * g_inv) % p;
		// now we need to determine what g^(-1*n) is but do it so that we don't overflow
		for (int i = 2; i < n; i++) {
			temp = (temp * g_inv) % p;
		}
		
		g_inv = (long)temp;
		// generate the g list
		temp = 1;		// reset temp
		for (int i = 0; i <= n; i++) {
			temp = (temp * g) % p;
			g_list[i] = (long)temp;
		}
		
		// generate the h list
		temp = h;		// reset temp
		for (int i = 0; i <= n; i++) {
			temp = (temp * g_inv) % p;
			h_list[i] = (long)temp;
		}
		
		// compare lists
		for (int i = 0; i <= n; i++) {
			for (int j = 0; j <= n; j++) {
				if (g_list[i] == h_list[j]) {
					// from the algorithm x = i + jn
					// plus one used because of zero indexing
					x = (i + 1) + ((j+1) * n);
				}
			}
		}
		return (x);
	}
	
	public static int chinese_remainder(int num_of_sets, int[] values, int[] mods) {
		// variable names from the book so don't bother
		int a = values[0];
		int m = mods[0];
		int b;
		int n;
		int temp;
		
		int m_inv;
		
		// use algebra to find an equation and insert that equation into the next congruence
		for (int i = 1; i < (num_of_sets); i++) {
			b = values[i];
			n = mods[i];
			
			m_inv = find_inverse((m % n), n);
			temp = ((((b - a) + n) % n) * m_inv) % n;
			a = a + (m*temp);			
			m = m * n;
		}
		// after all congruences are calculated, return the result
		return a;
	}
	
	public static int find_inverse(int g, int p){
		// go along the line of i*g to find one that is congruent to 1 mod p
		for (int i = 1; i < p; i++) {
			if ((g*i) % p == 1) {
				return i;
			}
		}
		return 0;
	}
	
	public static long find_inverse_long(long g, long p){
		// go along the line of i*g to find one that is congruent to 1 mod p
		if (g < 0) {g += p;}
		
		for (long i = 1; i < p; i++) {
			if ((g*i) % p == 1) {
				return i;
			}
		}
		return 0;
	}
	
	// returns all factors, not condensed
	public static List<Integer> prime_factors_all(int number) {
		List<Integer> factors = new ArrayList<Integer>();
		List<Integer> new_factors = new ArrayList<Integer>();
		
		// determine primes that divide into the number
		for (int i = 2; i <= number; i++) {
			while (number % i == 0) {
				factors.add(i);
				number /= i;
			}
		}
		return factors;
	}
	
	public static List<Integer> prime_factors(int number) {
		List<Integer> factors = new ArrayList<Integer>();
		List<Integer> new_factors = new ArrayList<Integer>();
		
		// determine primes that divide into the number
		for (int i = 2; i <= number; i++) {
			while (number % i == 0) {
				factors.add(i);
				number /= i;
			}
		}
		
		// last loop returns a list of primes used i.e. [2, 2, 2, 3, 3, 5]
		// we want to condense it into			   ---> [8, 9, 5]
		
		// buffer for the end
		factors.add(0);
		int temp = factors.get(0);
		int temp2 = temp;
		
		// no need in doing this if there is one factor (primes)
		if (factors.size() == 1) {return factors;}
		
		// this loop compares values next to each other
		// if they are the same, multiply them together
		// if not, add that final number to new factors and move along
		for (int i = 1; i < factors.size(); i++) {			
			if (temp == factors.get(i)) {
				temp2 *= factors.get(i);
			}
			else {
				new_factors.add(temp2);
				temp = factors.get(i);
				temp2 = temp;
			}
		}
		
		return new_factors;
	}
	
	static long PohligHellman(int g, int h, int p) {
		// array list and various variables used
		List<Integer> factors = new ArrayList<Integer>();
		
		long new_g = 0;
		long new_h = 0;
		long x = 0;
		long big_n = 0;
		int result = 0;
		
		// determine factors first
		factors = prime_factors(p - 1);
		
		// arrays to hold values to pass into chinese_remainder
		int[] values_arr = new int[factors.size()];
		int[] mods_arr = new int[factors.size()];
		
		// for each factor for p - 1 ... 
		for (int i = 0; i < factors.size(); i++) {
			// new_g = g ^ ((p-1)/factor) % p		per algorithm
			new_g = fast_power(g, ((p - 1) / factors.get(i)), p);
			// new_h = h ^ ((p-1)/factor) % p		per alogrithm
			new_h = fast_power(h, ((p - 1) / factors.get(i)), p);
			
			// use these smaller problems with shanks to determine new x's
			x = shanks((long)new_g, (long)new_h, p);
			
			// add it to the arrays along with the factor which is the NEW mod to be pieced with CRT
			values_arr[i] = (int)x;
			mods_arr[i] = factors.get(i);
		}
		
		// use the smaller pieces with CRT to get final answer
		result = chinese_remainder(factors.size(), values_arr, mods_arr);	
		return result;
	}

	// This begins the Miller-Rabin test block which will be constructed using the "Clean Code" conventions
	// test for compositeness and returns list of witnesses
	static List<Integer> Miller_Rabin(long number) {
		// Uses a long because BigInteger is annoying
		List<Integer> witnesses = new ArrayList<Integer>();
		int power_of_a;
		int power_of_two;
		
		// determine if it is divisible by 2 first
		if (determineEveness(number)) {
			witnesses.add(2);
			return witnesses;
		}
		
		// factor out all the twos possible
		power_of_a = factor_out_twos(number - 1);
		power_of_two = factor_of_two(number - 1);
		
		// we're going to loop k times where k is 20, if we find 10 witnesses, we can go ahead and stop
		for (int k = 0; k < 25; k++) {
			// generate a random a
			int a = random_int(number - 1);
			int x = fast_power(a, power_of_a, (int)number);
			if (x == 1 || x == (number - 1)) {continue;}
			
			boolean a_test = determine_a_witness(x, power_of_two, (int)number);
			if (a_test) {witnesses.add(a);}
			if (witnesses.size() == 10) {break;}
		}
		return witnesses;
	}

	static boolean determineEveness(long number) {
		if (number % 2 == 0) {
			return true;
		}
		return false;
	}
	
	static int factor_out_twos(long number) {
		List<Integer> factors = new ArrayList<Integer>();
		int temp = 1;
		factors = prime_factors((int)number);
		if (factors.get(0) % 2 == 0) {
			factors.remove(0);
		}
		for (int i = 0; i < factors.size(); i++) {
			temp *= factors.get(i);
		}
		return temp;
	}
	
	static int factor_of_two(long number) {
		List<Integer> factors = new ArrayList<Integer>();
		factors = prime_factors((int)number);
		return factors.get(0);
	}
	
	static int random_int(long max) {
		Random rand = new Random();
		return (rand.nextInt((int)max + 1)  + 2);
	}
	
	static boolean determine_a_witness(int x, int power, int n) {
		for (int j = 0; j < power; j++) {
			x = fast_power(x, 2, n);
			if (x == (n - 1)) {return false;}
		}
		return true;
	}
	// End Miller- Rabin block

	static long Pollard(long number) {
		BigInteger a = BigInteger.valueOf(2);
		long[] Euclidean_return = new long[3];
		
		for (long i = 2; i < 100; i++) {
			a = a.modPow(BigInteger.valueOf(i), BigInteger.valueOf(number));
			Euclidean_return = ext_euclidean(a.longValue() - 1, number);
			if (Euclidean_return[0] != 1) {
				return Euclidean_return[0];				
			}			
		}
		return number;
	}
	
	static long Pollard_rho(long g, long h, long p) {
		// variables
		BigInteger p_ = BigInteger.valueOf(p);
		BigInteger p_1 = BigInteger.valueOf(p - 1);
		BigInteger g_ = BigInteger.valueOf(g);
		BigInteger h_ = BigInteger.valueOf(h);
		BigInteger first_third = BigInteger.valueOf(p / 3);
		BigInteger second_third = BigInteger.valueOf((p / 3) * 2);
		
		BigInteger x = BigInteger.valueOf(1);
		BigInteger y = BigInteger.valueOf(1);
		BigInteger alpha = BigInteger.valueOf(0);
		BigInteger beta = BigInteger.valueOf(0);
		BigInteger gamma = BigInteger.valueOf(0);
		BigInteger delta = BigInteger.valueOf(0);
		
		int counter = 1;
		
		do {
			counter +=1;
			if (x.compareTo(first_third) == -1) {
				x = x.multiply(g_);
				alpha = alpha.add(BigInteger.ONE);
			}
			else if (x.compareTo(first_third) == 1 && x.compareTo(second_third) == -1) {
				x = x.multiply(x);
				alpha = alpha.multiply(BigInteger.valueOf(2));
				beta = beta.multiply(BigInteger.valueOf(2));
			}
			else {
				x = x.multiply(BigInteger.valueOf(h));
				beta = beta.add(BigInteger.ONE);
			}
			
			x = x.mod(p_);
			alpha = alpha.mod(p_1);
			beta = beta.mod(p_1);
			
			if (y.compareTo(first_third) == -1) {
				y = y.multiply(g_);
				gamma = gamma.add(BigInteger.ONE);
			}
			else if (y.compareTo(first_third) == 1 && y.compareTo(second_third) == -1) {
				y = y.multiply(y);
				gamma = gamma.multiply(BigInteger.valueOf(2));
				delta = delta.multiply(BigInteger.valueOf(2));
			}
			else {
				y = y.multiply(BigInteger.valueOf(h));
				delta = delta.add(BigInteger.ONE);
			}
			
			y = y.mod(p_);
			gamma = gamma.mod(p_1);
			delta = delta.mod(p_1);
			
			if (y.compareTo(first_third) == -1) {
				y = y.multiply(g_);
				gamma = gamma.add(BigInteger.ONE);
			}
			else if (y.compareTo(first_third) == 1 && y.compareTo(second_third) == -1) {
				y = y.multiply(y);
				gamma = gamma.multiply(BigInteger.valueOf(2));
				delta = delta.multiply(BigInteger.valueOf(2));
			}
			else {
				y = y.multiply(BigInteger.valueOf(h));
				delta = delta.add(BigInteger.ONE);
			}
			
			y = y.mod(p_);
			gamma = gamma.mod(p_1);
			delta = delta.mod(p_1);			
			/*
			System.out.println(counter);
			System.out.println("x: " + x.toString());
			System.out.println("a: " + alpha.toString());
			System.out.println("b: " + beta.toString());
			System.out.println("y: " + y.toString());
			System.out.println("g: " + gamma.toString());
			System.out.println("d: " + delta.toString() + "\n");
			*/
			
		} while (x.compareTo(y) != 0);
		
		alpha = alpha.subtract(gamma).mod(p_1);
		beta = delta.subtract(beta).mod(p_1);
		long[] temp = new long[3];
		temp = ext_euclidean(beta.intValue(), p_1.intValue());
		
		int i;
		
		for (i = 0; i < p_1.intValue(); i++) {
			if (i * beta.intValue() % p_1.intValue() == temp[0]) {
				break;
			}
		}
		
		alpha = alpha.multiply(BigInteger.valueOf(i)).mod(p_1);
		alpha = alpha.divide(BigInteger.valueOf(temp[0]));
		p_1 = p_1.divide(BigInteger.valueOf(temp[0]));
		
		BigInteger result = BigInteger.valueOf(1);
		result = g_.pow(alpha.intValue()).mod(p_);
		
		while (result.compareTo(h_) != 0) {
			alpha = alpha.add(p_1);
			result = g_.pow(alpha.intValue()).mod(p_);
		}
		
		return alpha.longValue();
	}
	
	static long elliptic_factor(long n, long x, long y, int A) {
		long[] ans = new long[3];
		long result;
		x1 = x;
		y1 = y;
		
		x2 = x;
		y2 = y;
		
		do {
			result = add_points(n, A);
			if (result != 0) {
				if (result < 0) {
					ans = ext_euclidean(result + n, n);
					return ans[0];
				}
				else {
					ans = ext_euclidean(result, n);
					return ans[0];
				}
			}
		} while (true);
	}
	
	
	static long add_points(long n, int A) {
		long lambda = 0;
		long inverse = 0;
		
		
		if (x1 == 0 && y1 == 0) {
			x2 = x2;
			y2 = y2;
		}
		else if (x2 == 0 && y2 == 0) {
			x2 = x1;
			y2 = y1;
		}
		else if (x1 == x2 && y1 == (y2 * -1)) {
			x2 = 0;
			y2 = 0;
		}
		else {
			if (x1 == x2 && y1 == y2) {
				inverse = find_inverse_long((2*y1), n);
				if (inverse == 0) {return x2 - x1;}
				lambda = inverse * (x1 * x1 * 3 + A) % n;
			}
			else {
				inverse = find_inverse_long((x2 - x1), n);
				if (inverse == 0) {return x2 - x1;}
				lambda = (((y2 - y1 + n) % n) * inverse +n) % n;
			}
			
			x2 = ((lambda * lambda) % n - x1 - x2 + n) % n;
			y2 = ((lambda * ((x1 - x2 + n) % n)) % n - y1 + n) % n;
		}
		return 0;
	}
}
