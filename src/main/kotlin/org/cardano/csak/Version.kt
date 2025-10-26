package org.cardano.csak

import java.util.Properties

object Version {
    val VERSION: String by lazy {
        try {
            val properties = Properties()
            val inputStream = Version::class.java.classLoader.getResourceAsStream("version.properties")
            if (inputStream != null) {
                properties.load(inputStream)
                properties.getProperty("version", "unknown")
            } else {
                "unknown"
            }
        } catch (e: Exception) {
            "unknown"
        }
    }
}
