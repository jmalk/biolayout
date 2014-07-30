/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.BioLayoutExpress3D.Files.webservice;

import cpath.service.jaxb.SearchHit;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author dwright8
 */
public class SearchHitsPanel extends JPanel{
    
    private static final Logger logger = Logger.getLogger(ImportWebService.class.getName());
    
    public SearchHitsPanel()
    {
        setLayout(new BorderLayout());
    }
    
 
}
