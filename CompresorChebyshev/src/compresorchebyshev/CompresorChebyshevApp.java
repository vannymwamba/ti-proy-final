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
    }
    //Crear Compressor y ejecutar compresión

    public static void comprimir(String path, Object GP, Object FC, String FE) {
        try {
            FileManager file = new FileManager(path, false);
            Compressor compresor = new Compressor(Integer.parseInt(GP.toString()), Integer.parseInt(FC.toString()),Double.parseDouble(FE));
            int i;
            long numBloques;
            Coeficiente[] tempEscritura;
            file.setBlockSize(compresor.getMuestrasXBloque() * 4);
            tempEscritura = new Coeficiente[compresor.getMuestrasXBloque()];
            numBloques = (file.getFileSize() - file.getHeader().length) / file.getBlockSize();

            FileManager fOut = new FileManager("salida.kl1", true);

            fOut.appendData(FC.toString());
            fOut.appendData((byte) 0x0D);
            fOut.appendData(GP.toString());
            fOut.appendData((byte) 0x0D);
            fOut.appendData("1");
            fOut.appendData(file.getHeader());

            System.err.println("Tamaño de la cabecera: " + file.getHeader().length);
            System.err.println("Posición: " + file.getCurrentPos());
            System.err.println("First Block Data: " + java.util.Arrays.toString(file.getCurrentDataBlock()));
            for (i = 0; i < numBloques; i++) {
                tempEscritura=compresor.comprimirBloque(file.getNextDataBlock());
                System.out.println("Bloque: " + (i+1) + " " + java.util.Arrays.toString(tempEscritura));
                for (int j=0; j < tempEscritura.length; j++)
                    fOut.appendData(tempEscritura[j].getAsByteArray());
            }


            System.err.println("===[ Compresión de todos los bloques finalizada: " + numBloques + " ]===");

        } catch (IOException e) {
            System.err.println("Error al comprimir el archivo");
            System.err.println(e.toString());
        }
    }

    public void descomprimir(String path) {
        //rellenar con métodos de FileManager y Descompresor!!
        FileManager fIn = new FileManager(path, false);
        int muestrasXBloque = (int) ((fIn.getDegree() + 1) * fIn.getCompresionFactor() * 3) / 2;
        byte[] res = new byte[muestrasXBloque * 4 * (int)fIn.getCompresionFactor()];
        Coeficiente[] aux = fIn.getNextCoeficientesBlock();
        Descompresor desc = new Descompresor((int) fIn.getDegree(), muestrasXBloque);
        int j = 0;
        while (aux != null) {
            //System.arraycopy(desc.descomprimirBloque(aux), 0, res, j, muestrasXBloque * 4);
            j += muestrasXBloque * 4;
            aux = fIn.getNextCoeficientesBlock();
        }
        System.out.println("Descomprimir");
        FileManager fOut = new FileManager("descomprimido.wav", true);

        fOut.appendData(fIn.getWavHeader());
        fOut.appendData(res);

        /*//código de prueba   muestrasXBloque = (int) ((GP + 1) * FC * 3) / 2;
        System.out.println("Descomprimir");
        Coeficiente[] alpha = new Coeficiente[2];
        Descompresor desc = new Descompresor(5, 72);
        byte[] res = new byte[4*72];
        res = desc.descomprimirBloque(alpha);
         */

    }
}
