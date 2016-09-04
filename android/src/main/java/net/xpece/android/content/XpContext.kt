@file:JvmName("XpContext")

package net.xpece.android.content

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.PowerManager
import android.support.annotation.*
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.NotificationCompat
import android.support.v7.content.res.AppCompatResources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import net.xpece.android.R

/**
 * @author Eugen on 28.07.2016.
 */

private val TYPED_VALUE = TypedValue()

@UiThread
fun Context.ensureRuntimeTheme() {
    theme.resolveAttribute(R.attr.ltRuntimeTheme, TYPED_VALUE, true)
    val resourceId = TYPED_VALUE.resourceId
    if (resourceId != 0) {
        setTheme(resourceId)
    }
}

fun <T : Activity> Context.startActivity(activity: Class<T>, func: Intent.() -> Unit = {}) {
    val intent = Intent(this, activity)
    intent.func()
    startActivity(intent)
}

fun <T : Activity> Fragment.startActivity(activity: Class<T>, func: Intent.() -> Unit = {}) {
    val intent = Intent(this.context, activity)
    intent.func()
    startActivity(intent)
}

fun <T : Activity> android.app.Fragment.startActivity(activity: Class<T>, func: Intent.() -> Unit = {}) {
    val intent = Intent(this.activity, activity)
    intent.func()
    startActivity(intent)
}

fun <T : Activity> Context.createIntent(activity: Class<T>, func: Intent.() -> Unit = {}): Intent {
    val intent = Intent(this, activity)
    intent.func()
    return intent
}

fun Context.view(uri: String, func: Intent.() -> Unit = {})
        = view(Uri.parse(uri), func)

fun Context.view(uri: Uri, func: Intent.() -> Unit) {
    val i = Intent(Intent.ACTION_VIEW, uri)
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    i.func()
    if (!maybeStartActivity(i)) {
        showNoActivityError(this)
    }
}

fun Context.openPlayStore(packageName: String = this.packageName)
        = view("http://play.google.com/store/apps/details?id=$packageName")

@UiThread
fun Context.getLayoutInflater(): LayoutInflater =
        LayoutInflater.from(this)

@UiThread
fun Context.inflate(@LayoutRes layout: Int): View =
        getLayoutInflater().inflate(layout, null, false)

@UiThread
fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = true): View =
        context.getLayoutInflater().inflate(layout, this, attachToRoot)

@ColorInt
fun Context.getColorCompat(@ColorRes resId: Int): Int =
        AppCompatResources.getColorStateList(this, resId)!!.defaultColor

fun Context.getColorStateListCompat(@ColorRes resId: Int): ColorStateList? =
        AppCompatResources.getColorStateList(this, resId)

fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable? =
        AppCompatResources.getDrawable(this, resId)

fun Context.grantUriPermission(intent: Intent, uri: Uri, modeFlags: Int) {
    val resolvedIntentActivities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    for (resolvedIntentInfo in resolvedIntentActivities) {
        val packageName = resolvedIntentInfo.activityInfo.packageName
        grantUriPermission(packageName, uri, modeFlags)
    }
}

/**
 * Attempt to launch the supplied [Intent]. Queries on-device packages before launching and
 * will display a simple message if none are available to handle it.
 */
fun Context.maybeStartActivity(intent: Intent) = maybeStartActivity(intent, false)

/**
 * Attempt to launch Android's chooser for the supplied [Intent]. Queries on-device
 * packages before launching and will display a simple message if none are available to handle
 * it.
 */
fun Context.maybeStartChooser(intent: Intent) = maybeStartActivity(intent, true)

fun Context.maybeStartActivity(intent: Intent, chooser: Boolean): Boolean {
    var intent = intent
    if (hasHandler(intent)) {
        if (chooser) {
            intent = Intent.createChooser(intent, null)
        }
        startActivity(intent)
        return true
    } else {
        //            showNoActivityError(context);
        return false
    }
}

fun Activity.maybeStartActivityForResult(intent: Intent, requestCode: Int): Boolean {
    if (hasHandler(intent)) {
        startActivityForResult(intent, requestCode)
        return true
    } else {
        //            showNoActivityError(activity);
        return false
    }
}

fun Fragment.maybeStartActivityForResult(intent: Intent, requestCode: Int): Boolean {
    val context = context
    if (context.hasHandler(intent)) {
        startActivityForResult(intent, requestCode)
        return true
    } else {
        //            showNoActivityError(context);
        return false
    }
}

fun android.app.Fragment.maybeStartActivityForResult(intent: Intent, requestCode: Int): Boolean {
    val context = activity
    if (context.hasHandler(intent)) {
        startActivityForResult(intent, requestCode)
        return true
    } else {
        //            showNoActivityError(context);
        return false
    }
}

fun showNoActivityError(context: Context) = Toast.makeText(context, R.string.xpc_no_intent_handler, Toast.LENGTH_LONG).show()

/**
 * Queries on-device packages for a handler for the supplied [Intent].
 */
fun Context.hasHandler(intent: Intent) = packageManager.queryIntentActivities(intent, 0).isNotEmpty()

fun Context.notification(func: NotificationCompat.Builder.() -> Unit): NotificationCompat.Builder {
    val builder = NotificationCompat.Builder(this)
    builder.func()
    return builder
}

val Context.notificationManager: NotificationManagerCompat
    get() = NotificationManagerCompat.from(this)
val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
val Context.audioManager: AudioManager
    get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager
val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
val Context.locationManager: LocationManager
    get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager
val Context.powerManager: PowerManager
    get() = getSystemService(Context.POWER_SERVICE) as PowerManager

val Context.isRtl: Boolean
    get() = resources.configuration.layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL

val Context.isDebugBuild: Boolean
    get() = 0 != (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)