package com.peace.log.assist.plugin.util

import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.util.jar.JarEntry
import java.util.jar.JarFile

class Decompressor {
    static void uncompress(File jarFile, File tarDir) throws IOException {
        JarFile jFile = new JarFile(jarFile)
        Enumeration<JarEntry> enumEntry = jFile.entries()
        while (enumEntry.hasMoreElements()) {
            JarEntry jarEntry = enumEntry.nextElement()
            File tarFile = new File(tarDir, jarEntry.name)
            if (jarEntry.name.contains('META-INF')) {
                File miFile = new File(tarDir, 'META-INF')
                if (!miFile.exists()) {
                    miFile.mkdirs()
                }
            }

            if (!tarFile.exists()) {
                if (jarFile.isDirectory()) {
                    tarFile.mkdirs()
                } else {
                    if (!tarFile.parentFile.exists()) {
                        tarFile.parentFile.mkdirs()
                    }
                }
            }

            if (jarEntry.isDirectory()) {
                continue
            }

            FileChannel fileChannel = new FileOutputStream(tarFile).getChannel()
            InputStream is = jFile.getInputStream(jarEntry)
            transferStream(is, fileChannel)
        }
    }

    private static void transferStream(InputStream is, FileChannel channel) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 10)
        ReadableByteChannel rbc = Channels.newChannel(is)
        try {
            while (rbc.read(byteBuffer) != -1) {
                byteBuffer.flip()
                channel.write(byteBuffer)
                byteBuffer.clear()
            }
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            close(rbc)
            close(channel)
        }
    }

    private static void close(Closeable closeable) {
        if (closeable == null) {
            return
        }

        try {
            closeable.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }
}