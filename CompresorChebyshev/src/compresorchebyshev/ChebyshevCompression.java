/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compresorchebyshev;

import java.io.IOException;

/**
 *
 * @author emirhg
 */
public class ChebyshevCompression {
    private int numMuestras;
    private int tamArchivoBytes;

    //  Variables inutilizadas
/*  private int GP;
    private int FC;
    private int FE;
    private int tamBloque;
*/


    /**
     * Default constructor
     */
    public ChebyshevCompression(){

    }

    /**
     * Compress the byte block using Chebyshev polynomials
     * @param arreglo
     * @throws IOException
     */
    public void comprimir(byte[] arreglo) throws IOException {
        byte[] canalDer, canalIzq;
        int i, j;
        int[] muestrasDer,muestrasIzq;

        tamArchivoBytes=arreglo.length;
        
        //Obtenemos el número de muestras
        numMuestras = (arreglo.length - 44) / 4;
        canalDer = new byte[numMuestras*2];
        canalIzq = new byte[numMuestras*2];
        /*
         * Rellenamos los dos arreglos uno por cada canal e invertimos bytes para
         * poder convertir a Integer posteriormente
         */
        i = 44;
        j=0;
        while (i < arreglo.length-2) {
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
        muestrasDer=new int[numMuestras];
        muestrasIzq=new int[numMuestras];
        j=0;
        for(i=0;i<canalDer.length;i=i+2){
            muestrasDer[j]=unsignedShortToInt(canalDer[i], canalDer[i+1])-32768;
            System.out.println("Muestra Der "+j+": "+muestrasDer[j]+"\n"+Integer.toHexString(canalDer[i])+"-"+Integer.toHexString(canalDer[i+1]));
            muestrasIzq[j]=unsignedShortToInt(canalIzq[i], canalIzq[i+1])-32768;
            System.out.println("Muestra Izq "+j+": "+muestrasIzq[j]+"\n"+Integer.toHexString(canalIzq[i])+"-"+Integer.toHexString(canalIzq[i+1]));
            j++;
        }




        
        System.out.println("Compresion Finalizada");
    }

    /**
     * Convierte 2 bytes en un entero
     * @param b0 primer byte
     * @param b1 segundo byte
     * @return entero
     */
    public int unsignedShortToInt(byte b0,byte b1) {
        int i = 0;
        i |= b0 & 0xFF;
        i <<= 8;
        i |= b1 & 0xFF;
        return i;
    }

}
