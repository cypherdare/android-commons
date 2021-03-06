package net.xpece.android.net

import android.content.BroadcastReceiver
import android.content.Context
import android.support.annotation.CheckResult
import io.reactivex.Flowable
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.processors.BehaviorProcessor

/**
 * @author Eugen on 04.01.2017.
 */

object ReactiveConnectivity {
    /**
     * Creates a new [Flowable] broadcasting changes in connectivity reported by system as
     * [ConnectivityInfo] objects.
     *
     * A typical flow would look like this:
     *
     *      ReactiveConnectivity.observe(context)
     *          .subscribeOn(Schedulers.computation())
     *          .observeOn(AndroidSchedulers.mainThread())
     *          .skipWhile { it.isConnected } // Do not show Snackbar if starting in connected state.
     *          .compose(RxLifecycleAndroid.bindActivity(lifecycle()))
     *          .subscribe {
     *              showConnectivitySnackbar(it)
     *          }
     * </code></pre>
     *
     * @param context Instance of [Context] able to register [BroadcastReceiver]s.
     */
    @CheckResult
    @JvmStatic
    @SchedulerSupport(SchedulerSupport.NONE)
    fun observe(context: Context): Flowable<ConnectivityInfo> {
        val connectivitySubject = BehaviorProcessor.create<ConnectivityInfo>()

        val connectivityReceiver = ConnectivityReceiver.newInstance(context.applicationContext)

        return Flowable.fromCallable {
            val connectivityCallback = ConnectivityCallback {
                connectivitySubject.onNext(it)
            }
            connectivityReceiver.register(connectivityCallback)
            return@fromCallable connectivityCallback
        }.flatMap {
            var flowable = connectivitySubject.doOnCancel {
                connectivityReceiver.unregister(it)
            }
            flowable = flowable.startWith(ConnectivityInfo.DEFAULT)
            flowable = flowable.distinctUntilChanged()
            return@flatMap flowable
        }
    }
}
