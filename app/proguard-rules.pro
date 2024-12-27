# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# ----- Корутин и Kotlin -----
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keep class kotlin.** { *; }
-dontwarn kotlin.**

# ----- Jetpack Compose -----
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ----- Жизненный цикл ViewModel -----
-keep class androidx.lifecycle.DefaultLifecycleObserver
-keepclassmembers class androidx.lifecycle.DefaultLifecycleObserver {
    *;
}
-dontwarn androidx.lifecycle.DefaultLifecycleObserver

# ----- Аннотации -----
-keepattributes *Annotation*

# ----- Для Room -----
-dontwarn androidx.room.paging.**
-dontwarn androidx.sqlite.db.**

# ----- Для Hilt -----
-dontwarn dagger.**
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-dontwarn javax.annotation.**

# ----- Для Kotlin Reflection (если используется) -----
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# ----- Прочее -----
-keep class androidx.** { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
-dontwarn androidx.**
-dontwarn android.**
-dontwarn org.intellij.lang.annotations.**

# ----- Ошибки и предупреждения (если возникают) -----
-dontwarn org.codehaus.**
-dontwarn com.google.gson.**

# ----- Генерация (если используются Room, Glide и т.д.) -----
-keepnames class * {
    @androidx.room.Database *;
    @androidx.room.Entity *;
    @androidx.room.Dao *;
}

-keepclassmembers class * {
    @androidx.room.Dao <methods>;
}

-keep @androidx.room.* class * { *; }
-dontwarn androidx.room.RoomDatabase
