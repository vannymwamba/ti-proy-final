/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compresorchebyshev;

/**
 *
 * @author Miguel Candia
 */
public class Spline {

    public int pN;//muestras por bloque
    private double[] X;//variable independiente
    private double[] S;//coeficientes del spline
    private double[] A;//puntos de ortogonalidad o nodos
    private int[] Y;

    public Spline(int mXB) {
        pN = mXB;
        reiniciarVariables();
    }
    public Spline(){
        
    }
    public void reiniciarVariables(){
        X = new double[pN];//arreglo de la variable independiente
        double step = (2 / ((double)pN - 1));
        int i;
        X[0] = -1;
        for (i = 1; i < X.length; i++) {
            X[i] = X[i - 1] + step;
        }
        X[pN - 1] = 1;
        A=null;
        A=new double[pN];
        S=null;
        S=new double[pN];
        Y=null;
        Y=new int[pN];
    }

    public double[] calcularSpline(int[] Y) {
        double[] RHO = new double[pN], TAU = new double[pN],
                Xbar = new double[pN], Ybar = new double[pN];
        double Hi_1, Hi, TEMP, D;
        int i, iB;
        this.Y = Y;
        //argumentos de ortogonalidad
        for (i = 0; i < pN; i++) {
            iB = pN - i + 1;
            A[i] = (2 * iB - 1) * Math.PI / (2 * pN);
            Xbar[i] = Math.cos(A[i]);
        }
        //calcular coeficientes
        RHO[1] = 0;
        TAU[1] = 0;
        for (i = 1; i < pN - 1; i++) {
            Hi_1 = X[i] - X[i - 1];
            Hi = X[i + 1] - X[i];
            TEMP = (Hi_1 / Hi) * (RHO[i] + 2) + 2;
            RHO[i + 1] = -1 / TEMP;
            D = 6 * ((Y[i + 1] - Y[i]) / Hi - (Y[i] - Y[i - 1]) / Hi_1) / Hi;
            TAU[i + 1] = (D - Hi_1 * TAU[i] / Hi) / TEMP;
        }
        S[0] = 0;
        S[pN-1] = 0;
        for (i = 1; i < pN - 2; i++) {
            iB = pN - 1-i;
            S[iB] = RHO[iB + i] * S[iB + 1] + TAU[iB + 1];
        }
        //calcular valores del spline
        for (i = 0; i < pN; i++) {
            Ybar[i] = spline(Xbar[i]);
        }
        return Ybar;
    }

    private double spline(double pX) {
        double A, B, HI, SP;
        int i;
        i = 2;
        while (i < pN && pX > X[i]) {
            i++;
        }
        if (pX <= X[i]) {
            i = i - 1;
        }
        A = X[i + 1] - pX;
        B = pX - X[i];
        HI = X[i + 1] - X[i];
        SP = A * S[i] * (A * A / HI - HI) / 6 + B * S[i + 1] * (B * B / HI - HI) / 6 + (A * Y[i] + B * Y[i + 1]) / HI;
        return SP;
    }

    public double[] getA() {
        return A;
    }

    public void setpN(int pN) {
        this.pN = pN;
        reiniciarVariables();
    }

}
