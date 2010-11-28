/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compresorchebyshev;

/**
 * The coeficiente representation using 3 bytes and float point with XS8
 *
 * @author emirhg
 */
public class Coeficiente {

    /*
     * Default constructor
     */
    private byte[] value;

    public Coeficiente(){
        value = new byte[3];
    }

    public Coeficiente(byte[] byteArray){
        value = new byte[3];
        for (int i = 0; i < 3; i++)
            this.value[i] = value[i];
    }

    public Coeficiente (long value){
        this.value = new byte[3];
        this.value[0] = (byte)((value & 0xFF0000) >>> 16);
        this.value[1] = (byte)((value & 0x00FF00) >>> 8);
        this.value[2] = (byte)(value & 0x0000FF);
    }

    @Override
    public String toString(){
    
        return "" + toDouble();
    }

    private double toDouble(){
        boolean neg;
        int exp = 0;
        double mantiza = 0;
        double dValue;
        int posVal;
        int val = (((value[0] & 0x7F) << 16) | (value[1]<< 8) | value[2]);
        neg = (value[0] & 0x80) == 0x80;
        exp = (value[0] & 0x78)>>> 3;
        for (int i = 1; i <= 19; i++){
            posVal = (int) Math.pow(2, 19 - i);
            mantiza += (Math.pow(2 * Integer.bitCount(val & posVal),  i ) != 0 ? 1/Math.pow(2 * Integer.bitCount(val & posVal),  i ) : 0);
        }

        dValue = Math.pow(2, exp - 8) * (1 + mantiza);

        if (neg)
            dValue = -dValue;
        return dValue;

    }

}
