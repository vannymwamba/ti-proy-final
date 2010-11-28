/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compresorchebyshev;

import java.io.IOException;

/**
 *
 * @author Miguel Candia
 */
public class Compressor {

    private int GP;
    private int muestrasXBloque;
    private Spline spl;

    public Compressor(int GP, int mXB) {
        this.GP = GP;
        muestrasXBloque = mXB;
        spl = new Spline(muestrasXBloque);
    }

    /**
     * Default constructor
     */
    public Compressor() {
        spl = new Spline();
    }

    public double[] calcularCoeficientes(int[] samples, int GP, int mXB) {
        this.GP = GP;
        muestrasXBloque = mXB;
        spl.setpN(mXB);
        return calcularCoeficientes(samples);
    }

    public double[] calcularCoeficientes(int[] samples) {
        double[] coef = new double[GP+1], Ybar, A;
        int i, j;
        Ybar = spl.calcularSpline(samples);
        A = spl.getA();
        coef[0] = Ybar[0];
        for (i = 1; i < muestrasXBloque; i++) {
            coef[0] = coef[0] + Ybar[i];
        }
        coef[0] = coef[0] / muestrasXBloque;

        for (j = 1; j < (GP + 1); j++) {
            coef[j] = 0;
            for (i = 0; i < muestrasXBloque; i++) {
                coef[j] = coef[j] + Ybar[i] * Math.cos((j - 1) * A[i]);
            }
            coef[j] = coef[j] * 2 / muestrasXBloque;
        }
        return coef;
    }

    /**
     * Compress the byte block using Chebyshev polynomials
     * @param arreglo
     * @throws IOException
     */
    public void comprimir(byte[] arreglo) throws IOException {
        byte[] canalDer, canalIzq;
        int i, j;
        int[] muestrasDer, muestrasIzq;
        //Para debuggear
        int FC = 6;
        int GP=7;
        int numMuestras = (arreglo.length - 44) / 4;
        long tamArchivoOrig = arreglo.length;
        long tamArchivoComp = tamArchivoOrig / FC;
        long numBloques = tamArchivoComp / (FC * 6 * (GP + 1));
        muestrasXBloque = (int) (numMuestras / numBloques);
        int tamArchivoBytes = arreglo.length;
        
        //System.out.println(numMuestras);
        canalDer = new byte[numMuestras * 2];
        canalIzq = new byte[numMuestras * 2];
        /*
         * Rellenamos los dos arreglos uno por cada canal e invertimos bytes para
         * poder convertir a Integer posteriormente
         */
        i = 44;
        j = 0;
        while (i < arreglo.length - 2) {
            canalIzq[j] = arreglo[i + 1];
            canalIzq[j + 1] = arreglo[i];
            i++;
            canalDer[j] = arreglo[i + 1];
            canalDer[j + 1] = arreglo[i];
            i++;
            j++;
        }

        /*
         * Convertimos los bytes en Integer
         */
        muestrasDer = new int[numMuestras];
        muestrasIzq = new int[numMuestras];
        j = 0;
        for (i = 0; i < canalDer.length; i = i + 2) {
            muestrasDer[j] = unsignedShortToInt(canalDer[i], canalDer[i + 1]) - 32768;
            muestrasIzq[j] = unsignedShortToInt(canalIzq[i], canalIzq[i + 1]) - 32768;
            j++;
        }
        muestrasXBloque=72;
        //obtener coeficientes compresor y spline para un bloque
        int[] bloque = new int[muestrasXBloque];
        for(i=0;i<muestrasXBloque;i++){
            bloque[i]=muestrasDer[i];
        }
        //Compressor comp = new Compressor(GP, muestrasXBloque);
        double[] coef = calcularCoeficientes(bloque);
        for(i=0;i<coef.length;i++){
            System.out.println(coef[i]);
        }




        
        System.out.println("Compresion Finalizada");
    }

    /**
     * Convierte 2 bytes en un entero
     * @param b0 primer byte
     * @param b1 segundo byte
     * @return entero
     */
    public int unsignedShortToInt(byte b0, byte b1) {
        int i = 0;
        i |= b0 & 0xFF;
        i <<= 8;
        i |= b1 & 0xFF;
        return i;
    }
    /*
     * Calcula el spline
     * @param int pn numero de puntos
     * @param float[] Y vector de valores
     */



}
