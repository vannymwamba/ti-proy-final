/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compresorchebyshev;

/**
 * Clase coeficiente para representar un número en formato de punto flotante usando 3 bytes
 * @author emirhg
 */
public class Coeficiente {

    private byte[] value;
    private boolean underflow;
    private boolean overflow;
    
    /**
     * Contructor por default
     */
    public Coeficiente(){
        value = new byte[3];
    }

    /**
     * Inicializa al coeficiente con el valor indicado
     * @param byteArray
     * Arreglo de bytes codificado como punto flotante de 24 bits 1:4:19
     */
    public Coeficiente(byte[] byteArray){
        value = new byte[3];
        System.arraycopy(byteArray, 0, value, 0, 3);
    }

    /**
     * Inicializa el valor del Coeficiente
     * @param value
     * Valor hexadecimal en formto de punto flotante de 24 bits
     */
    public Coeficiente(long value){
        setValue(value);
    }

    /**
     * Inicializa el Coeficiente con valor decimal
     * @param value
     * Valor decimal del coeficiento
     */
    public Coeficiente(double value){

        setValue(value);
    }

    /**
     * Establece un valor para el coeficiente
     * @param value
     * Representacion hexadecimal del Coeficiente en foamto de punto flotante de 3 bytes
     */
    /**
     *
     * @param value
     */
    public final void setValue(long value){
        this.value = new byte[3];
        this.value[0] = (byte)((value & 0xFF0000) >>> 16);
        this.value[1] = (byte)((value & 0x00FF00) >>> 8);
        this.value[2] = (byte)(value & 0x0000FF);
    }

    /**
     * Establece un valor para el coeficiente
     * @param value
     * Representación decimal del coeficiente
     */
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

        exp = value > 1 ? (int)Math.floor(Math.log(value) / Math.log(2)) : (int)Math.floor(Math.log(value) / Math.log(2));

        if (exp >= -8 && exp <= 7){

            valExp = Math.pow(2, exp);
            double mantisa = (value - valExp)/valExp;
            setMantisa(mantisa);
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

    /**
     * Verifica el signo del coeficiente
     * @return
     *  True si el número es negativo, falso de otra forma
     */
    private boolean isNegative(){
        return (value[0] & 0x80) == 0x80;
    }

    /**
     * Establece el signo del número
     * @param neg
     *  True si el número es negativo, falso de otra forma
     */
    private void setNegative(boolean neg){
        if (neg)
            value[0] = (byte) (value[0] | 0x80);
        else
            value[0] = (byte) (value[0] & 0x7F);
    }

    /**
     * Obtiene el valor del exponente
     * @return
     * Valor del exponente entr -8 y 7
     */
    private int getExponent(){
        return ((value[0] & 0x78)>>> 3) - 8;
    }

    /**
     * Establece el valor del exponente
     * @param exp
     * Valor del exponente entre -8 y 7
     */
    private void setExponent(int exp){
        if (exp >= -8 && exp <=7){
            exp += 8;
            value[0] = (byte) ((value[0] & 0x87) | exp << 3);
        }
        else
            ;//value[0] = (byte) ((value[0] & 0x87) | 0xF << 3); //Arrojar una excepcion que indique que el exponente no es válido

    }

    /**
     * Devuelve el valor de la mantisa
     * @return
     * Un double con el valor de la mantisa
     */
    private double getMantisa(){
        int posVal;
        String binaryVal, aux;

        aux = Integer.toBinaryString(value[0] & 0x07);
        while (aux.length()<3)
            aux = "0" + aux;

        binaryVal = aux;
        aux =  Integer.toBinaryString(value[1] & 0xFF);
        while (aux.length()<8)
            aux = "0" + aux;
        binaryVal += aux;

        aux =  Integer.toBinaryString(value[2] & 0xFF);
        while (aux.length()<8)
            aux = "0" + aux;
        binaryVal += aux;

        int val = Integer.parseInt(binaryVal, 2);
        double mantiza = val * Math.pow(2, -19);

        return mantiza;
    }

    /**
     * Asigna un valor a la mantisa
     * @param mantisa
     * Double con el nuevo valor de la mantisa
     */

    public final void setMantisa(double mantisa){
        int binMantisa;
        double smallest = Math.pow(2, -19);
        if (mantisa >=0 && mantisa <= 1 - smallest){
            value [0] = (byte) (value[0] & 0xF8);
            value [1] = 0;
            value [2] = 0;

            binMantisa = (int) Math.round(mantisa / smallest);

            value [0] = (byte) (value[0] | ((binMantisa >>> 16) & 0x07));
            value [1] = (byte) ((binMantisa >>> 8) & 0xFF);
            value [2] = (byte) (binMantisa & 0xFF);

        }else
            System.err.println("La mantiza no tiene un valor válido");//Arroja excepción de mantiza inválida

    }

    /**
     * Obtiene la representación decimal del Coeficiente
     * @return
     * Double con el valor decimal
     */
    public double toDouble(){
        double dValue;
        
        dValue = Math.pow(2, getExponent()) * (1 + getMantisa());

        if (isNegative())
            dValue = -dValue;
        return dValue;
    }

    /**
     * Representación del Coeficiente como String
     * @return
     * Valor decimal del coeficiente como String
     */
    @Override
    public String toString(){
        return "" + toDouble();// + ":" + toHexString() + " Exponente: " + getExponent() + " Mantiza: " + getMantiza();
    }

    /**
     * Obtiene la codificación binaria del Coeficiete
     * @return
     * Un String con la representación binaria del número codificado en el coeficiente
     */
    public String toBinaryString(){
        String binaryVal, aux;

        aux = Integer.toBinaryString(value[0] & 0x07);
        while (aux.length()<3)
            aux = "0" + aux;

        binaryVal = aux;
        aux =  Integer.toBinaryString(value[1] & 0xFF);
        while (aux.length()<8)
            aux = "0" + aux;
        binaryVal += aux;

        aux =  Integer.toBinaryString(value[2] & 0xFF);
        while (aux.length()<8)
            aux = "0" + aux;
        binaryVal += aux;
        return binaryVal;
    }

    /**
     * Obtiene la representación en Hexadecimal del número codificado en el coeficiente
     * @return
     * String con la representación en Hexadecimal del número en punto flotante
     */
    public String toHexString(){
        return Integer.toHexString(((value[0] & 0xFF) << 16) | (value[1] & 0xFF) << 8 | value[2] & 0xFF);
    }

    /**
     * Cuando se asigna un valor menor al rango de valores del coeficiente se produce un underflow y el número se mapea al menor que puede almacenarse en un coeficiente
     * @return
     * True si el valor real es menor a la cota inferior
     */
    public boolean isUnderflow(){
        return underflow;
    }

    /**
     * Cuando se asigna un valor mayor al rango de valores del coeficiente se produce un underflow y el número se mapea al mayor que puede almacenarse en un coeficiente
     * @return
     * True si el valor real es mayor a la cota superior
     */
    public boolean isOverflow(){
        return overflow;
    }

    /**
     * Obtiene un arreglo de bytes que contiene la codficicación del coeficiente
     * @return
     * Un arreglo de 3 bytes con el número en punto flotante
     */
    public byte[] getAsByteArray(){
        return value;
    }
}
