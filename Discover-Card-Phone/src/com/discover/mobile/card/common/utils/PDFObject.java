package com.discover.mobile.card.common.utils;

import java.io.File;

public class PDFObject {

    private  File file;
    private  boolean success;
    private String title;
    private String message;

    public File getFile() {
        return file;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public PDFObject(final File file, final boolean result) {
        super();
        this.file = file;
        success = result;
    }

    public PDFObject(final File file, final boolean result, final String title,
            final String message) {
        super();
        this.file = file;
        success = result;
        this.title = title;
        this.message = message;
    }
}
