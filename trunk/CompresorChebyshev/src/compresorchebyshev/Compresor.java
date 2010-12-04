/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compresorchebyshev;

import java.io.IOException;

/**
 * Clase con los métodos para realizar la compresión por aproximación polinomial.
 * Calcula los coeficientes que minimizan el error medio cuadrático a través del
 * uso de un spline natural que sirve para interpolar los valores de la señal en
 * los puntos de ortogonalidad.
 * @author Miguel Candia
 */
public class Compresor {

    private int GP;
    private int FE;
    private int muestrasXBloque;
    private Spline spl;

    /**
     * Instancia un nuevo objeto del tipo Compresor
     * @param GP Grado del polinomio
     * @param FC Factor de compresión
     * @param FE Factor de escala
     */
    public Compresor(int GP, int FC, int FE) {
        this.GP = GP;
        this.FE = FE;
        muestrasXBloque = (int) ((GP + 1) * FC * 3) / 2;//((24*(GP+1)*2)/8*FC)/4
        spl = new Spline(muestrasXBloque);
    }

    /**
     * Constructor por default
     * @deprecated
     * si muestrasXBloque no tiene un valor por defecto ocurre una excepción al ejecutar calcuarCoeficietnes()
     */
    public Compresor() {
        spl = new Spline();
    }

    /**
     * Calcula los valores de los coeficientes de Chebyshev y lo entrega en un arreglo para escribirlos al archivo de salida.
     * @param samples arreglo con los valores de las muestras de un canal
     * @return arreglo de coeficientes
     */
    public Coeficiente[] calcularCoeficientes(int[] samples) {
        Coeficiente[] result = new Coeficiente[GP + 1];
        double[] coef = new double[GP + 1];
        double[] Ybar, A;
        int i, j;
        Ybar = spl.calcularSpline(samples);
        A = spl.getA();
        coef[0] = Ybar[0];
        for (i = 1; i < muestrasXBloque; i++) {
            coef[0] = coef[0] + Ybar[i];
        }
        coef[0] = coef[0] / muestrasXBloque;

        result[0] = new Coeficiente(coef[0]);
        for (j = 1; j < (GP + 1); j++) {
            coef[j] = 0;
            for (i = 0; i < muestrasXBloque; i++) {
                coef[j] = coef[j] + Ybar[i] * Math.cos((j - 1) * A[i]);
            }
            coef[j] = coef[j] * 2 / muestrasXBloque;
            result[j] = new Coeficiente(coef[j]);
        }
        return result;
    }

    /**
     * Comprimir un bloque de datos a través de aproximación polinomial utilizando Chebyshev
     * @param arreglo bloque de bytes con las muestras izquierda y derecha intercaladas
     * @return arreglo de coeficientes de aproximación
     * @throws IOException
     */
    public Coeficiente[] comprimirBloque(byte[] arreglo) throws IOException {
        byte[] canalDer, canalIzq;
        int i, j, k;
        int[] muestrasDer, muestrasIzq;
        Coeficiente[] coefDer, coefIzq, resultado;
        //System.out.println(java.util.Arrays.toString(arreglo));
        canalDer = new byte[muestrasXBloque * 2];
        canalIzq = new byte[muestrasXBloque * 2];
        
         // Rellenamos los dos arreglos uno por cada canal e invertimos bytes para
         // poder convertir a Integer posteriormente
        i = 0;
        j = 0;
        while (i < arreglo.length - 2) {
            canalIzq[j] = arreglo[i + 1];
            canalIzq[j + 1] = arreglo[i];
            i+=2;
            canalDer[j] = arreglo[i + 1];
            canalDer[j + 1] = arreglo[i];
            i+=2;
            j+=2;
        }

        //Convertimos los bytes en Integer
        muestrasDer = new int[muestrasXBloque];
        muestrasIzq = new int[muestrasXBloque];
        j = 0;
        for (i = 0; i < canalDer.length; i = i + 2) {
            muestrasDer[j] = bytesAEnteroCompDos(canalDer[i], canalDer[i + 1])/FE;
            muestrasIzq[j] = bytesAEnteroCompDos(canalIzq[i], canalIzq[i + 1])/FE;
            j++;
        }
        //Obtener coeficientes compresor y spline para un bloque
        coefDer = calcularCoeficientes(muestrasDer);
        coefIzq = calcularCoeficientes(muestrasIzq);
        resultado = new Coeficiente[coefDer.length * 2];
        j = k = 0;
        for (i = 0; i < 2 * coefDer.length; i++) {
            if ((i + 1) % 2 == 1) {
                resultado[i] = coefIzq[j];
                j++;
            } else {
                resultado[i] = coefDer[k];
                k++;
            }
        }

        //System.out.println("Salida del Compresor: " + java.util.Arrays.toString(resultado));
        return resultado;
    }

    /**
     * Convierte 2 bytes en un entero complemento a 2
     * @param b0 primer byte
     * @param b1 segundo byte
     * @return entero
     */
    public int bytesAEnteroCompDos(byte b0, byte b1) {
  /*      int posVal;
        String binaryVal, aux;

        aux = Integer.toBinaryString(b0 & 0xFF);
        while (aux.length()<8)
            aux = "0" + aux;

        binaryVal = aux;
        aux =  Integer.toBinaryString(b1 & 0xFF);
        while (aux.length()<8)
            aux = "0" + aux;
        binaryVal += aux;

        int val = Integer.parseInt(binaryVal, 2);
*/
        
        int i = 0;
        i |= b0 & 0xFF;
        i <<= 8;
        i |= b1 & 0xFF;
        if (i > 32767) {
            i = i % 32768 - 32768;
        }
        return i;
        //return val;
    }

    /**
     * Regresa el número de muestras por bloque.
     * @return entero con el número de muestras por bloque
     */
    public int getMuestrasXBloque() {
        return muestrasXBloque;
    }
}
