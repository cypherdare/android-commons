package net.xpece.android.media.exif

import android.content.Context
import android.net.Uri
import android.support.media.ExifInterface
import java.io.BufferedInputStream

/**
 * Created by Eugen on 19.05.2016.
 */

object ExifUtils {

    /**
     * Requires com.android.support:exifinterface:25.1.0+.
     */
    @JvmStatic
    fun getOrientation(context: Context, uri: Uri): Int {
        context.contentResolver.openInputStream(uri).buffered().use {
            val orientation = getOrientation(it)
            return orientation
        }
    }

    /**
     * Requires com.android.support:exifinterface:25.1.0+.
     *
     * *Warning!* Android PNG reader doesn't support reset stream. Open a new stream before reading the image.
     */
    @JvmStatic
    fun getOrientation(buffered: BufferedInputStream): Int {
        var orientation = 0
        try {
            buffered.mark(65535)
            val exif = ExifInterface(buffered)
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            buffered.reset()
        } catch (ex: Exception) {
//        Timber.e(ex, "Invalid EXIF format")
        }
        return orientation
    }

    /**
     * Get rotation in degrees from EXIF tag.
     */
    @JvmStatic
    fun getRotationFromOrientation(orientation: Int) = when (orientation) {
        android.media.ExifInterface.ORIENTATION_NORMAL -> 0
        android.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90
        android.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180
        android.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }

}
