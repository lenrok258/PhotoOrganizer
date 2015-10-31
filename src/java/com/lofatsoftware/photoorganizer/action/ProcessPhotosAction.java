package com.lofatsoftware.photoorganizer.action;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProcessPhotosAction implements Action {

    @Override
    public void proceedClicked( JTextArea textArea, File chosenDirectory ) {
        textArea.append( "\n\nPhotos processing started" );
        File[] files = chosenDirectory.listFiles( );
        for ( File file : files ) {
            if ( !file.isFile( ) ) {
                continue;
            }
            try {
                processFile( file, textArea );
            } catch ( IOException | ParseException exception ) {
                textArea.append( "\n[ERROR]:\n" );
                textArea.append( ExceptionUtils.getStackTrace( exception ) );
            } catch ( ImageReadException exception ) {
                textArea.append( "\n[WARNING] Cannot process file " + file.getName( ) );
            }
        }
        textArea.append( "\n\nPhotos processing successfully finished" );
    }

    private void processFile( File file, JTextArea textArea ) throws IOException, ImageReadException, ParseException {
        IImageMetadata metadata = Sanselan.getMetadata( file );
        if ( !( metadata instanceof JpegImageMetadata ) ) {
            return;
        }
        JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
        TiffField createDateString = jpegMetadata.findEXIFValue( TiffConstants.EXIF_TAG_CREATE_DATE );
        DateFormat formatter = new SimpleDateFormat( "yyyy:MM:dd HH:mm:ss" );
        Date createDate = formatter.parse( createDateString.getValue( ).toString( ) );

        Calendar calendar = Calendar.getInstance( new Locale( "pl", "PL" ) );
        calendar.setTime( createDate );
        Integer year = calendar.get( Calendar.YEAR );
        Integer month = calendar.get( Calendar.MONTH ) + 1;
        String monthFormatted = ( month < 10 ) ? ( "0" + month ) : month.toString( );
        String monthName = new SimpleDateFormat( "MMMM" ).format( calendar.getTime( ) );
        monthName = removePolishChar( monthName );

        String pathString = file.getParentFile( ).getAbsolutePath( ) + "/" + year + "_" + monthFormatted + monthName.toUpperCase( );
        File path = new File( pathString );
        if ( !path.exists( ) ) {
            textArea.append( "\nCreating directory: " + path.getPath( ) );
            path.mkdir( );
        }

        String newFileString = pathString + "/" + createNewFileName( calendar, file, "" );
        File newFile = new File( newFileString );
        int i = 2;
        while ( newFile.exists( ) ) {
            newFileString =  pathString + "/" + createNewFileName( calendar, file, "_" + i );
            newFile = new File( newFileString );
            i++;
        }

        textArea.append( "\n" + file.getName( ) + " => " + newFile.getName( ) );
        FileUtils.moveFile( file, newFile );
    }

    String removePolishChar( String input ) {
        input = input.replaceAll( "ą", "a" );
        input = input.replaceAll( "ę", "e" );
        input = input.replaceAll( "ć", "c" );
        input = input.replaceAll( "ł", "l" );
        input = input.replaceAll( "ń", "n" );
        input = input.replaceAll( "ó", "o" );
        input = input.replaceAll( "ś", "s" );
        input = input.replaceAll( "ż", "z" );
        input = input.replaceAll( "ź", "z" );
        return input;
    }

    String createNewFileName( Calendar calendar, File oldFile, String postfix ) {
        SimpleDateFormat formatter = new SimpleDateFormat( "YYYYMMdd_HHmmss" );

        String[] nameSplitted = oldFile.getName( ).split( "\\." );
        String extension = "";
        if ( nameSplitted.length > 1 ) {
            extension = nameSplitted[nameSplitted.length - 1];
        }

        return formatter.format( calendar.getTime( ) ) + postfix + "." + extension;
    }
}
