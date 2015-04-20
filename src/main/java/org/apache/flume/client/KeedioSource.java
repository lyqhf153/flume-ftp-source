/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.flume.client;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;


/**
 *
 * @author Luis Lázaro lalazaro@keedio.com
 * Keedio
 */
public abstract class KeedioSource {
    
    private static final Logger log = LoggerFactory.getLogger(KeedioSource.class);
      
    private Map<String, Long> sizeFileList = new HashMap<>();
    private Set<String> existFileList = new HashSet<>();
    private Path pathTohasmap = Paths.get("");
    private Path hasmap = Paths.get("");
    private Path absolutePath = Paths.get("");   
    
    
    protected String server;
    protected String user;
    protected String password;
    protected String folder;
    protected String fileName;
    protected Integer port;
    protected Integer bufferSize;
    protected int runDiscoverDelay;
    protected String workingDirectory; //working directory specified in config.
    private boolean flushLines;
    
    protected boolean connected;
    protected String dirToList;
    protected Object file;   //type of file the sources will use in each file-system-connection
     
    public abstract boolean connect();
    public abstract void disconnect();
    public abstract List<Object> listFiles(String dirToList);
    public abstract void changeToDirectory(String directory);
    public abstract InputStream getInputStream(Object File ) throws IOException;
    public abstract String getObjectName(Object file);
    public abstract boolean isDirectory(Object file);
    public abstract boolean isFile(Object file);
    public abstract boolean particularCommand();
    public abstract long getObjectSize(Object file);
    public abstract boolean isLink(Object file);
    public abstract String getLink(Object file);
    public abstract String getDirectoryserver(); //the working directory retrieved by server
    public abstract Object getClientSource();
   
    /**
     * @void Serialize hashmap
     */
    public void saveMap() {
        try {
            FileOutputStream fileOut = new FileOutputStream(getAbsolutePath().toString());
            try (ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject((HashMap) this.getSizeFileList());
            }
        } catch (FileNotFoundException e) {
            log.error("Error saving map File", e);
        } catch (IOException e) {
            log.error("Error saving map IO:", e);
        }
    }

    /**
     * @return HashMap<String,Long> 
     * @param name
     */
    public Map<String, Long> loadMap(String name) throws ClassNotFoundException, IOException {
        FileInputStream map = new FileInputStream(name);
        HashMap hasMap;
        try (ObjectInputStream in = new ObjectInputStream(map)) {
            hasMap = (HashMap) in.readObject();
        }
        return hasMap;
    }

    /**
     * @void, delete file from hashmaps if deleted from server
     */
    public void cleanList() {
        for (Iterator<String> iter = this.getSizeFileList().keySet().iterator(); iter.hasNext();) {
            final String filename = iter.next();
            if (!(existFileList.contains(filename))) {
                iter.remove();
            }
        }
    }

    /**
     * @void, check if there are previous files to load of an old session
     */
    public void checkPreviousMap() {
        Path file1 = makeLocationFile();
        try {
            if (Files.exists(file1)) {
                setSizeFileList(loadMap(file1.toString()));
                log.info("Found previous map of files flumed");
            } else {
                log.info("Not found preivous map of files flumed");

            }

        } catch (IOException | ClassNotFoundException e) {
            log.info("Exception thrown checking previous map ", e);
        }
    }

    /**
     * @return boolean, folder where to save data exists
     */
    public boolean existFolder() {
        pathTohasmap = Paths.get(getFolder()); //ruta a la carpeta especificada en fichero conf
        boolean folderExits = false;
        if (Files.exists(getPathTohasmap())) { //si realmente existe la carpeta especificada
            folderExits = true;
        }
        return folderExits;
    }

    /**
     * @return server
     */
    public String getServer() {
        return server;
    }

    /**
     * @void 
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @void 
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the folder
     */
    public String getFolder() {
        return folder;
    }

    /**
     * @param folder the folder to set
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return the bufferSize
     */
    public Integer getBufferSize() {
        return bufferSize;
    }

    /**
     * @param bufferSize the bufferSize to set
     */
    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * @return the runDiscoverDelay
     */
    public int getRunDiscoverDelay() {
        return runDiscoverDelay;
    }

    /**
     * @param runDiscoverDelay the runDiscoverDelay to set
     */
    public void setRunDiscoverDelay(int runDiscoverDelay) {
        this.runDiscoverDelay = runDiscoverDelay;
    }

    /**
     * @return the workingDirectory
     */
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * @param workingDirectory the workingDirectory to set
     */
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * @return the sizeFileList
     */
    public Map<String, Long> getSizeFileList() {
        return sizeFileList;
    }

    /**
     * @param sizeFileList the sizeFileList to set
     */
    public void setSizeFileList(Map<String, Long> sizeFileList) {
        this.sizeFileList = sizeFileList;
    }

    /**
     * @return the existFileList
     */
    public Set<String> getExistFileList() {
        return existFileList;
    }

    /**
     * @param existFileList the existFileList to set
     */
    public void setExistFileList(Set<String> existFileList) {
        this.existFileList = existFileList;
    }

    /**
     * @return the pathTohasmap
     */
    public Path getPathTohasmap() {
        return pathTohasmap;
    }

    /**
     * @param pathTohasmap the pathTohasmap to set
     */
    public void setPathTohasmap(Path pathTohasmap) {
        this.pathTohasmap = pathTohasmap;
    }

    /**
     * @return the hasmap
     */
    public Path getHasmap() {
        return hasmap;
    }

    /**
     * @param hasmap the hasmap to set
     */
    public void setHasmap(Path hasmap) {
        this.hasmap = hasmap;
    }

    /**
     * @return the absolutePath
     */
    public Path getAbsolutePath() {
        return absolutePath;
    }

    /**
     * @param absolutePath the absolutePath to set
     */
    public void setAbsolutePath(Path absolutePath) {
        this.absolutePath = absolutePath;
    }

    /**
     * @return the connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * @param connected the connected to set
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    /**
     * 
     * @return Path of file and folder  
     */
    public Path makeLocationFile(){
      hasmap = Paths.get(getFileName());
      absolutePath = Paths.get(pathTohasmap.toString(), hasmap.toString());
      return absolutePath;
    }

    /**
     * @return the flushLines
     */
    public boolean isFlushLines() {
        return flushLines;
    }

    /**
     * @param flushLines the flushLines to set
     */
    public void setFlushLines(boolean flushLines) {
        this.flushLines = flushLines;
    }
    
} //endclass
