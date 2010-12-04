/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compresorchebyshev;

/**
 *
 * @author emirhg
 */
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author emirhg
 */
public class MsgBox extends Dialog implements ActionListener {
    private Button ok,can;
    /**
     * Indica si el MsgBox contiene un boton OK
     */
    public boolean isOk = false;

    /*
     * @param frame   Parent frame
     * @param msg     Mensaje a desplegar
     * @param okcan   true : Botones "ok" y "cancel", false : Sólo boton "ok"
     */
    MsgBox(Frame frame, String msg, boolean okcan){
        super(frame, "Message", true);
        setLayout(new BorderLayout());
        add("Center",new Label(msg));
        addOKCancelPanel(okcan);
        createFrame();
        pack();
        setVisible(true);
    }

    /**
     * Constructor sin boton cancelar
     * @param frame Parent frame
     * @param msg Mensaje a desplegar
     */
    MsgBox(Frame frame, String msg){
        this(frame, msg, false);
    }

    /**
     * Agregar boton cancelar
     * @param okcan true: Agrega un boton para cancelar, false: sólo despliega el botón aceptar
     */
    void addOKCancelPanel( boolean okcan ) {
        Panel p = new Panel();
        p.setLayout(new FlowLayout());
        createOKButton( p );
        if (okcan == true)
            createCancelButton( p );
        add("South",p);
    }

    /**
     * Agrega un boton "OK", al panel
     * @param p Parent panel
     */
    void createOKButton(Panel p) {
        p.add(ok = new Button("OK"));
        ok.addActionListener(this);
    }

    /**
     * Agrega un boton "cancel" al panel
     * @param p Parent panel
     */
    void createCancelButton(Panel p) {
        p.add(can = new Button("Cancel"));
        can.addActionListener(this);
    }

    /**
     * Configura el frame contenedor
     */
    void createFrame() {
        Dimension d = getToolkit().getScreenSize();
        setLocation(d.width/3,d.height/3);
    }

    /**
     * Action listener. Cierra la ventana
     * @param ae Evento ocurrido
     */
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == ok) {
            isOk = true;
            setVisible(false);
        }
        else if(ae.getSource() == can) {
            setVisible(false);
        }
    }
    
}