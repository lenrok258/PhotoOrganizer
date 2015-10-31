package com.lofatsoftware.photoorganizer;

import com.lofatsoftware.photoorganizer.action.ProcessPhotosAction;
import com.lofatsoftware.photoorganizer.gui.MainFrame;

import javax.swing.*;
import java.util.Locale;


public class EntryPoint {

	public static void main( String[] args ) throws Exception {
        UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
        Locale.setDefault(new Locale("pl", "PL"));

        ProcessPhotosAction processPhotosService = new ProcessPhotosAction( );

        MainFrame mainFrame = new MainFrame( processPhotosService );
        mainFrame.start( );
    }
}
