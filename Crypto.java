import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.math.BigInteger;

public class Crypto {
	
	public static int[] ext_euclidean(int a, int b) {
		int[] result = new int [3];
		
		// base case to end recursion
		if (b == 0) {
			result[0] = a;
			result[1] = 1;
			result[2] = 0;
		}
		else {
			// mod division to get integer portion
			int q = a / b;
			// recursive call
			result = ext_euclidean(b, a % b);
			
			// determine u and v
			int temp = result[1] - result[2] * q;
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
		// i forgot to comment so as of right NOW, i don't remember how this works
		// but it does
		
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
		// we want to condense it into					[8, 9, 5]
		
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
		factors = Crypto.prime_factors(p - 1);
		
		// arrays to hold values to pass into chinese_remainder
		int[] values_arr = new int[factors.size()];
		int[] mods_arr = new int[factors.size()];
		
		// for each factor for p - 1 ... 
		for (int i = 0; i < factors.size(); i++) {
			// new_g = g ^ ((p-1)/factor) % p		per algorithm
			new_g = Crypto.fast_power(g, ((p - 1) / factors.get(i)), p);
			// new_h = h ^ ((p-1)/factor) % p		per alogrithm
			new_h = Crypto.fast_power(h, ((p - 1) / factors.get(i)), p);
			
			// use these smaller problems with shanks to determine new x's
			x = Crypto.shanks((long)new_g, (long)new_h, p);
			
			// add it to the arrays along with the factor which is the NEW mod to be pieced with CRT
			values_arr[i] = (int)x;
			mods_arr[i] = factors.get(i);
		}
		
		// use the smaller pieces with CRT to get final answer
		result = Crypto.chinese_remainder(factors.size(), values_arr, mods_arr);	
		return result;
	}
}
