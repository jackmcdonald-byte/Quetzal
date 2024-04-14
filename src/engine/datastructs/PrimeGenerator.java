package engine.datastructs;

import static engine.utils.Constants.primes;

public class PrimeGenerator { //For later use with resizing hash tables and such
    /**
     * Generates a prime number that is greater than the key
     *
     * @param key Key to find a prime number greater than
     * @return Prime number greater than the key
     */
    public static long getClosestPrime(int key) {
        int low = 0;
        int high = primes.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = primes[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return ((long) mid << 32) | primes[mid];
            }
        }
        return ((long) low << 32) | primes[low];
    }

    /**
     * Generates a prime number that is greater than the key starting from a certain position
     *
     * @param key      Key to find a prime number greater than
     * @param startPos Starting position to search for a prime number
     * @return Prime number greater than the key
     */
    public static long getClosestPrime(int key, int startPos) {
        int i = Math.min(startPos, primes.length);
        for (; i < primes.length; i++) {
            if (primes[i] >= key) {
                return ((long) i << 32) | primes[i];
            }
        }
        return ((long) i << 32) | primes[i];
    }
}