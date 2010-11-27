/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compresorchebyshev;

/**
 *
 * @author Miguel Candia
 */
public class Chebyshev {

    public static void main(String[] args) {
        int N = 10;//Integer.parseInt(args[0]);
        Polynomial[] T  = new Polynomial[Math.max(2, N)];   // T[i] = ith Chebyshev polynomial
        T[0]            = new Polynomial(1, 0);             // 1
        T[1]            = new Polynomial(1, 1);             // x
        Polynomial twox = new Polynomial(2, 1);             // 2x

        // compute Chebyshev polynomials
        for (int n = 2; n < N; n++) {
            Polynomial temp1 = twox.times(T[n-1]);
            T[n] = temp1.minus(T[n-2]);
        }

        // print results
        for (int n = 0; n < N; n++)
            System.out.println(T[n]);
    }
}
