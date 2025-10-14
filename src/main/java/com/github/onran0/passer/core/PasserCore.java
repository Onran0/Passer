package com.github.onran0.passer.core;

import com.github.onran0.passer.io.PassesReader;
import com.github.onran0.passer.io.PassesWriter;
import com.github.onran0.passer.security.SecuredCharArray;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.prefs.Preferences;

public final class PasserCore {
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(PasserCore.class);

    private final List<File> recentPassesFiles = new ArrayList<>();
    private final ClipboardEraseThread clipboardEraseThread = new ClipboardEraseThread();

    public void eraseClipboardAfter(long ms) {
        clipboardEraseThread.eraseAfter(ms);
    }

    public List<File> getRecentPassesFiles() {
        return Collections.unmodifiableList(recentPassesFiles);
    }

    public void removePassesFileFromRecent(File file) {
        recentPassesFiles.removeIf(file::equals);
    }

    public void addPassesFileToRecent(File file) {
        for(File recent : recentPassesFiles) {
            if (recent.getAbsolutePath().equals(file.getAbsolutePath()))
                return;
        }

        recentPassesFiles.add(file);

        while(recentPassesFiles.size() > 10)
            recentPassesFiles.remove(0);
    }

    public Passes getPassesForFile(File file, SecuredCharArray masterPassword) throws IOException, GeneralSecurityException {
        Passes passes;

        try(var in = new FileInputStream(file)) {
            passes = new PassesReader(in).read(masterPassword);
        }

        return passes;
    }

    public void setPassesForFile(
            File file, Passes passes,
            String cipherAlgorithm, String kdfAlgorithm,
            SecuredCharArray masterPassword
    ) throws IOException, GeneralSecurityException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        new PassesWriter(baos, cipherAlgorithm, kdfAlgorithm).write(passes, masterPassword);


        FileOutputStream fos = new FileOutputStream(file);

        fos.write(baos.toByteArray());

        fos.close();
    }

    public void start() {
        clipboardEraseThread.start();
        load();
    }

    public void stop() {
        clipboardEraseThread.stopThread();
        store();
    }

    public void load() {
        this.recentPassesFiles.clear();

        String recentPassesFilesRawPaths = PREFERENCES.get("recentPassesFiles", null);

        if(recentPassesFilesRawPaths != null) {
            for(String recentPassesFile : recentPassesFilesRawPaths.split(";")) {
                File recent = new File(recentPassesFile);

                if(recent.exists())
                    this.recentPassesFiles.add(recent);
            }
        }
    }

    public void store() {
        if(!recentPassesFiles.isEmpty()) {
            StringBuilder recentPassesFilesRawPaths = new StringBuilder();

            for(File recentPassesFile : recentPassesFiles) {
                recentPassesFilesRawPaths.append(recentPassesFile.getAbsolutePath());
                recentPassesFilesRawPaths.append(";");
            }

            recentPassesFilesRawPaths.setLength(recentPassesFilesRawPaths.length() - 1);

            PREFERENCES.put("recentPassesFiles", recentPassesFilesRawPaths.toString());
        } else PREFERENCES.get("recentPassesFiles", "");
    }
}