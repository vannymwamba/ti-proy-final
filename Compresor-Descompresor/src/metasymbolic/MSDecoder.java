/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metasymbolic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author cliente preferido
 */
public class MSDecoder {
    private File aFile;
    //Variables for bit reading
    private int rb, lB, sl;
    //Variables for bit writing
    private int osb, nbos;
    //Variables for ms decoding
    private int symLen, msLen, nMs, offLen, gapLen;
    private Vector<Integer> msList; //list of metasymbols ocurrence
    private Vector<Integer> msPos; //list of metaymbols relative positions
    private Vector<MSDefinition> msDefs; //list of ms definitions

    public MSDecoder(){
        sl = 0;
        rb = 0;
        lB = 0;
        osb = 0;
        nbos = 0;
    }

    public MSDecoder(File aF){
        this();
        aFile = aF;
    }

    public File getAFile() {
        return aFile;
    }

    public void setAFile(File aFile) {
        this.aFile = aFile;
    }

    //Function for reading nB bits from fSt input stream
    private int readNBits(FileInputStream fSt, int nB) throws IOException {
        int resp;
        rb = 0;
        while (nB > sl && sl >= 0) {
            rb = fSt.read();
            if (rb >= 0) {
                if (rb > 255) {
                    System.out.println(rb);
                }
                //add a Integer to the end
                lB = (lB << 8) + rb;
                sl += 8;
            } else {
                sl = -1;
            }
        }
        if (sl < 0) {
            resp = lB;
            lB = -1;
            sl = 0;
        } else {
            //read nBits from the source
            resp = (lB >> (sl - nB));
            lB -= (resp << (sl - nB));
            sl -= nB;
        }
        return resp;
    }

    /*
     * La lectura del Header es correcta
     */
    //Read parameters in header
    private boolean readHeader(FileInputStream fSt) throws IOException{
        int c;
        //Reading First Nibble
        c = readNBits(fSt, 4);
        if(c >= 0){
            symLen = c + 1;

            c = readNBits(fSt, 4);
            if (c >= 0){
                msLen = c + 1;

                c = readNBits(fSt, 4);
                if (c >= 0){
                    offLen = c + 1;

                    c = readNBits(fSt, 4);
                    if (c >= 0){
                        gapLen = c + 1;
                    }
                }
            }
        }

        if (c < 0){
            System.out.println("Failed Reading Header");
        }

        System.out.println("Header 4 nibbles(dec): " + symLen + " " + msLen + " " + offLen + " " + gapLen);

        return c >= 0;
    }

    //Read indexes and positions for metasymbols
    //Returns the number of elements read
    private int readMSLocation(FileInputStream fSt) throws IOException{
        int c, m, t, off;

        msList = new Vector<Integer>();
        msPos = new Vector<Integer>();

        //Read ms index list
        nMs = 0;
        m = 0;
        c = readNBits(fSt, msLen);
        while(c > 0){
            msList.add(c);
            if(nMs < c){
                nMs = c;
            }
            m++;
            c = readNBits(fSt, msLen);
        }

        System.out.println("Indices: \t(" + msList.size() + ") " + msList.toString());
        if(m < 0){
            System.out.println("Failure while reading ms indexes");
        }else{
            //Read ms positions
            //t = (1 << offLen) - 1;
            t = (int) (Math.pow(2, offLen) - 1);
            off = 0;
            while(m > 0){
                c = readNBits(fSt, offLen);
                if(c < 0){
                    System.out.println("Error while reading ms positions");
                    m = 0;
                }else{
                    if(c < t){
                        off += c;
                        msPos.add(off);
                        off = 0;
                    }else{
                        off += t;
                        m++;
                    }
                }
                m--;
            }
        }

        System.out.println("Posiciones: \t(" +msPos.size() +") "+ msPos.toString());
        return msList.size();
    }
    /*
    private int readMSLocation_deprec(FileInputStream fSt) throws IOException{
        int c, m, t, off;

        msList = new Vector<Integer>();
        msPos = new Vector<Integer>();

        //Read ms index list
        nMs = 0;
        m = 0;
        c = readNBits(fSt, msLen);
        while(c > 0){
            msList.add(c);
            if(nMs < c){
                nMs = c;
            }
            m++;
            c = readNBits(fSt, msLen);
        }

        if(m < 0){
            System.out.println("Failure while reading ms indexes");
        }else{
            //Read ms positions
            t = (1 << offLen) - 1;
            off = 0;
            while(m > 0){
                c = readNBits(fSt, offLen);
                if(c < 0){
                    System.out.println("Error while reading ms positions");
                    m = 0;
                }else{
                    if(c < t){
                        off += c;
                        msPos.add(off);
                        off = 0;
                    }else{
                        off += t;
                        m++;
                    }
                }
                m--;
            }
        }

        return msList.size();
    }
*/
    private boolean readMSDefinitions(FileInputStream fSt) throws IOException{
        MSDefinition aDef;
        Vector<Integer> msC;
        Vector<Integer> msS;
        int i, c;
        msDefs = new Vector<MSDefinition>();
        msC = new Vector<Integer>();
        msS = new Vector<Integer>();

        //Read metasymbol structures (gaps)
        i = 0;
        c = 0;
        while(i < nMs && c >= 0){
            c = readNBits(fSt, gapLen);
            if(c < 0){
                System.out.println("Definition reading failed...");
            }else if (c == 0){
                //Metasymbol completed
                i++;
                aDef = new MSDefinition(i, msC, msS);

                //Add definition to the global collection
                msDefs.add(aDef);

                //Create new lists for next metasymbol
                msC = new Vector<Integer>();
                msS = new Vector<Integer>();
            }else {
                // minus 1?
                msS.add(c);
            }
        }

        //Read metasymbol contents
        if(c >= 0){
            for(i = 0; i < msDefs.size(); i++){
                for(int j = 0; j <= msDefs.get(i).msStructure.size(); j++){
                    c = readNBits(fSt, symLen);
                    msDefs.get(i).msContent.add(c);
                }
            }
            
        }
        
        for(i = 0; i < msDefs.size(); i++){
            System.out.println("[" + i + "]Estructura: "  + msDefs.get(i).msStructure.toString() + "\nContenido: " + msDefs.get(i).msContent.toString());
        }
        return c>=0;
    }

    private void writeNBits(FileOutputStream fOut, int btw, int n) throws IOException{
        //append bits to the temporal buffer
        osb = osb << n;
        osb = osb | btw;
        nbos += n;

        //if there are more than 8 bits in the queue write to the output file
        if(nbos > 7){
            int bs, aosb, i;
            aosb = osb >> (nbos % 8);
            osb = osb ^ (aosb << (nbos % 8));
            bs = 0;
            i = 0;
            while(nbos > 7){
                bs = bs << 8;
                bs = bs | (0x00FF & aosb);
                aosb = aosb >> 8;
                i++;
                nbos -= 8;
            }
            while(i > 0){
                fOut.write(bs & 0x00FF);
                bs = bs >> 8;
                i--;
            }
        }
    }

    private void flushQueue(FileOutputStream fOut) throws IOException{
        if (nbos > 0){
            osb = osb << (8 - nbos);
            fOut.write(osb);
        }
    }

    private boolean fileReconstruction(FileInputStream fSt, String outFileName){
        int i,fpos, fillSymb, sym;
        boolean resp = true;
        MSDecoThread msTh;
        MSDefinition aDef;
        Vector<MSDecoThread> outTh;

        try{
            //Create file stream to write
            FileOutputStream fOut = new FileOutputStream(outFileName);

            //Create threads
            outTh = new Vector<MSDecoThread>();
            fpos = 0;
            for (i = 0; i < msList.size(); i++){
                fpos += msPos.get(i);
                aDef = msDefs.get(msList.get(i) - 1);
                msTh = new MSDecoThread(fpos, aDef);
                outTh.add(msTh);
            }

            //Starts writing
            fillSymb = readNBits(fSt, symLen);
            fpos = 0;
            //Keep writing while there are fill symbols or threads
            while(fillSymb >= 0 || outTh.size() > 0){
                //Look if there is a metasymbol to write
                sym = -1;
                i = 0;
                while(i < outTh.size() && fpos >= outTh.get(i).firstEntry){
                    //System.out.println("threads: " + outTh.size());
                    if(outTh.get(i).nextEntry == fpos){
                        //If metasymbol matches the position then its used and
                        //its offset is updated
                        sym = outTh.get(i).nextContent();

                        //If the thread has no more symbols is then removed
                        if(outTh.get(i).hasEnded()){
                            outTh.remove(i);
                            i--;
                        }
                    }
                    i++;
                }

                //If no metasymbol matches the position then a fill symbol is used
                if(sym < 0){
                    sym = fillSymb;
                    fillSymb = readNBits(fSt, symLen);
                }

                //Finally we write the symbol
                writeNBits(fOut, sym, symLen);
                fpos++;
                //System.out.println("fpos = " + fpos + " sym = " + sym);
            }

            //Clean the output queue and close
            flushQueue(fOut);
            fOut.close();
        }catch(Exception ex){
            System.out.println("Reconstruction failed");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            resp = false;
        }

        return resp;
    }

    //Function for decoding the file asigned to the object
    public void decode(){
        try{
            //Open the file
            String str;
            FileInputStream fSt = new FileInputStream(aFile);

            str = aFile.getAbsolutePath();
            System.out.println("Reading file: " + str);
            str = str.substring(0, str.lastIndexOf("."));
            str += ".DMS";

            //Read headers
            if(readHeader(fSt)){
                //System.out.println("1/4 Header reading successful");
                if(readMSLocation(fSt) > 0){
                    //System.out.println("2/4 Location reading successful");
                    if(readMSDefinitions(fSt)){
                        //System.out.println("3/4 Definition reading successful");
                        if(fileReconstruction(fSt, str)){
                            System.out.println("Descompresión completa");
                        }
                        else{
                            System.out.println("No se pudo generar el archivo de salida");
                        }
                    }
                    else{
                        System.out.println("Error al leer el archivo");
                    }
                }
                 else{
                    System.out.println("Los indices son incorrectos");
                 }
            }else{
                System.out.println("No se pudo leer la cabecera del archivo");
            }

            fSt.close();
        }catch(Exception ex){
            System.out.println("Decoding failed");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    public void decode(File aFile){
        setAFile(aFile);
        decode();
    }

}
