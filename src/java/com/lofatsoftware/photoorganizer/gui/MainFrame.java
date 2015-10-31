package com.lofatsoftware.photoorganizer.gui;

import com.lofatsoftware.photoorganizer.action.Action;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static javax.swing.SpringLayout.*;

public class MainFrame {

    private JFrame frame = new JFrame( " ●▬ Photo organizer ▬● " );
    private SpringLayout layout = new SpringLayout( );
    private Container contentPane;
    private JButton chooseDirectoryButton;
    private JButton executeButton;
    private JTextArea textArea;
    private File chosenDirectory;
    private Action action;
    private ImageIcon loadingIcon;

    public MainFrame( Action action ) {
        this.action = action;
    }

    public void start( ) {
        frame.setBounds( 200, 200, 350, 450 );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setIconImage( new ImageIcon( getClass( ).getResource( "/icon.png" ) ).getImage( ) );

        contentPane = frame.getContentPane( );
        contentPane.setLayout( layout );

        createButtons( );
        configureChooseDirectoryButton( );
        configureExecuteButton( );

        frame.setVisible( true );
    }

    private void createButtons( ) {
        chooseDirectoryButton = new JButton( "Choose photo folder" );
        executeButton = new JButton( "Process images" );
        executeButton.setEnabled( false );

        textArea = new JTextArea( "Please choose folder to process by clicking on 'Choose photo folder' button" );
        textArea.setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
        textArea.setLineWrap( true );
        textArea.setWrapStyleWord( true );
        textArea.setFont( new Font( "Verdana", Font.PLAIN, 10 ) );
        DefaultCaret caret = (DefaultCaret) textArea.getCaret( );
        caret.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );
        JScrollPane textAreaScrollPane = new JScrollPane( textArea );

        contentPane.add( chooseDirectoryButton );
        contentPane.add( executeButton );
        contentPane.add( textAreaScrollPane );

        layout.putConstraint( WEST, chooseDirectoryButton, 10, WEST, contentPane );
        layout.putConstraint( NORTH, chooseDirectoryButton, 10, NORTH, contentPane );

        layout.putConstraint( WEST, executeButton, 10, EAST, chooseDirectoryButton );
        layout.putConstraint( NORTH, executeButton, 10, NORTH, contentPane );

        layout.putConstraint( WEST, textAreaScrollPane, 10, WEST, contentPane );
        layout.putConstraint( NORTH, textAreaScrollPane, 10, SOUTH, chooseDirectoryButton );
        layout.putConstraint( SOUTH, textAreaScrollPane, -10, SOUTH, contentPane );
        layout.putConstraint( EAST, textAreaScrollPane, -10, EAST, contentPane );

        loadingIcon = new ImageIcon( getClass( ).getResource( "/loading-icon.gif" ) );
    }

    private void configureChooseDirectoryButton( ) {
        final JFileChooser fileChooser = new JFileChooser( );
        fileChooser.setDialogTitle( "Choose directory with photos to process" );
        fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
        fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

        chooseDirectoryButton.addActionListener( new ActionListener( ) {
            public void actionPerformed( ActionEvent e ) {
                int returnVal = fileChooser.showOpenDialog( frame );
                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    chosenDirectory = fileChooser.getSelectedFile( );
                    textArea.setText( "Chosen directory:\n" + chosenDirectory.getAbsolutePath( ) );
                    textArea.append( "\nClick on 'Process images' " );
                    executeButton.setEnabled( true );
                }
            }
        } );
    }

    private void configureExecuteButton( ) {
        executeButton.addActionListener( new AbstractAction( ) {
            public void actionPerformed( ActionEvent e ) {
                executeButton.setIcon( loadingIcon );
                executeButton.setEnabled( false );

                Thread worker = new Thread( ) {
                    public void run( ) {
                        action.proceedClicked( textArea, chosenDirectory );
                        SwingUtilities.invokeLater( new Runnable( ) {
                            public void run( ) {
                                executeButton.setIcon( null );
                            }
                        } );
                    }
                };

                worker.start( );
            }
        } );
    }
}
