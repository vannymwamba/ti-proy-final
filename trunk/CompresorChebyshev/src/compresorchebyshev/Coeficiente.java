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

    private byte[] value;
    private boolean underflow;
    private boolean overflow;
    
    /**
     * Default constructor
     */
    public Coeficiente(){
        value = new byte[3];
    }

    /**
     * Constructor that receives the byte array representation of the float point number
     * @param byteArray
     * @deprecated
     */
    public Coeficiente(byte[] byteArray){
        value = new byte[3];
        System.arraycopy(byteArray, 0, value, 0, 3);
    }

    /**
     * Constructor that receives the hexadecimal representation of the float number
     * @param value
     */
    public Coeficiente(long value){
        setValue(value);
    }

    /**
     * Constructor that receives the base 10 representation of the number
     * @param value
     */
    public Coeficiente(double value){

        setValue(value);
    }
    
    public final void setValue(long value){
        this.value = new byte[3];
        this.value[0] = (byte)((value & 0xFF0000) >>> 16);
        this.value[1] = (byte)((value & 0x00FF00) >>> 8);
        this.value[2] = (byte)(value & 0x0000FF);
    }

    public final void setValue(double value){
        this.value = new byte[3];
        double smallest;
        int exp;
        double valExp;

        //Enciende el bit del signo si es un número neggativo
        if (value < 0){
            setNegative(true);
            value = -value;
        }
        else{
            setNegative(false);
        }

        exp = value > 1 ? (int)Math.ceil(Math.log(value) / Math.log(2)) : (int)Math.floor(Math.log(value) / Math.log(2));

        if (exp >= -8 && exp <= 7){

            valExp = Math.pow(2, exp);
            setMantiza((value - valExp)/valExp);
            setExponent(exp);            
        }
        else{
            //System.err.println("Exponente: " + exp);
            if (exp >7){
                overflow = true;
                this.value[0] = (byte) (this.value[0] | 0x7F);
                this.value[1] = (byte) 0xFF;
                this.value[2] = (byte) 0xFF;
            }else{
                underflow = true;
                this.value[0] = (byte) (this.value[0] & 0x80);
                this.value[1] = (byte) 0x00;
                this.value[2] = (byte) 0x00;
            }

        }
    }

    /*
     * @return
     *  True if the number is negative, false otherwhise
     *
     */
    private boolean isNegative(){
        return (value[0] & 0x80) == 0x80;
    }

    /*
     * @param neg
     *  True if the numis wil be set as negative, false otherwhise
     */
    private void setNegative(boolean neg){
        if (neg)
            value[0] = (byte) (value[0] | 0x80);
        else
            value[0] = (byte) (value[0] & 0x7F);
    }


    private int getExponent(){
        return ((value[0] & 0x78)>>> 3) - 8;
    }

    private void setExponent(int exp){
        if (exp >= -8 && exp <=7){
            exp += 8;
            value[0] = (byte) ((value[0] & 0x87) | exp << 3);
        }
        else
            ;//value[0] = (byte) ((value[0] & 0x87) | 0xF << 3); //Arrojar una excepcion que indique que el exponente no es válido

    }

    private double getMantiza(){
        int posVal;
        double mantiza = 0;
        int val = (((value[0] & 0x7F) << 16) | (value[1]<< 8) | value[2]);
        for (int i = 1; i <= 19; i++){
            posVal = (int) Math.pow(2, 19 - i);
            mantiza += (Math.pow(2 * Integer.bitCount(val & posVal),  i ) != 0 ? 1/Math.pow(2 * Integer.bitCount(val & posVal),  i ) : 0);
        }

        return mantiza;
    }

    /**
     * Sets the mantiza a new value
     * @param mantiza
     * The decimal value for the mantiza
     */

    private String getBinaryString(double val){
        String result="";
        for (int i = 0; i < 19; i++){
            val = val *2;
            if (val>1){
                result += "1";
                val--;
            }else
                result += "0";

        }

        return result;
    }
    public final void setMantiza(double mantiza){
        int binMantiza;
        double smallest;
        if (mantiza >=0 && mantiza < 1){
            value [0] = (byte) (value[0] & 0xF8);
            value [1] = (byte) (value[1] & 0x00);
            value [2] = (byte) (value[2] & 0x00);

            smallest = Math.pow(2, -19);

            binMantiza = (int) (mantiza / smallest);

            value [0] = (byte) (value[0] | (binMantiza & 0x070000) >>> 16);
            value [1] = (byte) ((binMantiza & 0x00FF00) >>> 8);
            value [2] = (byte) (binMantiza & 0x0000FF);
        }else
            ;//Arroja excepción de mantiza inválida

    }

    public double toDouble(){
        double dValue;
        dValue = Math.pow(2, getExponent()) * (1 + getMantiza());

        if (isNegative())
            dValue = -dValue;
        return dValue;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString(){
        return "" + getBinaryString();
    }

    public String getBinaryString(){
        return Integer.toBinaryString(((value[0] & 0xFF) << 16) | (value[1] & 0xFF) << 8 | value[2] & 0xFF);
    }

    public boolean isUnderflow(){
        return underflow;
    }

    public boolean isOverflow(){
        return overflow;
    }

    public byte[] getAsByteArray(){
        return value;
    }
}
