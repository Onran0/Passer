package com.github.onran0.passer.core;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public final class ClipboardEraseThread extends Thread {

    private long eraseIn;
    private boolean running;

    public ClipboardEraseThread() {
        this.setDaemon(true);
        this.setName("Clipboard Erase Thread");
    }

    public synchronized void eraseAfter(long ms) {
        this.eraseIn = System.currentTimeMillis() + ms;
    }

    public synchronized void stopThread() {
        running = false;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            while(System.currentTimeMillis() < eraseIn) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Toolkit
                        .getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(
                                new StringSelection(""),
                                null
                        );

                eraseIn = Long.MAX_VALUE;
            } catch (IllegalStateException e) {
                eraseIn = System.currentTimeMillis() + 500;
            }
        }
    }
}