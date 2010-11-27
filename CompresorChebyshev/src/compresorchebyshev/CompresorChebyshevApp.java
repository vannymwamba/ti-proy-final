/*
 * CompresorChebyshevApp.java
 */
package compresorchebyshev;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class CompresorChebyshevApp extends SingleFrameApplication {
    private static int GP;
    private static int FC;
    private static int FE;
    private static int tamBloque;
    private static int numMuestras;
    private static int tamArchivoBytes;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new CompresorChebyshevView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of CompresorChebyshevApp
     */
    public static CompresorChebyshevApp getApplication() {
        return Application.getInstance(CompresorChebyshevApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) throws IOException {
        launch(CompresorChebyshevApp.class, args);
        comprimir();
    }

    public static void comprimir() throws IOException {
        File wav = new File("BRIEF.wav");
        byte[] arreglo = getBytesFromFile(wav);
        byte[] headerWAV = new byte[44], canalDer, canalIzq;
        int i, j;
        int[] muestrasDer,muestrasIzq;

        tamArchivoBytes=arreglo.length;
        for (i = 0; i < 44; i++) {
            headerWAV[i] = arreglo[i];
            //System.out.println(Integer.toHexString(headerWAV[i]));
        }
        //Obtenemos el número de muestras
        numMuestras = (arreglo.length - 44) / 4;
        //System.out.println(numMuestras);
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
         * COnvertimos los bytes en Integer
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


    }


    

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * Convierte 2 bytes en un entero
     * @param b0 primer byte
     * @param b1 segundo byte
     * @return entero
     */
    public static final int unsignedShortToInt(byte b0,byte b1) {
        int i = 0;
        i |= b0 & 0xFF;
        i <<= 8;
        i |= b1 & 0xFF;
        return i;
    }


}
