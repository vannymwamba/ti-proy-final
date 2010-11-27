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
public class MSDecoThread {
    public int firstEntry;
    public int nextEntry;
    private int thPos;
    public MSDefinition msDef;

    public MSDecoThread(int fE, MSDefinition msD){
        firstEntry = fE;
        nextEntry = fE;
        thPos = 0;
        msDef = msD;
    }

    public boolean hasEnded(){
        return thPos >= msDef.msContent.size();
    }

    public int nextContent(){
        thPos++;
        if(!hasEnded()){
            nextEntry += msDef.msStructure.get(thPos - 1);
        }
        return msDef.msContent.get(thPos - 1);
    }
}
