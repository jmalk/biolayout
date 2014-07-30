/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.BioLayoutExpress3D.Files.webservice;

import java.awt.Desktop;
import java.awt.Font;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author dwright8
 */
public class ExcerptPane extends JEditorPane
{
    public ExcerptPane()
    {
        super(); 
        setEditable(false);
        setContentType("text/html");
        
        Font defaultFont = this.getFont();
        String fontFamily = defaultFont.getFamily();
        HTMLEditorKit hed = new HTMLEditorKit();
        StyleSheet ss = hed.getStyleSheet();
        ss.addRule("body {font-family : " + fontFamily + "}");
        ss.addRule("b {color : blue;}");
        ss.addRule(".hitHL {color : green; font-weight : bold}");
        Document doc = hed.createDefaultDocument();
        
        //Search hit excerpt
        setEditorKit(hed);
        setDocument(doc);
    }
}
