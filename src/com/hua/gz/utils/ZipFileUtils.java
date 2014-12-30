package com.hua.gz.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class ZipFileUtils {

	
	
	private static final int BUFFER_SIZE = 1024;
	private static final String TAG = ZipFileUtils.class.getSimpleName();

	public static void zip(String[] files, String zipFile) throws IOException {
	    BufferedInputStream origin = null;
	    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
	    try { 
	        byte data[] = new byte[BUFFER_SIZE];

	        for (int i = 0; i < files.length; i++) {
	            FileInputStream fi = new FileInputStream(files[i]);    
	            origin = new BufferedInputStream(fi, BUFFER_SIZE);
	            try {
	                ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
	                out.putNextEntry(entry);
	                int count;
	                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
	                    out.write(data, 0, count);
	                }
	            }
	            finally {
	                origin.close();
	            }
	        }
	    }
	    finally {
	        out.close();
	    }
	}

	public static void unzip(String zipFile, String saveLocation) throws IOException {
	    try {
	        File f = new File(saveLocation);
	        if(!f.isDirectory()) {
	            f.mkdirs();
	        }
	        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
	        try {
	        	byte data[] = new byte[BUFFER_SIZE];
	            ZipEntry ze = null;
	            while ((ze = zin.getNextEntry()) != null) {
	                String path = saveLocation + ze.getName();

	                if (ze.isDirectory()) {
	                    File unzipFile = new File(path);
	                    if(!unzipFile.isDirectory()) {
	                        unzipFile.mkdirs();
	                    }
	                }
	                else {
	                    FileOutputStream fout = new FileOutputStream(path, false);
	                    try {
	                    	 int count;
							while ((count = zin.read(data, 0, BUFFER_SIZE)) != -1) {
								fout.write(data, 0, count);
	     	                }
//	                        for (int c = zin.read(); c != -1; c = zin.read()) {
//	                            fout.write(c);
//	                        }
	                        zin.closeEntry();
	                    }
	                    finally {
	                        fout.close();
	                    }
	                }
	            }
	        }
	        finally {
	            zin.close();
	        }
	    }
	    catch (Exception e) {
	        Log.e(TAG, "Unzip exception", e);
	    }
	}
}
