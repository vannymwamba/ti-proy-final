/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metasymbolic;

import java.util.Vector;

/**
 *
 * @author cliente preferido
 */
public class MSDefinition {
    public int index;
    public Vector<Integer> msContent;
    public Vector<Integer> msStructure;

    public MSDefinition(){
        index = 0;
        msContent = new Vector<Integer>();
        msStructure = new Vector<Integer>();
    }

    public MSDefinition(int ind, Vector<Integer> msC, Vector<Integer> msS){
        index = ind;
        msContent = msC;
        msStructure = msS;
    }
}
