package Main;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import Componentes.TextPrompt;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class QrcodeGenerator extends javax.swing.JFrame {

    private static final String ADDRESS_TEMPLATE
            = "BEGIN:VCARD\n"
            + "VERSION:3.0\n"
            + "N:{LN};{FN};\n"
            + "FN:{FN} {LN}\n"
            + "TITLE:{TITLE}/{COMPANYNAME}\n"
            + "TEL;TYPE=WORK;VOICE:{PHONE}\n"
            + "EMAIL;TYPE=WORK:{EMAIL}\n"
            + "ADR;TYPE=INTL,POSTAL,WORK:;;{STREET};{CITY};{STATE};{ZIP};{COUNTRY}\n"
            + "URL;TYPE=WORK:{WEBSITE}\n"
            + "END:VCARD";

    private BufferedImage image;
    private JFileChooser chooser1 = new JFileChooser();

    private class QRCodePanel extends javax.swing.JPanel {

        @Override
        protected void paintComponent(Graphics grphcs) {
            super.paintComponent(grphcs);
            if (image != null) {
                grphcs.drawImage(image, 0, 0, null);
            }
        }
    }

    private class FilterPNG extends javax.swing.filechooser.FileFilter {

        public String getDescription() {
            return "PNG files";
        }

        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String name = file.getName();
            name = name.toLowerCase();
            return name.endsWith(".png");
        }
    }

    private class TransferableImage implements Transferable {

        private final java.awt.Image image;

        public TransferableImage(java.awt.Image image) {
            this.image = image;
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.imageFlavor) && image != null) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = DataFlavor.imageFlavor;
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavor.equals(flavors[i])) {
                    return true;
                }
            }
            return false;
        }
    }

    public Color fondoQR = new Color(110, 153, 34);
    int rojo = 128, verde = 128, azul = 128;

    public QrcodeGenerator() {
        initComponents();
        TextPrompt nm = new TextPrompt(" Ingrese sitio web: ''https://www.ejemplo.com'' ", tSitioWeb);
        TextPrompt ww = new TextPrompt(" Ingrese número telefonico (+51) 9**-***-*** ", tNumTel);
        TextPrompt SS = new TextPrompt(" Ingrese número telefonico (+51) 9**-***-*** ", tNumTelMSJ);
        TextPrompt nn = new TextPrompt(" Nombre del contacto ", tNombre);
        TextPrompt aa = new TextPrompt(" Apellido del contacto ", tApellido);
        TextPrompt tt = new TextPrompt(" (+51) 9**-***-*** ", tTel);
        TextPrompt ee = new TextPrompt(" E-mail ejempo:'youmail@domain.com' ", tEmail);
        TextPrompt sw = new TextPrompt(" Sitio web: ''https://www.ejemplo.com''", tSW);
        TextPrompt cc = new TextPrompt(" Nombre de la empresa", tCom);
        TextPrompt ca = new TextPrompt(" Cargo responsable", tCar);
        TextPrompt cl = new TextPrompt(" Nombre de la calle", tCall);
        TextPrompt ci = new TextPrompt(" Nombre de la ciudad", tCiu);
        TextPrompt es = new TextPrompt(" Nombre del estado", tEsta);
        TextPrompt cd = new TextPrompt(" Código postal", tCod);
        TextPrompt pp = new TextPrompt(" Pais", tPais);
        mostrar();
        tSitioWeb.requestFocus();
        generateQrCode(tSitioWeb.getText());
    }

    public String Hex = "";

    public void cl() {
        Hex = Integer.toHexString(rojo) + Integer.toHexString(verde) + Integer.toHexString(azul);
        th.setText(Hex.toUpperCase());
        tr.setText("" + rojo);
        tg.setText("" + verde);
        tb.setText("" + azul);
    }

    public void Generar() {
        Color color = new Color(rojo, verde, azul);
        pColor.setBackground(color);
        pColor.setColorPrimario(color);
        fondoQR = color;
        cl();
        ver();
    }

    public void Convert() {
        String w = "#" + th.getText();
        int r = Integer.valueOf(w.substring(1, 3), 16);
        jSliderR.setValue(r);
        int g = Integer.valueOf(w.substring(3, 5), 16);
        jSliderG.setValue(g);
        int b = Integer.valueOf(w.substring(5, 7), 16);
        jSliderB.setValue(b);
    }

    public void cambiar() {
        if (rbPred.isSelected()) {
            pPredet.setVisible(true);
            fondoQR = new Color(110, 153, 34);
            ver();
        } else {
            pPredet.setVisible(false);
        }
        if (rbBlack.isSelected()) {
            pBlack.setVisible(true);
            fondoQR = new Color(51, 51, 51);
            ver();
        } else {
            pBlack.setVisible(false);
        }
        if (rbPer.isSelected()) {
            pPers.setVisible(true);
            Generar();
        } else {
            pPers.setVisible(false);
        }
    }

    public void ver() {
        if (pSW.isVisible()) {
            generateQrCode(tSitioWeb.getText());
        } else {
            if (pTXT.isVisible()) {
                String text = taTXT.getText();
                int length = text.length();
                jLabel2.setText("" + length + " character" + (length <= 1 ? "" : "s"));
                generateQrCode(text);
            } else {
                if (pNT.isVisible()) {
                    generateQrCode("tel:" + tNumTel.getText());
                } else {
                    if (pMSJ.isVisible()) {
                        String text2 = tMensajeNT.getText();
                        int length2 = text2.length();
                        jLabel6.setText("" + length2 + " character" + (length2 <= 1 ? "" : "s"));
                        generateQrCode("sms:" + tNumTelMSJ.getText() + ":" + text2);
                    } else {
                        if (pCT.isVisible()) {
                            contact();
                        }
                    }
                }
            }
        }
    }

    public void mostrar() {
        pSW.setVisible(true);
        pTXT.setVisible(false);
        pNT.setVisible(false);
        pMSJ.setVisible(false);
        pCT.setVisible(false);
        pPredet.setVisible(true);
        pBlack.setVisible(false);
        pPers.setVisible(false);
    }

    public void press(ActionEvent evento) {
        try {
            if (evento.getSource() == bURL) {
                bURL.setBackground(new Color(204, 204, 204));
                pSW.setVisible(true);
                tSitioWeb.requestFocus();
                ver();
                //generateQrCode(tSitioWeb.getText());
            } else {
                bURL.setBackground(new Color(255, 255, 255));
                pSW.setVisible(false);
            }
            if (evento.getSource() == bTXT) {
                bTXT.setBackground(new Color(204, 204, 204));
                pTXT.setVisible(true);
                taTXT.requestFocus();
                ver();
                /*
                String text = taTXT.getText();
                int length = text.length();
                jLabel2.setText("" + length + " character" + (length <= 1 ? "" : "s"));
                generateQrCode(text);
                 */
            } else {
                bTXT.setBackground(new Color(255, 255, 255));
                pTXT.setVisible(false);
            }
            if (evento.getSource() == bNT) {
                bNT.setBackground(new Color(204, 204, 204));
                pNT.setVisible(true);
                tNumTel.requestFocus();
                ver();
                //generateQrCode("tel:" + tNumTel.getText());
            } else {
                bNT.setBackground(new Color(255, 255, 255));
                pNT.setVisible(false);
            }
            if (evento.getSource() == bMSJ) {
                bMSJ.setBackground(new Color(204, 204, 204));
                pMSJ.setVisible(true);
                tNumTelMSJ.requestFocus();
                ver();
                /*
                String text2 = tMensajeNT.getText();
                int length2 = text2.length();
                jLabel6.setText("" + length2 + " character" + (length2 <= 1 ? "" : "s"));
                generateQrCode("sms:" + tNumTelMSJ.getText() + ":" + text2);
                 */
            } else {
                bMSJ.setBackground(new Color(255, 255, 255));
                pMSJ.setVisible(false);
            }
            if (evento.getSource() == bCT) {
                bCT.setBackground(new Color(204, 204, 204));
                pCT.setVisible(true);
                tNombre.requestFocus();
                ver();
                //contact();
            } else {
                bCT.setBackground(new Color(255, 255, 255));
                pCT.setVisible(false);
            }
        } catch (Exception e) {
        }
    }

    public void contact() {
        generateQrCode(ADDRESS_TEMPLATE.replace("{FN}", tNombre.getText())
                .replace("{LN}", tApellido.getText())
                .replace("{PHONE}", tTel.getText())
                .replace("{EMAIL}", tEmail.getText())
                .replace("{WEBSITE}", tSW.getText())
                .replace("{COMPANYNAME}", tCom.getText())
                .replace("{TITLE}", tCar.getText())
                .replace("{STREET}", tCall.getText())
                .replace("{CITY}", tCiu.getText())
                .replace("{STATE}", tEsta.getText())
                .replace("{ZIP}", tCod.getText())
                .replace("{COUNTRY}", tPais.getText())
        );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Colores_Fondo = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanelRound2 = new LIB.JPanelRound();
        jPanel2 = new QRCodePanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        button1 = new Componentes.Button();
        button2 = new Componentes.Button();
        rbPer = new javax.swing.JRadioButton();
        jLabel27 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        rbPred = new javax.swing.JRadioButton();
        rbBlack = new javax.swing.JRadioButton();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        pPers = new javax.swing.JPanel();
        jSliderB = new Componentes.JsliderCustom();
        jSliderR = new Componentes.JsliderCustom();
        jSliderG = new Componentes.JsliderCustom();
        jLabel31 = new javax.swing.JLabel();
        tb = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        pColor = new LIB.JPanelRound();
        jLabel28 = new javax.swing.JLabel();
        th = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        tr = new javax.swing.JLabel();
        tg = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        pPredet = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        pBlack = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jPanelRound1 = new LIB.JPanelRound();
        bNT = new Componentes.Button();
        bURL = new Componentes.Button();
        bCT = new Componentes.Button();
        bTXT = new Componentes.Button();
        bMSJ = new Componentes.Button();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        pCT = new javax.swing.JPanel();
        jPanelRound6 = new LIB.JPanelRound();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tNombre = new Componentes.TextFieldSuggestion();
        tApellido = new Componentes.TextFieldSuggestion();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tTel = new Componentes.TextFieldSuggestion();
        tEmail = new Componentes.TextFieldSuggestion();
        jLabel15 = new javax.swing.JLabel();
        tSW = new Componentes.TextFieldSuggestion();
        jLabel19 = new javax.swing.JLabel();
        jPanelRound7 = new LIB.JPanelRound();
        jLabel20 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        tCom = new Componentes.TextFieldSuggestion();
        jLabel17 = new javax.swing.JLabel();
        tCar = new Componentes.TextFieldSuggestion();
        jPanelRound8 = new LIB.JPanelRound();
        jLabel21 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tCall = new Componentes.TextFieldSuggestion();
        jLabel22 = new javax.swing.JLabel();
        tCiu = new Componentes.TextFieldSuggestion();
        jLabel23 = new javax.swing.JLabel();
        tEsta = new Componentes.TextFieldSuggestion();
        jLabel24 = new javax.swing.JLabel();
        tCod = new Componentes.TextFieldSuggestion();
        jLabel25 = new javax.swing.JLabel();
        tPais = new Componentes.TextFieldSuggestion();
        pSW = new LIB.JPanelRound();
        jLabel1 = new javax.swing.JLabel();
        tSitioWeb = new Componentes.TextFieldSuggestion();
        pTXT = new LIB.JPanelRound();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taTXT = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pNT = new LIB.JPanelRound();
        jLabel5 = new javax.swing.JLabel();
        tNumTel = new Componentes.TextFieldSuggestion();
        pMSJ = new LIB.JPanelRound();
        jLabel9 = new javax.swing.JLabel();
        tNumTelMSJ = new Componentes.TextFieldSuggestion();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tMensajeNT = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Bar = new javax.swing.JPanel();
        lblClose = new javax.swing.JLabel();
        lblMinimize = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(233, 238, 242));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 189, 150)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelRound2.setBackground(new java.awt.Color(255, 255, 255));
        jPanelRound2.setArch(10);
        jPanelRound2.setArcw(10);
        jPanelRound2.setOpaque(true);
        jPanelRound2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        jPanelRound2.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, 250, 250));

        jLabel26.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(51, 51, 51));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Generador QR");
        jPanelRound2.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 210, -1));

        jLabel14.setForeground(new java.awt.Color(152, 152, 152));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Formato: png");
        jPanelRound2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 335, 350, -1));

        button1.setBackground(new java.awt.Color(204, 204, 204));
        button1.setForeground(new java.awt.Color(51, 51, 51));
        button1.setText("COPIAR ");
        button1.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });
        jPanelRound2.add(button1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 300, 140, 25));

        button2.setBackground(new java.awt.Color(144, 199, 46));
        button2.setForeground(new java.awt.Color(255, 255, 255));
        button2.setText("GUARDAR QR");
        button2.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });
        jPanelRound2.add(button2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 300, 140, 25));

        Colores_Fondo.add(rbPer);
        rbPer.setForeground(new java.awt.Color(152, 152, 152));
        rbPer.setText("Personalizado");
        rbPer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rbPer.setOpaque(false);
        rbPer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPerActionPerformed(evt);
            }
        });
        jPanelRound2.add(rbPer, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 410, -1, -1));

        jLabel27.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(51, 51, 51));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Color de fondo");
        jPanelRound2.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 370, 90, 20));
        jPanelRound2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 380, 100, 10));
        jPanelRound2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 380, 100, 10));

        Colores_Fondo.add(rbPred);
        rbPred.setForeground(new java.awt.Color(152, 152, 152));
        rbPred.setSelected(true);
        rbPred.setText("Predeterminado");
        rbPred.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rbPred.setOpaque(false);
        rbPred.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbPredStateChanged(evt);
            }
        });
        rbPred.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPredActionPerformed(evt);
            }
        });
        jPanelRound2.add(rbPred, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 410, -1, -1));

        Colores_Fondo.add(rbBlack);
        rbBlack.setForeground(new java.awt.Color(152, 152, 152));
        rbBlack.setText("Black");
        rbBlack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rbBlack.setOpaque(false);
        rbBlack.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbBlackStateChanged(evt);
            }
        });
        rbBlack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbBlackActionPerformed(evt);
            }
        });
        jPanelRound2.add(rbBlack, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 410, 70, -1));

        jLayeredPane2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pPers.setBackground(new java.awt.Color(255, 255, 255));
        pPers.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSliderB.setBackground(new java.awt.Color(230, 229, 229));
        jSliderB.setForeground(new java.awt.Color(56, 145, 228));
        jSliderB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderBStateChanged(evt);
            }
        });
        pPers.add(jSliderB, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 120, -1, -1));

        jSliderR.setBackground(new java.awt.Color(230, 229, 229));
        jSliderR.setForeground(new java.awt.Color(56, 145, 228));
        jSliderR.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderRStateChanged(evt);
            }
        });
        pPers.add(jSliderR, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 60, -1, -1));

        jSliderG.setBackground(new java.awt.Color(230, 229, 229));
        jSliderG.setForeground(new java.awt.Color(56, 145, 228));
        jSliderG.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderGStateChanged(evt);
            }
        });
        pPers.add(jSliderG, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 90, -1, -1));

        jLabel31.setForeground(new java.awt.Color(153, 153, 153));
        jLabel31.setText("B");
        pPers.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 116, 10, -1));

        tb.setForeground(new java.awt.Color(153, 153, 153));
        tb.setText("000");
        pPers.add(tb, new org.netbeans.lib.awtextra.AbsoluteConstraints(275, 116, 30, -1));

        jLabel33.setForeground(new java.awt.Color(153, 153, 153));
        jLabel33.setText("G");
        pPers.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 86, 10, -1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pColor.setBackground(new java.awt.Color(51, 51, 51));
        pColor.setArch(5);
        pColor.setArcw(5);
        pColor.setOpaque(true);

        javax.swing.GroupLayout pColorLayout = new javax.swing.GroupLayout(pColor);
        pColor.setLayout(pColorLayout);
        pColorLayout.setHorizontalGroup(
            pColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        pColorLayout.setVerticalGroup(
            pColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jPanel4.add(pColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 8, 20, 20));

        jLabel28.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(51, 51, 51));
        jLabel28.setText("#");
        jPanel4.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(118, 10, 10, -1));

        th.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        th.setForeground(new java.awt.Color(51, 51, 51));
        th.setText("000000");
        jPanel4.add(th, new org.netbeans.lib.awtextra.AbsoluteConstraints(128, 10, 50, -1));

        jLabel39.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(152, 152, 152));
        jLabel39.setText("Valor hexadecimal:");
        jPanel4.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 110, -1));

        pPers.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 310, 35));

        jLabel34.setForeground(new java.awt.Color(153, 153, 153));
        jLabel34.setText("R");
        pPers.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 56, 10, -1));

        tr.setForeground(new java.awt.Color(153, 153, 153));
        tr.setText("000");
        pPers.add(tr, new org.netbeans.lib.awtextra.AbsoluteConstraints(275, 56, 30, -1));

        tg.setForeground(new java.awt.Color(153, 153, 153));
        tg.setText("000");
        pPers.add(tg, new org.netbeans.lib.awtextra.AbsoluteConstraints(275, 86, 30, -1));

        jLabel37.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(153, 153, 153));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("Personalizar fondo");
        pPers.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 130, 20));

        jLayeredPane2.add(pPers, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 200));

        pPredet.setBackground(new java.awt.Color(255, 255, 255));
        pPredet.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel40.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("Predeterminado");
        pPredet.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 140, 100, -1));

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/verde.png"))); // NOI18N
        pPredet.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 160, -1));

        jLabel41.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(102, 102, 102));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("Valor: ( 110, 153, 34 )");
        pPredet.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 280, -1));

        jLayeredPane2.add(pPredet, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 200));

        pBlack.setBackground(new java.awt.Color(255, 255, 255));
        pBlack.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel42.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 255, 255));
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("Black");
        pBlack.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 140, 100, -1));

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/black.png"))); // NOI18N
        pBlack.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 160, -1));

        jLabel44.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(102, 102, 102));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("Valor: ( 51, 51, 51 )");
        pBlack.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 280, -1));

        jLayeredPane2.add(pBlack, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 200));

        jPanelRound2.add(jLayeredPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 440, 330, 200));

        jPanel1.add(jPanelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 45, 370, 655));

        jPanelRound1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelRound1.setArch(10);
        jPanelRound1.setArcw(10);
        jPanelRound1.setOpaque(true);
        jPanelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bNT.setBackground(new java.awt.Color(255, 255, 255));
        bNT.setForeground(new java.awt.Color(51, 51, 51));
        bNT.setText("Número Telefonico");
        bNT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNTActionPerformed(evt);
            }
        });
        jPanelRound1.add(bNT, new org.netbeans.lib.awtextra.AbsoluteConstraints(182, 10, 160, 20));

        bURL.setBackground(new java.awt.Color(204, 204, 204));
        bURL.setForeground(new java.awt.Color(51, 51, 51));
        bURL.setText("URL");
        bURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bURLActionPerformed(evt);
            }
        });
        jPanelRound1.add(bURL, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 10, 80, 20));

        bCT.setBackground(new java.awt.Color(255, 255, 255));
        bCT.setForeground(new java.awt.Color(51, 51, 51));
        bCT.setText("Contacto");
        bCT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCTActionPerformed(evt);
            }
        });
        jPanelRound1.add(bCT, new org.netbeans.lib.awtextra.AbsoluteConstraints(436, 10, 90, 20));

        bTXT.setBackground(new java.awt.Color(255, 255, 255));
        bTXT.setForeground(new java.awt.Color(51, 51, 51));
        bTXT.setText("Texto");
        bTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bTXTActionPerformed(evt);
            }
        });
        jPanelRound1.add(bTXT, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 90, 20));

        bMSJ.setBackground(new java.awt.Color(255, 255, 255));
        bMSJ.setForeground(new java.awt.Color(51, 51, 51));
        bMSJ.setText("Mensaje");
        bMSJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bMSJActionPerformed(evt);
            }
        });
        jPanelRound1.add(bMSJ, new org.netbeans.lib.awtextra.AbsoluteConstraints(344, 10, 90, 20));

        jPanel1.add(jPanelRound1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 45, 535, 40));

        jLayeredPane1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pCT.setBackground(new java.awt.Color(233, 238, 242));
        pCT.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelRound6.setBackground(new java.awt.Color(255, 255, 255));
        jPanelRound6.setArch(10);
        jPanelRound6.setArcw(10);
        jPanelRound6.setOpaque(true);
        jPanelRound6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setForeground(new java.awt.Color(152, 152, 152));
        jLabel11.setText("Apellido:");
        jPanelRound6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 30, 60, -1));

        jLabel10.setForeground(new java.awt.Color(152, 152, 152));
        jLabel10.setText("Nombre:");
        jPanelRound6.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, -1));

        tNombre.setForeground(new java.awt.Color(51, 51, 51));
        tNombre.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tNombreKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tNombreKeyTyped(evt);
            }
        });
        jPanelRound6.add(tNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 210, -1));

        tApellido.setForeground(new java.awt.Color(51, 51, 51));
        tApellido.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tApellido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tApellidoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tApellidoKeyTyped(evt);
            }
        });
        jPanelRound6.add(tApellido, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, 265, -1));

        jLabel12.setForeground(new java.awt.Color(152, 152, 152));
        jLabel12.setText("Telefono:");
        jPanelRound6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 60, -1));

        jLabel13.setForeground(new java.awt.Color(152, 152, 152));
        jLabel13.setText("E-mail:");
        jPanelRound6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 100, 50, -1));

        tTel.setForeground(new java.awt.Color(51, 51, 51));
        tTel.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tTel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tTelKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tTelKeyTyped(evt);
            }
        });
        jPanelRound6.add(tTel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 210, -1));

        tEmail.setForeground(new java.awt.Color(51, 51, 51));
        tEmail.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tEmailKeyReleased(evt);
            }
        });
        jPanelRound6.add(tEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 120, 265, -1));

        jLabel15.setForeground(new java.awt.Color(152, 152, 152));
        jLabel15.setText("Sitio web:");
        jPanelRound6.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 70, 20));

        tSW.setForeground(new java.awt.Color(51, 51, 51));
        tSW.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tSW.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tSWKeyReleased(evt);
            }
        });
        jPanelRound6.add(tSW, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 515, -1));

        jLabel19.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setText("Datos del contacto");
        jPanelRound6.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 120, -1));

        pCT.add(jPanelRound6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 535, 240));

        jPanelRound7.setBackground(new java.awt.Color(255, 255, 255));
        jPanelRound7.setArch(10);
        jPanelRound7.setArcw(10);
        jPanelRound7.setOpaque(true);
        jPanelRound7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 51, 51));
        jLabel20.setText("Organización");
        jPanelRound7.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, -1));

        jLabel16.setForeground(new java.awt.Color(152, 152, 152));
        jLabel16.setText("Compañia:");
        jPanelRound7.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 70, 20));

        tCom.setForeground(new java.awt.Color(51, 51, 51));
        tCom.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tCom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tComKeyReleased(evt);
            }
        });
        jPanelRound7.add(tCom, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 240, -1));

        jLabel17.setForeground(new java.awt.Color(152, 152, 152));
        jLabel17.setText("Cargo:");
        jPanelRound7.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, 50, 20));

        tCar.setForeground(new java.awt.Color(51, 51, 51));
        tCar.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tCar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tCarKeyReleased(evt);
            }
        });
        jPanelRound7.add(tCar, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, 245, -1));

        pCT.add(jPanelRound7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 250, 535, 100));

        jPanelRound8.setBackground(new java.awt.Color(255, 255, 255));
        jPanelRound8.setArch(10);
        jPanelRound8.setArcw(10);
        jPanelRound8.setOpaque(true);
        jPanelRound8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(51, 51, 51));
        jLabel21.setText("Dirección");
        jPanelRound8.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, -1));

        jLabel18.setForeground(new java.awt.Color(152, 152, 152));
        jLabel18.setText("Calle:");
        jPanelRound8.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 70, 20));

        tCall.setForeground(new java.awt.Color(51, 51, 51));
        tCall.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tCall.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tCallKeyReleased(evt);
            }
        });
        jPanelRound8.add(tCall, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 240, -1));

        jLabel22.setForeground(new java.awt.Color(152, 152, 152));
        jLabel22.setText("Ciudad:");
        jPanelRound8.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, 60, 20));

        tCiu.setForeground(new java.awt.Color(51, 51, 51));
        tCiu.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tCiu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tCiuKeyReleased(evt);
            }
        });
        jPanelRound8.add(tCiu, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, 245, -1));

        jLabel23.setForeground(new java.awt.Color(152, 152, 152));
        jLabel23.setText("Estado:");
        jPanelRound8.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 70, 20));

        tEsta.setForeground(new java.awt.Color(51, 51, 51));
        tEsta.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tEsta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tEstaKeyReleased(evt);
            }
        });
        jPanelRound8.add(tEsta, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 240, -1));

        jLabel24.setForeground(new java.awt.Color(152, 152, 152));
        jLabel24.setText("Codigo postal:");
        jPanelRound8.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 100, 100, 20));

        tCod.setForeground(new java.awt.Color(51, 51, 51));
        tCod.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tCod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tCodKeyReleased(evt);
            }
        });
        jPanelRound8.add(tCod, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, 245, -1));

        jLabel25.setForeground(new java.awt.Color(152, 152, 152));
        jLabel25.setText("Pais:");
        jPanelRound8.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 70, 20));

        tPais.setForeground(new java.awt.Color(51, 51, 51));
        tPais.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tPais.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tPaisKeyReleased(evt);
            }
        });
        jPanelRound8.add(tPais, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 515, -1));

        pCT.add(jPanelRound8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 535, 240));

        jLayeredPane1.add(pCT, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 535, 600));

        pSW.setBackground(new java.awt.Color(255, 255, 255));
        pSW.setArch(10);
        pSW.setArcw(10);
        pSW.setOpaque(true);
        pSW.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setForeground(new java.awt.Color(152, 152, 152));
        jLabel1.setText("Sitio Web:");
        pSW.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 60, -1));

        tSitioWeb.setForeground(new java.awt.Color(51, 51, 51));
        tSitioWeb.setText("https://");
        tSitioWeb.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tSitioWeb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tSitioWebKeyReleased(evt);
            }
        });
        pSW.add(tSitioWeb, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 515, -1));

        jLayeredPane1.add(pSW, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 535, 85));

        pTXT.setBackground(new java.awt.Color(255, 255, 255));
        pTXT.setArch(10);
        pTXT.setArcw(10);
        pTXT.setOpaque(true);
        pTXT.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setForeground(new java.awt.Color(152, 152, 152));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("0 Carácteres");
        pTXT.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 220, 120, -1));

        taTXT.setColumns(20);
        taTXT.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        taTXT.setForeground(new java.awt.Color(51, 51, 51));
        taTXT.setRows(5);
        taTXT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                taTXTKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                taTXTKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(taTXT);

        pTXT.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 30, 505, 180));

        jLabel3.setForeground(new java.awt.Color(152, 152, 152));
        jLabel3.setText("Max. 100");
        pTXT.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 220, 70, -1));

        jLabel4.setForeground(new java.awt.Color(152, 152, 152));
        jLabel4.setText("Texto:");
        pTXT.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 10, 60, -1));

        jLayeredPane1.add(pTXT, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 535, 250));

        pNT.setBackground(new java.awt.Color(255, 255, 255));
        pNT.setArch(10);
        pNT.setArcw(10);
        pNT.setOpaque(true);
        pNT.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setForeground(new java.awt.Color(152, 152, 152));
        jLabel5.setText("Número de telefono:");
        pNT.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 120, -1));

        tNumTel.setForeground(new java.awt.Color(51, 51, 51));
        tNumTel.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tNumTel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tNumTelKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tNumTelKeyTyped(evt);
            }
        });
        pNT.add(tNumTel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 515, -1));

        jLayeredPane1.add(pNT, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 535, 85));

        pMSJ.setBackground(new java.awt.Color(255, 255, 255));
        pMSJ.setArch(10);
        pMSJ.setArcw(10);
        pMSJ.setOpaque(true);
        pMSJ.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setForeground(new java.awt.Color(152, 152, 152));
        jLabel9.setText("Número de telefono:");
        pMSJ.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 120, -1));

        tNumTelMSJ.setForeground(new java.awt.Color(51, 51, 51));
        tNumTelMSJ.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tNumTelMSJ.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tNumTelMSJKeyTyped(evt);
            }
        });
        pMSJ.add(tNumTelMSJ, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 515, -1));

        jLabel8.setForeground(new java.awt.Color(152, 152, 152));
        jLabel8.setText("Mensaje:");
        pMSJ.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 60, 20));

        tMensajeNT.setColumns(20);
        tMensajeNT.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        tMensajeNT.setForeground(new java.awt.Color(51, 51, 51));
        tMensajeNT.setRows(5);
        tMensajeNT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tMensajeNTKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(tMensajeNT);

        pMSJ.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 515, 180));

        jLabel7.setForeground(new java.awt.Color(152, 152, 152));
        jLabel7.setText("Max. 100");
        pMSJ.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 70, 20));

        jLabel6.setForeground(new java.awt.Color(152, 152, 152));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("0 Carácteres");
        pMSJ.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 300, 120, 20));

        jLayeredPane1.add(pMSJ, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 535, 330));

        jPanel1.add(jLayeredPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 95, 535, 600));

        Bar.setBackground(new java.awt.Color(144, 199, 46));
        Bar.setPreferredSize(new java.awt.Dimension(1051, 30));
        Bar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                BarMouseDragged(evt);
            }
        });
        Bar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BarMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                BarMousePressed(evt);
            }
        });

        lblClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/icons8_multiply_18px_1.png"))); // NOI18N
        lblClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblCloseMousePressed(evt);
            }
        });

        lblMinimize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/icons8_minus_18px_1.png"))); // NOI18N
        lblMinimize.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lblMinimize.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblMinimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblMinimizeMousePressed(evt);
            }
        });

        javax.swing.GroupLayout BarLayout = new javax.swing.GroupLayout(Bar);
        Bar.setLayout(BarLayout);
        BarLayout.setHorizontalGroup(
            BarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BarLayout.createSequentialGroup()
                .addContainerGap(917, Short.MAX_VALUE)
                .addComponent(lblMinimize)
                .addGap(3, 3, 3)
                .addComponent(lblClose)
                .addGap(4, 4, 4))
        );
        BarLayout.setVerticalGroup(
            BarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblClose, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
            .addComponent(lblMinimize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.add(Bar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 960, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 960, 720));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    int xy, xx;
    private void taTXTKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_taTXTKeyTyped
        if (taTXT.getText().length() >= 100) {
            evt.consume();
        }
    }//GEN-LAST:event_taTXTKeyTyped

    private void tMensajeNTKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tMensajeNTKeyTyped
        String text = tMensajeNT.getText();
        int length = text.length();
        jLabel6.setText("" + length + " character" + (length <= 1 ? "" : "s"));
        generateQrCode("sms:" + tNumTelMSJ.getText() + ":" + text);
    }//GEN-LAST:event_tMensajeNTKeyTyped

    private void bURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bURLActionPerformed
        press(evt);
    }//GEN-LAST:event_bURLActionPerformed

    private void bTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bTXTActionPerformed
        press(evt);
    }//GEN-LAST:event_bTXTActionPerformed

    private void bNTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNTActionPerformed
        press(evt);
    }//GEN-LAST:event_bNTActionPerformed

    private void bMSJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMSJActionPerformed
        press(evt);
    }//GEN-LAST:event_bMSJActionPerformed

    private void bCTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCTActionPerformed
        press(evt);
    }//GEN-LAST:event_bCTActionPerformed

    private void lblCloseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseMousePressed
        System.exit(0);
    }//GEN-LAST:event_lblCloseMousePressed

    private void lblMinimizeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinimizeMousePressed
        this.setState(QrcodeGenerator.ICONIFIED);
    }//GEN-LAST:event_lblMinimizeMousePressed

    private void BarMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BarMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xx, y - xy);
    }//GEN-LAST:event_BarMouseDragged

    private void BarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BarMouseClicked

    }//GEN-LAST:event_BarMouseClicked

    private void BarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BarMousePressed
        xx = evt.getX();
        xy = evt.getY();
    }//GEN-LAST:event_BarMousePressed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        if (chooser1.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String fileName = chooser1.getSelectedFile().getPath().replaceFirst("(.*?)(\\.\\w{3,4})*$", "$1.png");
        try {
            ImageIO.write(image, "png", new File(fileName));
        } catch (IOException ex) {
            Logger.getLogger(QrcodeGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button2ActionPerformed

    private void tSitioWebKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tSitioWebKeyReleased
        generateQrCode(tSitioWeb.getText());
    }//GEN-LAST:event_tSitioWebKeyReleased

    private void taTXTKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_taTXTKeyReleased
        String text = taTXT.getText();
        int length = text.length();
        jLabel2.setText("" + length + " character" + (length <= 1 ? "" : "s"));
        generateQrCode(text);
    }//GEN-LAST:event_taTXTKeyReleased

    private void tNumTelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tNumTelKeyReleased
        generateQrCode("tel:" + tNumTel.getText());
    }//GEN-LAST:event_tNumTelKeyReleased

    private void tNumTelMSJKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tNumTelMSJKeyTyped
        generateQrCode("sms:" + tNumTelMSJ.getText() + ":" + tMensajeNT.getText());
        int key = evt.getKeyChar();
        boolean numeros = key >= 48 && key <= 57;
        if (!numeros) {
            evt.consume();
        }
    }//GEN-LAST:event_tNumTelMSJKeyTyped

    private void tNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tNombreKeyReleased
        contact();
    }//GEN-LAST:event_tNombreKeyReleased

    private void tApellidoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tApellidoKeyReleased
        contact();
    }//GEN-LAST:event_tApellidoKeyReleased

    private void tTelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tTelKeyReleased
        contact();
    }//GEN-LAST:event_tTelKeyReleased

    private void tEmailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tEmailKeyReleased
        contact();
    }//GEN-LAST:event_tEmailKeyReleased

    private void tSWKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tSWKeyReleased
        contact();
    }//GEN-LAST:event_tSWKeyReleased

    private void tComKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tComKeyReleased
        contact();
    }//GEN-LAST:event_tComKeyReleased

    private void tCarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tCarKeyReleased
        contact();
    }//GEN-LAST:event_tCarKeyReleased

    private void tCallKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tCallKeyReleased
        contact();
    }//GEN-LAST:event_tCallKeyReleased

    private void tCiuKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tCiuKeyReleased
        contact();
    }//GEN-LAST:event_tCiuKeyReleased

    private void tEstaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tEstaKeyReleased
        contact();
    }//GEN-LAST:event_tEstaKeyReleased

    private void tCodKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tCodKeyReleased
        contact();
    }//GEN-LAST:event_tCodKeyReleased

    private void tPaisKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tPaisKeyReleased
        contact();
    }//GEN-LAST:event_tPaisKeyReleased

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        TransferableImage trans = new TransferableImage(image);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(trans, new ClipboardOwner() {
            public void lostOwnership(Clipboard clpbrd, Transferable t) {
            }
        });
    }//GEN-LAST:event_button1ActionPerformed

    private void rbPredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPredActionPerformed
        cambiar();
    }//GEN-LAST:event_rbPredActionPerformed

    private void rbBlackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbBlackActionPerformed
        cambiar();
    }//GEN-LAST:event_rbBlackActionPerformed

    private void rbPerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPerActionPerformed
        cambiar();
    }//GEN-LAST:event_rbPerActionPerformed

    private void tTelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tTelKeyTyped
        // SOLO NUMEROS 
        int key = evt.getKeyChar();
        boolean numeros = key >= 48 && key <= 57;
        if (!numeros) {
            evt.consume();
        }
    }//GEN-LAST:event_tTelKeyTyped

    private void tNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tNombreKeyTyped
        // Solo letras mayusculas y minusculas, incluye espacios
        int key = evt.getKeyChar();
        boolean mayusculas = key >= 65 && key <= 90;
        boolean minusculas = key >= 97 && key <= 122;
        boolean espacio = key == 32;
        if (!(minusculas || mayusculas || espacio)) {
            evt.consume();
        }
    }//GEN-LAST:event_tNombreKeyTyped

    private void tApellidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tApellidoKeyTyped
        int key = evt.getKeyChar();
        boolean mayusculas = key >= 65 && key <= 90;
        boolean minusculas = key >= 97 && key <= 122;
        boolean espacio = key == 32;
        if (!(minusculas || mayusculas || espacio)) {
            evt.consume();
        }
    }//GEN-LAST:event_tApellidoKeyTyped

    private void tNumTelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tNumTelKeyTyped
        int key = evt.getKeyChar();
        boolean numeros = key >= 48 && key <= 57;
        if (!numeros) {
            evt.consume();
        }
    }//GEN-LAST:event_tNumTelKeyTyped

    private void rbPredStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbPredStateChanged
//        cambiar();
    }//GEN-LAST:event_rbPredStateChanged

    private void rbBlackStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbBlackStateChanged
//        cambiar();
    }//GEN-LAST:event_rbBlackStateChanged

    private void jSliderRStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderRStateChanged
        rojo = jSliderR.getValue();
        tr.setText("" + rojo);
        Generar();
    }//GEN-LAST:event_jSliderRStateChanged

    private void jSliderGStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderGStateChanged
        verde = jSliderG.getValue();
        tg.setText("" + verde);
        Generar();
    }//GEN-LAST:event_jSliderGStateChanged

    private void jSliderBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderBStateChanged
        azul = jSliderB.getValue();
        tb.setText("" + azul);
        Generar();
    }//GEN-LAST:event_jSliderBStateChanged

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
 /*
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QrcodeGenerator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QrcodeGenerator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QrcodeGenerator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QrcodeGenerator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
         */
        FlatIntelliJLaf.registerCustomDefaultsSource("style");
        FlatIntelliJLaf.setup();
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QrcodeGenerator().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Bar;
    private javax.swing.ButtonGroup Colores_Fondo;
    private Componentes.Button bCT;
    private Componentes.Button bMSJ;
    private Componentes.Button bNT;
    private Componentes.Button bTXT;
    private Componentes.Button bURL;
    private Componentes.Button button1;
    private Componentes.Button button2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private LIB.JPanelRound jPanelRound1;
    private LIB.JPanelRound jPanelRound2;
    private LIB.JPanelRound jPanelRound6;
    private LIB.JPanelRound jPanelRound7;
    private LIB.JPanelRound jPanelRound8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private Componentes.JsliderCustom jSliderB;
    private Componentes.JsliderCustom jSliderG;
    private Componentes.JsliderCustom jSliderR;
    private javax.swing.JLabel lblClose;
    private javax.swing.JLabel lblMinimize;
    private javax.swing.JPanel pBlack;
    private javax.swing.JPanel pCT;
    private LIB.JPanelRound pColor;
    private LIB.JPanelRound pMSJ;
    private LIB.JPanelRound pNT;
    private javax.swing.JPanel pPers;
    private javax.swing.JPanel pPredet;
    private LIB.JPanelRound pSW;
    private LIB.JPanelRound pTXT;
    private javax.swing.JRadioButton rbBlack;
    private javax.swing.JRadioButton rbPer;
    private javax.swing.JRadioButton rbPred;
    private Componentes.TextFieldSuggestion tApellido;
    private Componentes.TextFieldSuggestion tCall;
    private Componentes.TextFieldSuggestion tCar;
    private Componentes.TextFieldSuggestion tCiu;
    private Componentes.TextFieldSuggestion tCod;
    private Componentes.TextFieldSuggestion tCom;
    private Componentes.TextFieldSuggestion tEmail;
    private Componentes.TextFieldSuggestion tEsta;
    private javax.swing.JTextArea tMensajeNT;
    private Componentes.TextFieldSuggestion tNombre;
    private Componentes.TextFieldSuggestion tNumTel;
    private Componentes.TextFieldSuggestion tNumTelMSJ;
    private Componentes.TextFieldSuggestion tPais;
    private Componentes.TextFieldSuggestion tSW;
    private Componentes.TextFieldSuggestion tSitioWeb;
    private Componentes.TextFieldSuggestion tTel;
    private javax.swing.JTextArea taTXT;
    private javax.swing.JLabel tb;
    private javax.swing.JLabel tg;
    private javax.swing.JLabel th;
    private javax.swing.JLabel tr;
    // End of variables declaration//GEN-END:variables

    private void generateQrCode(String messsage) {
        Logger.getLogger(QrcodeGenerator.class.getName()).log(Level.INFO, messsage);
        if (messsage == null || messsage.isEmpty()) {
            image = null;
            return;
        }
        try {
            Hashtable hintMap = new Hashtable();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(messsage,
                    BarcodeFormat.QR_CODE, jPanel2.getPreferredSize().width, jPanel2.getPreferredSize().height, hintMap);
            int CrunchifyWidth = byteMatrix.getWidth();
            image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,
                    BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
            graphics.setColor(fondoQR);
            for (int i = 0; i < CrunchifyWidth; i++) {
                for (int j = 0; j < CrunchifyWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            jPanel2.repaint();
        } catch (WriterException ex) {
            Logger.getLogger(QrcodeGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
