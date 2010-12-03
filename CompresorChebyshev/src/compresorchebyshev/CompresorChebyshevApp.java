/*
 * CompresorChebyshevApp.java
 */
package compresorchebyshev;

import java.io.IOException;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * La clase principal de la aplicación.
 */
public class CompresorChebyshevApp extends SingleFrameApplication {

    static int underflow = 0, overflow = 0, total = 0;

    /**
     * Al iniciar crear y mostrar el marco principal de la aplicación.
     */
    @Override
    protected void startup() {
        show(new CompresorChebyshevView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * Método que regresa la instancia de la aplicación.
     * @return la instancia de CompresorChebyshevApp
     */
    public static CompresorChebyshevApp getApplication() {
        return Application.getInstance(CompresorChebyshevApp.class);
    }

    /**
     * Método principal que lanza la aplicación.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        launch(CompresorChebyshevApp.class, args);
        //Coeficiente test = new Coeficiente(19.125559536835784);

        //System.out.println("19.125559536835784 = " + test);


    }
    //Crear Compressor y ejecutar compresión

    /**
     * Método invocado para realizar la compresión. Utiliza un objeto de tipo FileManager para acceder al archivo de manera secuencial
     * y poder comprimir por partes un archivo. De igual manera, se guarda en un archivo a través de otro FileManager que adjunta los
     * bytes al final del archivo.
     * @param path dirección del archivo a comprimir
     * @param GP grado del polinomio
     * @param FC factor de compresión
     * @param FE factor de escala
     */
    public static void comprimir(String path, Object GP, Object FC, String FE) {
        try {
            FileManager file = new FileManager(path, false);
            Compresor compresor = new Compresor(Integer.parseInt(GP.toString()), Integer.parseInt(FC.toString()), Integer.parseInt(FE));
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

    /**
     * Método invocado para realizar la descompresión. Utiliza un objeto de tipo FileManager para acceder al archivo de manera secuencial
     * y poder descomprimir por bloques un archivo. De igual manera, se guarda en un archivo a través de otro FileManager que adjunta los
     * bytes al final del archivo.
     * @param path
     */
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
        System.err.println(" ===[ Número de bloques: " + j + " ] === ");
        System.err.println(" ===[ La descompresión ha finalizado ] === ");
    }
}
