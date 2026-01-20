#==============================================
# OBFUSCATION - BẬT TRỘN MÃ MẠNH
#==============================================
-dontusemixedcaseclassnames
-verbose
-optimizationpasses 5
-repackageclasses ''
-allowaccessmodification
-overloadaggressively

#==============================================
# KEEP ATTRIBUTES
#==============================================
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

#==============================================
# ANDROID BASICS
#==============================================
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#==============================================
# RETROFIT (CHỈ GIỮ INTERFACE, CHO PHÉP OBFUSCATE IMPLEMENTATION)
#==============================================
-keepattributes Signature
-keepattributes Exceptions

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep Retrofit service interfaces
-keep,allowobfuscation interface com.example.dat_lich_kham_fe.data.api.** { *; }

#==============================================
# GSON (CHỈ BẢO VỆ MODEL)
#==============================================
-keepattributes Signature
-keepattributes *Annotation*

# Keep Gson classes
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# QUAN TRỌNG: Chỉ keep MODEL classes
-keep class com.example.dat_lich_kham_fe.data.model.** { *; }

# Keep fields với @SerializedName
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

#==============================================
# OKHTTP
#==============================================
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }



#==============================================
# COMPOSE
#==============================================
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

-dontwarn androidx.compose.**

#==============================================
# KOTLIN & COROUTINES
#==============================================
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}

-dontwarn kotlinx.**

#==============================================
# FIREBASE
#==============================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

#==============================================
# ML KIT
#==============================================
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

#==============================================
# CAMERAX
#==============================================
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

#==============================================
# DATASTORE
#==============================================
-keep class androidx.datastore.*.** { *; }

#==============================================
# KOTLINX SERIALIZATION
#==============================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}

-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.example.dat_lich_kham_fe.**$$serializer { *; }

-keepclassmembers class com.example.dat_lich_kham_fe.** {
    *** Companion;
}

-keepclasseswithmembers class com.example.dat_lich_kham_fe.** {
    kotlinx.serialization.KSerializer serializer(...);
}

#==============================================
# KTOR
#==============================================
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

#==============================================
# REMOVE LOGS (PRODUCTION)
#==============================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}

#==============================================
# WARNINGS
#==============================================
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**