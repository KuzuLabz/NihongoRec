import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.compose.components.resources)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

val copyModelResources by tasks.registering(Copy::class) {
    description = ""
    from(project(":shared").layout.projectDirectory
        .dir("src/commonMain/composeResources/files"))
    into(layout.buildDirectory.dir("generated/app-resources/common"))
    include("char_classifier.onnx", "char_classifier_labels.txt")
}

compose.desktop {
    application {
        mainClass = "com.kuzulabz.nihongorec.MainKt"

        nativeDistributions {
            appResourcesRootDir.set(layout.buildDirectory.dir("generated/app-resources"))
            targetFormats(TargetFormat.Pkg, TargetFormat.Exe, TargetFormat.Deb, TargetFormat.Rpm)

            modules("jdk.unsupported")
            modules("jdk.unsupported.desktop")

            windows {
                iconFile.set(project.file("../icons/windows/icon_512.ico"))
            }

            macOS {
                iconFile.set(project.file("../icons/macos/AppIcon.icns"))
            }

            linux {
                iconFile.set(project.file("../icons/icon.png"))
                menuGroup = "Education"
                appCategory = "Education"
                debMaintainer = "kuzulabz@gmail.com"
            }

            packageName = "NihongoRec"
            packageVersion = "0.1.0"
            copyright = "© 2026 KuzuLabz. All rights reserved."
            vendor = "KuzuLabz"
        }
    }
}

tasks.configureEach {
    // Match any task that prepares or packages resources / distributables
    if (name.contains("prepareAppResources", ignoreCase = true) ||
        name.contains("AppResources", ignoreCase = true) ||
        name.startsWith("package") ||
        (name.startsWith("create") && name.contains("Distributable", ignoreCase = true))
    ) {
        dependsOn(copyModelResources)
    }
}