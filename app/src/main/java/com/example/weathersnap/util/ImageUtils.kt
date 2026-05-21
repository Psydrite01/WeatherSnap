package com.example.weathersnap.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface

data class CompressedImageResult(
    val uri: Uri,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long
)

object ImageUtils {

    /**
     * Compresses the image at [originalFile] to ~60% quality JPEG,
     * also downscaling to max 1280px on the longest edge.
     * Returns a [CompressedImageResult] with the new URI and both sizes.
     */
    fun compressImage(
        context: Context,
        originalFile: File,
        quality: Int = 60,
        maxDimension: Int = 1280
    ): CompressedImageResult {
        val originalSizeBytes = originalFile.length()

        // Decode with inJustDecodeBounds first
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(originalFile.absolutePath, options)

        val rawWidth  = options.outWidth
        val rawHeight = options.outHeight
        var sampleSize = 1
        while (rawWidth / (sampleSize * 2) >= maxDimension ||
            rawHeight / (sampleSize * 2) >= maxDimension) {
            sampleSize *= 2
        }

        val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath, decodeOpts)
            ?: throw IllegalStateException("Failed to decode bitmap")

        // ── Read EXIF rotation and fix it ────────────────────────────────────
        val exif = ExifInterface(originalFile.absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val rotationDegrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90  -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else                                 -> 0f
        }
        val correctedBitmap = if (rotationDegrees != 0f) {
            val matrix = Matrix().apply { postRotate(rotationDegrees) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                .also { if (it !== bitmap) bitmap.recycle() }
        } else {
            bitmap
        }
        // ─────────────────────────────────────────────────────────────────────

        val scaledBitmap = scaleBitmap(correctedBitmap, maxDimension)

        val compressedDir  = File(context.cacheDir, "camera_images").apply { mkdirs() }
        val compressedFile = File(compressedDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(compressedFile).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }

        if (scaledBitmap !== correctedBitmap) correctedBitmap.recycle()
        scaledBitmap.recycle()

        val compressedUri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            compressedFile
        )

        return CompressedImageResult(
            uri                 = compressedUri,
            originalSizeBytes   = originalSizeBytes,
            compressedSizeBytes = compressedFile.length()
        )
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= maxDimension && h <= maxDimension) return bitmap
        val ratio = minOf(maxDimension.toFloat() / w, maxDimension.toFloat() / h)
        return Bitmap.createScaledBitmap(bitmap, (w * ratio).toInt(), (h * ratio).toInt(), true)
    }

    fun formatBytes(bytes: Long): String = when {
        bytes >= 1_000_000 -> "%.2f MB".format(bytes / 1_000_000f)
        bytes >= 1_000     -> "%.1f KB".format(bytes / 1_000f)
        else               -> "$bytes B"
    }
}