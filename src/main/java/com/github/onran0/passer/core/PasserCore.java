package com.github.onran0.passer.core;

import com.github.onran0.passer.io.PASSERReader;
import com.github.onran0.passer.io.PASSERWriter;
import com.github.onran0.passer.security.RuntimeSecurity;
import com.github.onran0.passer.security.SecuredBool;
import com.github.onran0.passer.security.SecuredCharArray;
import com.github.onran0.passer.security.SecurityUtil;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.prefs.Preferences;

public class PasserCore {

    public static final String VERSION = "0.2.0";

    public static final char[] MISSING_PROPERTY_DEFAULT = "Not set".toCharArray();
    public static final int RECENT_FILES_COUNT = 10;

    public static final int V_0 = 0;
    public static final int V_1 = 1;
    public static final int V_2 = 2;

    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(PasserCore.class);

    private final List<File> recentPassesFiles = new ArrayList<>();
    private final ClipboardEraseThread clipboardEraseThread = new ClipboardEraseThread();

    private File openedFile;
    private SecuredCharArray masterPassword;
    private Passes passes;
    private boolean fileSaved;

    public void eraseClipboardAfter(long ms) {
        clipboardEraseThread.eraseAfter(ms);
    }

    public List<File> getRecentPassesFiles() {
        return Collections.unmodifiableList(recentPassesFiles);
    }

    public void createFile(File file, SecuredCharArray masterPassword, String cipherAlgorithm, String kdfAlgorithm) throws IOException, GeneralSecurityException {
        writeFile(file, passes = new Passes(), masterPassword, cipherAlgorithm, kdfAlgorithm);

        openFile(file, masterPassword, false);
    }

    public void openFile(File file, SecuredCharArray masterPassword) throws IOException, GeneralSecurityException {
        openFile(file, masterPassword, true);
    }

    private void openFile(File file, SecuredCharArray masterPassword, boolean doRead) throws IOException, GeneralSecurityException {
        if(doRead) {
            try(var in = new FileInputStream(file)) {
                this.passes = new PASSERReader(in).read(masterPassword);
            }
        }

        this.openedFile = file;
        this.masterPassword = masterPassword;
        this.fileSaved = true;

        recentPassesFiles.removeIf(recent -> recent.getAbsolutePath().equals(file.getAbsolutePath()));

        recentPassesFiles.add(file);

        while(recentPassesFiles.size() > RECENT_FILES_COUNT)
            recentPassesFiles.remove(0);
    }

    public void editMasterPassword(SecuredCharArray oldPassword, SecuredCharArray newPassword) throws IOException, GeneralSecurityException {
        if(openedFile == null)
            throw new IOException("File is not open");

        char[] currentDecrypted = this.masterPassword.getDecryptedData();
        char[] oldDecrypted = oldPassword.getDecryptedData();

       boolean currentMatchOld = SecurityUtil.passwordsAreMatch(currentDecrypted, oldDecrypted);

        RuntimeSecurity.clear(currentDecrypted);
        RuntimeSecurity.clear(oldDecrypted);

        if(!currentMatchOld)
            throw new GeneralSecurityException("Current master password and passed old are mismatch");

        this.masterPassword = newPassword;
    }

    public void saveFile(String cipherAlgorithm, String kdfAlgorithm) throws IOException, GeneralSecurityException {
        writeFile(openedFile, passes, masterPassword, cipherAlgorithm, kdfAlgorithm);

        fileSaved = true;
    }

    private void writeFile(File file, Passes passes, SecuredCharArray masterPassword, String cipherAlgorithm, String kdfAlgorithm) throws IOException, GeneralSecurityException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        new PASSERWriter(baos, cipherAlgorithm, kdfAlgorithm).write(passes, masterPassword);

        // guarantee of file integrity in case of an exception

        try(FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(baos.toByteArray());
        }
    }

    public void closeFile() {
        openedFile = null;
        masterPassword = null;
        passes = null;
        fileSaved = true;
    }

    public File getOpenedFile() {
        return openedFile;
    }

    public Passes getPasses() {
        return passes;
    }

    public boolean isSaved() {
        return fileSaved;
    }

    public void setUnsaved() {
        this.fileSaved = false;
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