package com.peace.log.assist.plugin.util

import java.util.zip.CRC32
import java.util.zip.CheckedOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class Compressor {
    private File outputFile
    private static final int BUFFER = 8192

    Compressor(String fileName) {
        outputFile = new File(fileName)
    }

    void compress(String inputFile) {
        File file = new File(inputFile)
        if (file.exists()) {
            File[] files = file.listFiles()
            if (files != null && files.length > 0) {
                FileOutputStream fos = new FileOutputStream(outputFile)
                CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32())
                ZipOutputStream zos = new ZipOutputStream(cos)

                String baseDir = ""
                for (int i = 0; i != files.length; ++i) {
                    compress(files[i], zos, baseDir)
                }
                zos.close()
            }
        }
    }

    void compress(File file, ZipOutputStream zos, String baseDir) {
        if (file.isDirectory()) {
            compressDirectory(file, zos, baseDir)
        } else {
            compressFile(file, zos, baseDir)
        }
    }

    private void compressDirectory(File dir, ZipOutputStream zos, String baseDir) {
        if (!dir.exists()) {
            return
        }

        File[] files = dir.listFiles()
        for (int i = 0; i != files.length; ++i) {
            compress(files[i], zos, baseDir + dir.name + "/")
        }
    }

    private void compressFile(File file, ZipOutputStream zos, String baseDir) {
        if (!file.exists()) {
            return
        }

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))
            String filePath = baseDir + file.name

            ZipEntry entry = new ZipEntry(filePath)
            zos.putNextEntry(entry)

            int count
            byte[] data = new byte[BUFFER]
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                zos.write(data, 0, count)
            }
            bis.close()
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }
}