package com.eltiland.utils;

import java.io.*;

/**
 * Common encoding utils.
 *
 * @author Aleksey Plotnikov
 */
public class EncodingUtils {

    /**
     * Convert file to another encoding.
     *
     * @param infile  input file.
     * @param outfile output file.
     * @param from    input encoding.
     * @param to      output encoding.
     */
    public static void convert(
            String infile, //input file name, if null reads from console/stdin
            String outfile, //output file name, if null writes to console/stdout
            String from,   //encoding of input file (e.g. UTF-8/windows-1251, etc)
            String to)     //encoding of output file (e.g. UTF-8/windows-1251, etc)
            throws IOException, UnsupportedEncodingException {
        // set up byte streams
        InputStream in;
        if (infile != null)
            in = new FileInputStream(infile);
        else
            in = System.in;
        OutputStream out;
        if (outfile != null)
            out = new FileOutputStream(outfile);
        else
            out = System.out;

        // Use default encoding if no encoding is specified.
        if (from == null) from = System.getProperty("file.encoding");
        if (to == null) to = System.getProperty("file.encoding");

        // Set up character stream
        Reader r = new BufferedReader(new InputStreamReader(in, from));
        Writer w = new BufferedWriter(new OutputStreamWriter(out, to));

        // Copy characters from input to output.  The InputStreamReader
        // converts from the input encoding to Unicode,, and the OutputStreamWriter
        // converts from Unicode to the output encoding.  Characters that cannot be
        // represented in the output encoding are output as '?'
        char[] buffer = new char[4096];
        int len;
        while ((len = r.read(buffer)) != -1)
            w.write(buffer, 0, len);
        r.close();
        w.flush();
        w.close();
    }
}
