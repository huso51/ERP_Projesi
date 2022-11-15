/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import com.erprest.dao.UserDao;
import com.erprest.model.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author msi_ge72
 */
public class FileSystem {

    private static final Logger logger = Logger.getLogger(FileSystem.class.getName());

    public final String ErpStorageDir = System.getProperty("catalina.base") + "/webapps/ErpStorage/";
    public final String wkhtmltopdfDir = ErpStorageDir + "wkhtmltopdf/";
    public final String invoiceTemp = wkhtmltopdfDir + "invoiceTemp/";
    public final String wkhtmltopdf = wkhtmltopdfDir + "wkhtmltopdf";

    public String configFile = ErpStorageDir + "config.json";

    public boolean checkWkhtmltopdf() {
        checkErpStorage();
        File wkhtmltopdfDirFile = new File(wkhtmltopdfDir);
        if (!wkhtmltopdfDirFile.exists()) {
            if (wkhtmltopdfDirFile.mkdirs()) {
                addFilePermissions(wkhtmltopdfDir);
            } else {
                logger.log(Level.SEVERE, null, "###wkhtmltopdf dizini yaratılamadı");
                return false;
            }
        }
        File wkhtmltopdfFile = new File(wkhtmltopdf);
        if (!wkhtmltopdfFile.exists()) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, "###wkhtmltopdf not exist...downloading");
            try {
                Runtime rt = Runtime.getRuntime();
                String[] command = {"wget",
                    "--no-check-certificate",
                    "https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.4/wkhtmltox-0.12.4_linux-generic-amd64.tar.xz",
                    "-O",
                    wkhtmltopdfDir + "wkhtmltox-0.12.4_linux-generic-amd64.tar.xz"};
                Process pr = rt.exec(command);
                pr.waitFor();
                logger.log(Level.INFO, Arrays.toString(command));

                String[] command2 = {"tar",
                    "-xvf",
                    wkhtmltopdfDir + "wkhtmltox-0.12.4_linux-generic-amd64.tar.xz",
                    "-C",
                    wkhtmltopdfDir};
                Process pr2 = rt.exec(command2);
                pr2.waitFor();
                logger.log(Level.INFO, Arrays.toString(command2));

                String[] command3 = {"cp",
                    wkhtmltopdfDir + "wkhtmltox/bin/wkhtmltopdf",
                    wkhtmltopdfDir};
                Process pr3 = rt.exec(command3);
                pr3.waitFor();
                logger.log(Level.INFO, Arrays.toString(command3));

                String[] command4 = {"mkdir",
                    invoiceTemp};
                Process pr4 = rt.exec(command4);
                pr4.waitFor();
                logger.log(Level.INFO, Arrays.toString(command4));

                String[] command5 = {"chmod",
                    "777",
                    invoiceTemp};
                Process pr5 = rt.exec(command5);
                pr5.waitFor();
                logger.log(Level.INFO, Arrays.toString(command5));
            } catch (IOException | InterruptedException e) {
                logger.log(Level.SEVERE, "###wkhtmltopdf setup failed:\n", e);
                return false;
            }
            logger.log(Level.INFO, "###wkhtmltopdf setup commands completed!");
        }
        if (new File(wkhtmltopdf).exists()) {
            logger.log(Level.INFO, "###wkhtmltopdf setup successfully");
            return true;
        } else {
            logger.log(Level.SEVERE, "###wkhtmltopdf setup failed");
            return false;
        }
    }

    public void addFilePermissions(String path) {
        File file = new File(path);
        try {
            file.setExecutable(true);
            file.setReadable(true);
            file.setWritable(true);
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_WRITE);
            perms.add(PosixFilePermission.OTHERS_READ);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
            perms.add(PosixFilePermission.GROUP_READ);
            perms.add(PosixFilePermission.GROUP_WRITE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            Path FilePathObject = Paths.get(file.getAbsolutePath());
            Files.setPosixFilePermissions(FilePathObject, perms);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void checkErpStorage() {
        Path path = Paths.get(ErpStorageDir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                addFilePermissions(ErpStorageDir);
            } catch (IOException e) {
                //fail to create directory
                logger.log(Level.SEVERE, "###ErpStorageDir dizini yaratılamadı" + ErpStorageDir, e);
            }
        }
    }

    public boolean checkConfigFile() {
        File configF = new File(configFile);
        if (!configF.exists()) {
            logger.log(Level.INFO, "###config.json file creating");
            Config config = new Config();
            try (Writer writer = new FileWriter(configFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(config, writer);
                logger.log(Level.INFO, "###config.json created. Check this file!!!");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "###config.json oluşturulurken hata oluştu.", e);
            }
        }
        return configF.exists();
    }

}
