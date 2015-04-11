package com.eltiland.test;

import com.eltiland.model.file.File;
import com.eltiland.model.file.FileBody;

/**
 * Created with IntelliJ IDEA.
 * User: nklimenko
 * Date: 30.01.13
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class FileFactory {
    private static final String FILE_NAME = "testFile";

    public static File createFile(){
        return createFile(FILE_NAME);
    }

    private static File createFile(String fileName) {
        FileBody body = new FileBody();
        body.setBody("body".getBytes());
        body.setFilename(null);

        FileBody preview = new FileBody();
        preview.setBody("preview".getBytes());
        preview.setFilename(null);

        File file = new File();
        file.setName(fileName);
        file.setSize(4);
        file.setType("application/acrobat");
        file.setBody(body);
        file.setPreviewBody(preview);

        return file;
    }
}
