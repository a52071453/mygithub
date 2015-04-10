/**
 * Copyright (C) Alibaba Cloud Computing, 2012
 * All rights reserved.
 * 
 * 版权所有 （C）阿里巴巴云计算，2012
 */

package com.aliyun.oss.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class IOUtils {

    public static String readStreamAsString(InputStream in, String charset)
            throws IOException {
        if (in == null)
            return "";

        Reader reader = null;
        Writer writer = new StringWriter();
        String result;

        char[] buffer = new char[1024];
        try{
            reader = new BufferedReader(
                    new InputStreamReader(in, charset));

            int n;
            while((n = reader.read(buffer)) > 0){
                writer.write(buffer, 0, n);
            }

            result = writer.toString();
        } finally {
            in.close();
            if (reader != null){
                reader.close();
            }
            if (writer != null){
                writer.close();
            }
        }

        return result;
    }
    
    public static byte[] readStreamAsByteArray(InputStream in)
        throws IOException {
        if (in == null) {
            return new byte[0];
        }
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > -1) {
            output.write(buffer, 0, len);
        }
        output.flush();
        return output.toByteArray();
    }

    public static void safeClose(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) { }
        }
    }

    public static void safeClose(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {}
        }
    }
    
    public static boolean checkFile(String filePath) {
    	if (filePath == null || filePath.trim().equals("")) {
    		return false;
    	}
    	
    	File file = new File(filePath);
    	boolean exists = false;
    	boolean isFile = false;
    	boolean canRead = false;
    	try {
    		exists = file.exists();
    		isFile = file.isFile();
    		canRead = file.canRead();
    	} catch (SecurityException se) {
    		// Swallow the exception and return false directly.
    		return false;
    	}
    	
    	return (exists && isFile && canRead);
    }
}
