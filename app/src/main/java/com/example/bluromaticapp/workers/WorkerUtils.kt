package com.example.bluromaticapp.workers

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.bluromaticapp.OUTPUT_PATH
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Create a [Uri] from a resource.
 */
fun Context.getImageUri(): Uri {
    // Improved version: Use the actual resource name directly as per handoff recommendation
    // Using a more robust string template for the Uri
    val packageName = packageName
    val resourceName = "ic_launcher_foreground" // Ideally this would be "anhdep" if it existed
    return Uri.parse("android.resource://$packageName/drawable/$resourceName")
}

/**
 * Writes bitmap to a temporary file and returns the Uri for the file
 */
fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap): Uri {
    val name = String.format("blur-filter-output-%s.png", UUID.randomUUID().toString())
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val outputFile = File(outputDir, name)
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, out)
    } finally {
        out?.let {
            try {
                it.close()
            } catch (ignore: Exception) {
            }
        }
    }
    return Uri.fromFile(outputFile)
}

/**
 * Blurs the given Bitmap image
 * @param bitmap Image to blur
 * @param blurLevel Blur level (1, 2, or 3)
 * @return Blurred bitmap
 */
fun blurBitmap(bitmap: Bitmap, blurLevel: Int): Bitmap {
    val input = Bitmap.createScaledBitmap(
        bitmap,
        bitmap.width / (blurLevel * 5),
        bitmap.height / (blurLevel * 5),
        true
    )
    return Bitmap.createScaledBitmap(input, bitmap.width, bitmap.height, true)
}
