package com.arq.currencyconverter

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchitectureTest {

    @Test
    fun featurePackagesDoNotDependOnOtherFeaturePackages() {
        val featureRoot = File("src/main/java/com/arq/currencyconverter/feature")
        if (!featureRoot.exists()) return

        val featurePackages = featureRoot.listFiles { it.isDirectory }?.map { it.name }.orEmpty()
        val violations = mutableListOf<String>()

        featurePackages.forEach { sourceFeature ->
            val sourceDir = featureRoot.resolve(sourceFeature)
            sourceDir.walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .forEach { file ->
                    file.readLines().forEach { line ->
                        featurePackages
                            .filter { it != sourceFeature }
                            .forEach { targetFeature ->
                                val forbiddenImport =
                                    "import com.arq.currencyconverter.feature.$targetFeature"
                                if (line.trim().startsWith(forbiddenImport)) {
                                    violations.add("${file.path}: $line")
                                }
                            }
                    }
                }
        }

        assertTrue(
            "Cross-feature imports detected:\n${violations.joinToString("\n")}",
            violations.isEmpty()
        )
    }
}
