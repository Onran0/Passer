package com.github.onran0.passer.core;

import com.github.onran0.passer.security.SecuredByteArray;
import com.github.onran0.passer.security.SecuredCharArray;
import com.github.onran0.passer.security.SecuredInt;
import com.github.onran0.passer.security.SecuredLong;

public class PasswordInfo {

    private SecuredCharArray caption;
    private final SecuredInt type;
    private final SecuredByteArray password;
    private final SecuredLong creationTime;
    private SecuredLong modificationTime;

    public PasswordInfo(
            char[] caption,
            int[] type,
            byte[] password,
            long[] creationTime,
            long[] modificationTime
    ) {
        this.caption = new SecuredCharArray(caption);
        this.type = new SecuredInt(type);
        this.password = new SecuredByteArray(password);
        this.creationTime = new SecuredLong(creationTime);
        this.modificationTime = new SecuredLong(modificationTime);
    }

    public char[] getCaption() {
        return caption.getDecryptedData();
    }

    public void setCaption(char[] caption) {
        this.caption = new SecuredCharArray(caption);
    }

    public int[] getType() {
        return type.getDecryptedData();
    }

    public byte[] getPassword() {
        return password.getDecryptedData();
    }

    public long[] getCreationTime() {
        return creationTime.getDecryptedData();
    }

    public long[] getModificationTime() {
        return modificationTime.getDecryptedData();
    }

    public void setModificationTime(long[] modificationTime) {
        this.modificationTime = new SecuredLong(modificationTime);
    }
}