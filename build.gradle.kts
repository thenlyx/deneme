import org.gradle.api.tasks.compile.JavaCompile

plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
}

// Tüm modüllerde Java derleyicisinin varsayılan 1.8 hedefini zorla 17 yap.
subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.release.set(17)
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}
