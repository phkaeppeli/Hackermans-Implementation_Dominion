package client.commonClasses;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import client.ServiceLocator;


/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * This class provides basic functionality for loading and saving program
 * options. Default options may be delivered with the application; local options
 * (changed by the user) are saved to a file. Program constants can be defined
 * by defining options that the user has no way to change.
 * 
 * @author Brad Richards
 */
public class Configuration {
    ServiceLocator sl = ServiceLocator.getServiceLocator();  // for easy reference

    private Properties defaultOptions;
    private Properties localOptions;

    public Configuration() {
        // Load default properties from wherever the code is
        defaultOptions = new Properties();
        String defaultFilename = sl.getAPP_NAME() + "_defaults.cfg";
        InputStream inStream = sl.getAPP_CLASS().getResourceAsStream(defaultFilename);
        try {
            defaultOptions.load(inStream);
        } catch (Exception e) {
        } finally {
            try {
                inStream.close();
            } catch (Exception ignore) {
            }
        }

        // Define locally-saved properties; link to the default values
        localOptions = new Properties(defaultOptions);

        // Load the local configuration file, if it exists.
        try {
            inStream = new FileInputStream(sl.getAPP_NAME() + ".cfg");
            localOptions.load(inStream);
        } catch (FileNotFoundException e) { // from opening the properties file
        } catch (IOException e) { // from loading the properties
        } finally {
            try {
                inStream.close();
            } catch (Exception ignore) {
            }
        }
    }
    
    public void save() {
        FileOutputStream propFile = null;
        try {
            propFile = new FileOutputStream(sl.getAPP_NAME() + ".cfg");
            localOptions.store(propFile, null);
        } catch (Exception e) {
        } finally {
            if (propFile != null) {
                try {
                    propFile.close();
                } catch (Exception e) {
                }
            }
        }
    }
    
    public String getOption(String name) {
        return localOptions.getProperty(name);
    }
    
    public void setLocalOption(String name, String value) {
        localOptions.setProperty(name, value);
    }
}
