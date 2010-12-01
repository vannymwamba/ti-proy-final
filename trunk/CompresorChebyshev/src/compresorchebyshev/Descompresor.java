/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compresorchebyshev;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguelcandia
 */
public class Descompresor {

    private int muestrasXBloque;
    private int FC;
    private int GP;
    private double[] X;

    public Descompresor(){

    }

    public Descompresor(int GP, int FC, int muestrasXBloque) {
        this.FC = FC;
        this.GP = GP;
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

    public byte[] descomprimirBloque(byte[] bloque) {
        byte[] resultado = new byte[muestrasXBloque * 4];
        double[] coefDer = new double[GP+1],coefIzq =  new double[GP+1];
        double[] muestrasDer =  new double[muestrasXBloque];
        double[] muestrasIzq =  new double[muestrasXBloque];
        double arccos;
        int i,j,k=0;

        for(i=0;i<muestrasXBloque;i++){
            arccos=Math.acos(X[i]);

            muestrasIzq[i]=coefDer[1]+coefDer[2]*X[i];
            for(j=3;j<GP+1;j++){
                muestrasIzq[i]+=coefDer[j]*Math.cos((j-1)*arccos);
            }muestrasDer[i]=coefDer[1]+coefDer[2]*X[i];
            for(j=3;j<GP+1;j++){
                muestrasDer[i]+=coefDer[j]*Math.cos((j-1)*arccos);
            }
            System.arraycopy(doubleToBytes(muestrasDer[i]), 0, resultado,k , 2);
            muestrasDer[i]=coefDer[1]+coefDer[2]*X[i];
            for(j=3;j<GP+1;j++){
                muestrasDer[i]+=coefDer[j]*Math.cos((j-1)*arccos);
            }
        }
        return resultado;
    }
    public byte[] doubleToBytes(double muestra){
        byte[] resultado = new byte[2];
        int mues = (int)muestra;
        mues=30000;
        String res =Integer.toBinaryString(mues);
        resultado[0] = (byte) (mues & 0xff);
        mues>>=8;
        resultado[1] = (byte) (mues & 0xff);
        String value="";
        try {
            value = new String(resultado, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Descompresor.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("int: "+res+" bytes: "+value);
        return resultado;
    }
}
