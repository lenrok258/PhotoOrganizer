package com.lofatsoftware.photoorganizer.action;


import javax.swing.*;
import java.io.File;

public interface Action {

    void proceedClicked( JTextArea textArea, File chosenDirectory );

}
