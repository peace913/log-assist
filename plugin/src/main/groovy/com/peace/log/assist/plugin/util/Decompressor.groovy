package com.peace.log.assist.plugin.util

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

            }
        }
    }
}