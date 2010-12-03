/*
 * CompresorChebyshevApp.java
 */
package compresorchebyshev;

import java.io.IOException;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class CompresorChebyshevApp extends SingleFrameApplication {

    static int underflow = 0, overflow = 0, total = 0;

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
        //Coeficiente test = new Coeficiente(19.125559536835784);

        //System.out.println("19.125559536835784 = " + test);


    }
    //Crear Compressor y ejecutar compresión

    public static void comprimir(String path, Object GP, Object FC, String FE) {
        try {
            FileManager file = new FileManager(path, false);
            Compressor compresor = new Compressor(Integer.parseInt(GP.toString()), Integer.parseInt(FC.toString()), Integer.parseInt(FE));
            int i;
            long numBloques;
            Coeficiente[] tempEscritura;
            underflow = 0;
            overflow = 0;
            total = 0;
            file.setBlockSize(compresor.getMuestrasXBloque() * 4);
            tempEscritura = new Coeficiente[compresor.getMuestrasXBloque()];
            numBloques = (file.getFileSize() - file.getHeader().length) / file.getBlockSize();

            String fOutName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")) + GP.toString() + "_" + FC.toString() + ".KL1";
            FileManager fOut = new FileManager(fOutName, true);

            fOut.appendData(FC.toString());
            fOut.appendData((byte) 0x0D);
            fOut.appendData(GP.toString());
            fOut.appendData((byte) 0x0D);
            fOut.appendData(FE.toString());
            fOut.appendData(file.getHeader());

            for (i = 0; i < numBloques; i++) {
                tempEscritura = compresor.comprimirBloque(file.getNextDataBlock());
                for (int j = 0; j < tempEscritura.length; j++) {
                    fOut.appendData(tempEscritura[j].getAsByteArray());
                    overflow += tempEscritura[j].isOverflow() ? 1 : 0;
                    underflow += tempEscritura[j].isUnderflow() ? 1 : 0;
                    total++;
                }
            }

            
        } catch (IOException e) {
            System.err.println("Error al comprimir el archivo");
            System.err.println(e.toString());
        }
    }

    public void descomprimir(String path) {
        //rellenar con métodos de FileManager y Descompresor!!
        FileManager fIn = new FileManager(path, false);
        int muestrasXBloque = (int) ((fIn.getDegree() + 1) * fIn.getCompresionFactor() * 3) / 2;
        Coeficiente[] aux = fIn.getNextCoeficientesBlock();

        Descompresor desc = new Descompresor((int) fIn.getDegree(), (int) fIn.getScaleFactor(), muestrasXBloque);
        String sux = "" + fIn.getDegree();
        String fOutName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("_") - sux.length()) + ".WAV";
        FileManager fOut = new FileManager(fOutName, true);
        fOut.appendData(fIn.getWavHeader());

        int j = 0;
        while (aux != null) {
            fOut.appendData(desc.descomprimirBloque(aux));
            j++;
            aux = fIn.getNextCoeficientesBlock();
        }
    }
}
