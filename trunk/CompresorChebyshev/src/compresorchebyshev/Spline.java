/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compresorchebyshev;

/**
 * Clase secundaria en el cálculo de los coeficientes de Chebyshev. Se utiliza
 * un spline natural para interpolar los valores de la señal entre las muestras
 * dadas.
 * @author Miguel Candia
 */
public class Spline {

    /**
     *
     */
    public int pN;//muestras por bloque
    private double[] X;//variable independiente
    private double[] S;//coeficientes del spline
    private double[] A;//puntos de ortogonalidad o nodos
    private int[] Y;

    /**
     * Crea una nueva instancia del objeto Spline con el número de muestras por
     * bloque e inicia todas las variables para el cálculo del Spline
     * @param mXB número de muestras por bloque
     */
    public Spline(int mXB) {
        pN = mXB;
        reiniciarVariables();
    }

    /**
     * Constructor por defecto.
     * @deprecated se requiere iniciar las variables utilizadas para evitar excepciones.
     */
    public Spline() {
    }

    /**
     * Método que inicializa las variables utilizadas para calcular el Spline natural.
     */
    private void reiniciarVariables() {
        X = new double[pN];//arreglo de la variable independiente
        double step = (2 / ((double) pN - 1));
        int i;
        X[0] = -1;
        for (i = 1; i < X.length; i++) {
            X[i] = X[i - 1] + step;
        }
        X[pN - 1] = 1;
        A = null;
        A = new double[pN];
        S = null;
        S = new double[pN];
        Y = null;
        Y = new int[pN];
    }

    /**
     * Calcula el Spline natural.
     * @param Y vector de valores
     * @return
     */
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
        S[pN - 1] = 0;
        for (i = 1; i < pN - 2; i++) {
            iB = pN - 1 - i;
            S[iB] = RHO[iB + i] * S[iB + 1] + TAU[iB + 1];
        }
        //calcular valores del spline
        for (i = 0; i < pN; i++) {
            Ybar[i] = spline(Xbar[i]);
        }
        return Ybar;
    }

    private double spline(double pX) {
        double a, B, HI, SP;
        int i;
        i = 2;
        while (i < pN && pX > X[i]) {
            i++;
        }
        if (pX <= X[i]) {
            i = i - 1;
        }
        a = X[i + 1] - pX;
        B = pX - X[i];
        HI = X[i + 1] - X[i];
        SP = a * S[i] * (a * a / HI - HI) / 6 + B * S[i + 1] * (B * B / HI - HI) / 6 + (a * Y[i] + B * Y[i + 1]) / HI;
        return SP;
    }

    /**
     * Regresa el arreglo con los puntos de ortogonalidad A.
     * @return arreglo con los puntos de ortogonalidad.
     */
    public double[] getA() {
        return A;
    }

    /**
     * Inicializar el tamaño del spline. Reinicia las variables del objeto pues dependen de este valor.
     * @param pN número de muestras para el cálculo del spline.
     */
    public void setpN(int pN) {
        this.pN = pN;
        reiniciarVariables();
    }
}
