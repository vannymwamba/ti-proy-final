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

    public static void comprimir(String path, Object GP, Object FC) {
        try {
            FileManager file = new FileManager(path, false);
            Compressor compresor = new Compressor(Integer.parseInt(GP.toString()), Integer.parseInt(FC.toString()), 0);
            int i;
            long numBloques;
            Coeficiente[] tempEscritura;
            file.setBlockSize(compresor.getMuestrasXBloque() * 4);
            tempEscritura= new Coeficiente[compresor.getMuestrasXBloque()];
            numBloques=(file.getFileSize() -44) / file.getBlockSize();

            FileManager fOut = new FileManager("salida.kl1", true);

            fOut.appendData(FC.toString());
            fOut.appendData((byte)0x0D);
            fOut.appendData(GP.toString());
            fOut.appendData((byte)0x0D);
            fOut.appendData("1");
            fOut.appendData(file.getHeader());

            for (i = 0; i < numBloques; i++) {
                tempEscritura=compresor.comprimirBloque(file.getNextDataBlock());
                for (int j=0; j < tempEscritura.length; j++)
                    fOut.appendData(tempEscritura[j].getAsByteArray());
            }

            System.out.println("Compresión de todos los bloques finalizada");

            // Código de prueba para recuperar el archivo comprimido
            FileManager fIn = new FileManager("salida.kl1", false);
            System.out.println("Grado: " + fIn.getDegree());
            System.out.println("Factor de comp: " + fIn.getCompresionFactor());
            System.out.println("Factor de escala: " + fIn.getScaleFactor());

            while (fIn.isNextDataBlock()){
                System.out.println("\n" + java.util.Arrays.toString(fIn.getNextCoeficientesBlock()));
            }
            
            //Fin del código de prueba

        } catch (IOException e) {
            System.err.println("Error al comprimir el archivo");
            System.err.println(e.toString());
        }
    }
    public void descomprimir(String path){
        Descompresor desc = new Descompresor();
        desc.doubleToBytes(3.0);
    }
}
