/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metasymbolic;

import java.lang.Math;
/**
 *
 * @author emirhg
 */
public class BinaryParser {

    public BinaryParser(){

    }

    public byte[] binaryStringToCharArray(String binStr){
        String binStrArray[];
        int j = 0;
        int actBin;
        
        binStrArray = binStr.split("(?<=\\G.{8})");
        byte byteArray[] = new byte[binStrArray.length];

        for (int i = 0; i < binStrArray.length; i++){
            j = 0;
            byteArray[i] = 0;
            while (j < 8){
                actBin = Integer.parseInt("" + binStrArray[i].charAt(j));
                byteArray[i] = (byte)((int)byteArray[i] + (actBin * Math.pow(2,(7 - j))));
                j++;
            }
        }
        return byteArray;
    }

}
