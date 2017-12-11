package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/8/28.
 */
public class FileUpload extends StatusBoolean implements Serializable {
    public String FileName;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }
}
