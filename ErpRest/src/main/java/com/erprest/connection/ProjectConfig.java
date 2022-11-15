/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.connection;

import com.erprest.model.Config;
import com.erprest.service.FileSystem;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author msi_ge72
 */
public class ProjectConfig {

    private static ProjectConfig projectConfig;
    private static Config config;
    private static String xsltString;
    private String despatchXslString;
    private File DespatchExample;
    private static final Logger logger = Logger.getLogger(ProjectConfig.class.getName());

    public static ProjectConfig getInstance() {
        if (projectConfig == null) {
            projectConfig = new ProjectConfig();
        }
        return projectConfig;
    }

    private ProjectConfig() {
        String cFile = new FileSystem().configFile;
        String osType = System.getProperty("os.name");
        if (osType.toLowerCase().contains("windows")) {
            cFile = "C:\\Users\\msi_ge72\\Desktop\\invoiceTemp\\config.json";
        }
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(cFile));
            config = gson.fromJson(reader, Config.class);
            reader.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "###config.json okunurken hata oluştu.", e);
        }
        try {
            xsltString = new String(
                    Files.readAllBytes(
                            Paths.get(
                                    ProjectConfig.class.getClassLoader().getResource("xslt/general.xslt").toURI())));
            DespatchExample = new File(ProjectConfig.class.getClassLoader().getResource("example/Irsaliye1.xml").getFile());
            despatchXslString = new String(
                    Files.readAllBytes(
                            Paths.get(
                                    ProjectConfig.class.getClassLoader().getResource("xslt/irsaliye.xsl").toURI())));
        } catch (IOException | URISyntaxException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    public Config getConfig() {
        return config;
    }

    public String getXslt() {
        return xsltString;
    }

    public File getDespatchExample() {
        return DespatchExample;
    }

    public String getDespatchXsl() {
        return despatchXslString;
    }

}
