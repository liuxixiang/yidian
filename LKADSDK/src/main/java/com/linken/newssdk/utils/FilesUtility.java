package com.linken.newssdk.utils;

import android.support.annotation.Nullable;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Helper class to work with file system.
 */
public class FilesUtility {

    /**
     * Delete object at the given location.
     * If it is a folder - it will be deleted recursively will all content.
     *
     * @param pathToFileOrDirectory absolute path to the file/directory.
     */
    public static void delete(String pathToFileOrDirectory) {
        if (pathToFileOrDirectory == null) {
            return;
        }
        delete(new File(pathToFileOrDirectory));
    }

    /**
     * Delete file object.
     * If it is a folder - it will be deleted recursively will all content.
     *
     * @param fileOrDirectory file/directory to delete
     */
    public static void delete(File fileOrDirectory) {
        if (!fileOrDirectory.exists()) {
            return;
        }

        if (fileOrDirectory.isDirectory()) {
            File[] filesList = fileOrDirectory.listFiles();
            for (File child : filesList) {
                delete(child);
            }
        }

        final File to = new File(fileOrDirectory.getAbsolutePath() + System.currentTimeMillis());
        fileOrDirectory.renameTo(to);
        to.delete();

        //fileOrDirectory.delete();
    }

    /**
     * Check if folder exists at the given path.
     * If not - it will be created with with all subdirectories.
     *
     * @param dirPath absolute path to the directory
     */
    public static void ensureDirectoryExists(String dirPath) {
        ensureDirectoryExists(new File(dirPath));
    }

    /**
     * Check if folder exists.
     * If not - it will be created with with all subdirectories.
     *
     * @param dir file object
     */
    public static void ensureDirectoryExists(File dir) {
        if (dir != null && (!dir.exists() || !dir.isDirectory())) {
            dir.mkdirs();
        }
    }

    /**
     * Copy object from one place to another.
     * Can be used to copy file to file, or folder to folder.
     *
     * @param src absolute path to source object
     * @param dst absolute path to destination object
     * @throws IOException
     */
    public static void copy(String src, String dst) throws IOException {
        copy(new File(src), new File(dst));
    }

    /**
     * Copy file object from one place to another.
     * Can be used to copy file to file, or folder to folder.
     *
     * @param src source file
     * @param dst destination file
     * @throws IOException
     */
    public static void copy(File src, File dst) throws IOException {
        if (src.isDirectory()) {
            ensureDirectoryExists(dst);

            String[] filesList = src.list();
            for (String file : filesList) {
                File srcFile = new File(src, file);
                File destFile = new File(dst, file);

                copy(srcFile, destFile);
            }
        } else {
            copyFile(src, dst);
        }
    }

    private static void copyFile(File fromFile, File toFile) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(fromFile));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(toFile));

        // Transfer bytes from in to out
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    @Nullable
    public static JSONObject readJSONFromFile(InputStream inputStream) throws IOException {
        String str = readFromFile(inputStream);

        JSONObject obj = null;
        try {
            obj = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Read data as string from the provided file.
     *
     * @param inputStream file to read from
     * @return data from file
     * @throws IOException
     */
    public static String readFromFile(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line).append("\n");
        }

        bufferedReader.close();

        return content.toString().trim();
    }

    @Nullable
    public static ArrayList<JSONObject> readJSONArrayFromFile(InputStream inputStream) throws IOException {
        ArrayList<String> strArray = readArrayFromFile(inputStream);

        ArrayList<JSONObject> objArray = new ArrayList<JSONObject>();
        for (int i = 0; i < strArray.size(); i++) {
            try {

                JSONObject obj = new JSONObject(strArray.get(i));

                objArray.add(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return objArray;
    }

    /**
     * Read data as Array from the provided file.
     *
     * @param inputStream file to read from
     * @return data from file
     * @throws IOException
     */
    public static ArrayList<String> readArrayFromFile(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ArrayList<String> stringArrayList = new ArrayList<String>();
        String line;
        try {
            Stack<String> stack = new Stack<String>();
            StringBuilder content = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("{")&&line.endsWith("}")){//json with one line
                    stringArrayList.add(new String(line));
                    continue;
                } else if ("".equals(line) || !(line.startsWith("{") || line.startsWith("}") || line.startsWith("\"") || line.startsWith("[") ||line.startsWith("]"))){//line in json start with {,},",[,]
                    continue;
                }
                for (int i = 0;i<line.length();i++){
                    if (line.charAt(i) == '{')
                        stack.push("{");
                    else if (line.charAt(i) == '}')
                        stack.pop();
                    else
                        continue;
                }
                content.append(line).append("\n");
                if (stack.isEmpty()){//one json ended
                    String temp = new String(content.toString());
                    stringArrayList.add(temp);
                    content.delete(0,content.length());//clear StringBuilder
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null)
                bufferedReader.close();
        }
        return stringArrayList;
    }

    /**
     * Calculate MD5 hash of the file at the given path
     *
     * @param filePath absolute path to the file
     * @return calculated hash
     * @throws Exception
     * @see
     */
    public static String calculateFileHash(String filePath) throws Exception {
        return calculateFileHash(new File(filePath));
    }

    /**
     * Calculate MD5 hash of the file
     *
     * @param file file whose hash we need
     * @return calculated hash
     * @throws Exception
     * @see
     */
    public static String calculateFileHash(File file) throws Exception {
        return EncryptUtil.generateMD5(file);
    }
}