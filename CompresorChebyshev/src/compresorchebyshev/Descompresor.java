/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compresorchebyshev;

/**
 * Clase que reconstruye la señal a través de los polinomios de Chebyshev y genera
 * los bytes requeridos para guardar el nuevo archivo WAVE.
 * @author miguelcandia
 */
public class Descompresor {

    private int muestrasXBloque;
    private int GP;
    private int FE;
    private double[] X;

    /**
     * Constructor por defecto.
     * @deprecated es necesario iniciar los valores de los cuales depende el
     * tamaño de los arreglos para evitar excepciones
     */
    public Descompresor() {
    }

    /**
     * Genera una nueva instancia del objeto Descompresor. Se inicializa la variable
     * independiente para el cálculo de las muestras.
     * @param GP grado del polinomio
     * @param FE factor de escala
     * @param muestrasXBloque número de muestras por bloque
     */
    public Descompresor(int GP, int FE, int muestrasXBloque) {
        this.GP = GP;
        this.FE = FE;
        this.muestrasXBloque = muestrasXBloque;
        X = new double[muestrasXBloque];//arreglo de la variable independiente
        double step = (2 / ((double) muestrasXBloque - 1));
        int i;
        X[0] = -1;
        for (i = 1; i < X.length; i++) {
            X[i] = X[i - 1] + step;
        }
        X[muestrasXBloque - 1] = 1;
    }

    /**
     * Calcula los valores de las muestras de un bloque dado un arreglo de coeficientes.
     * @param bloque arreglo de coeficientes de un bloque, con ambos canales izquierdo y derecho
     * @return arreglo de bytes con la representación en bytes little endian de las muestras generadas
     */
    public byte[] descomprimirBloque(Coeficiente[] bloque) {

        byte[] resultado = new byte[muestrasXBloque * 2];
        double[] coef = new double[GP + 1];
        double[] muestras = new double[muestrasXBloque];
        double arccos;
        int i, j, k;

        //Llenar los arreglos de coeficientes
        j = k = 0;
        for (i = 0; i < bloque.length; i++) {
            if (bloque[i] != null) {
                coef[i] = bloque[i].toDouble();
            } else {
                coef[i] = 0;
            }
        }

        //Recuperar muestras
        k = 0;
        for (i = 0; i < muestrasXBloque; i++) {
            arccos = Math.acos(X[i]);
            muestras[i] = coef[1] + coef[2] * X[i];
            for (j = 3; j < GP + 1; j++) {
                muestras[i] += coef[j] * Math.cos((j - 1) * arccos);
            }
            muestras[i] = muestras[i] * FE;
            System.arraycopy(doubleToIntToBytes(muestras[i]), 0, resultado, k, 2);
            k += 2;
            /*muestrasDer[i] = coefDer[1] + coefDer[2] * X[i];
            for (j = 3; j < GP + 1; j++) {
                muestrasDer[i] += coefDer[j] * Math.cos((j - 1) * arccos);
            }
            muestrasDer[i] = muestrasDer[i] * FE;
            System.arraycopy(doubleToIntToBytes(muestrasDer[i]), 0, resultado, k, 2);
            k += 2;*/
        }
        return resultado;
    }

    /**
     * Método que convierte un double a entero para posteriormente guardarlo como
     * dos bytes que se grabarán en el archivo.
     * @param muestra double con el valor de la muestra calculado a partir de
     * los polinomios de Chebyshev
     * @return arreglo de 2 bytes con el entero en el formato requerido para el
     * archivo WAVE
     */
    public byte[] doubleToIntToBytes(double muestra) {
        byte[] resultado = new byte[2];
        int mues = (int) Math.round(muestra);
        if (mues > 32767) {
            mues = 32767;
        }
        if (mues < -32768) {
            mues = -32768;
        }

        resultado[0] = (byte) (mues & 0xFF);
        mues >>= 8;
        resultado[1] = (byte) (mues & 0xFF);
        if (muestra < 0) {
            resultado[0] = (byte) (resultado[0] | 0x80);
        } else {
            resultado[0] = (byte) (resultado[0] & 0x7F);
        }
        return resultado;
    }
}
