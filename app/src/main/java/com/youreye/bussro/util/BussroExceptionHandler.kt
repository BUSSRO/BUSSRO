package com.youreye.bussro.util

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Process
import com.youreye.bussro.feature.error.ErrorActivity
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * [BussroExceptionHandler]
 * 안드로이드 앱 오류 처리 대응
 */

class BussroExceptionHandler(
    application: Application,
    private val crashlyticsExceptionHandler: Thread.UncaughtExceptionHandler
) : Thread.UncaughtExceptionHandler {

    private var lastActivity: Activity? = null
    private var activityCount = 0

    /* 최신 화면 정보 갱신 (최근에 실행한 화면 재실행을 위해) */
    init {
        application.registerActivityLifecycleCallbacks(
            object: Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    if (isSkipActivity(activity)) {
                        return
                    }
                    lastActivity = activity
                }

                override fun onActivityStarted(activity: Activity) {
                    if (isSkipActivity(activity)) {
                        return
                    }
                    activityCount++
                    lastActivity = activity
                }

                override fun onActivityResumed(activity: Activity) {
                }

                override fun onActivityPaused(activity: Activity) {
                }

                override fun onActivityStopped(activity: Activity) {
                    if (isSkipActivity(activity)) {
                        return
                    }
                    activityCount--

                    if (activityCount < 0) {
                        lastActivity = null
                    }
                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                }

            }
        )
    }

    private fun isSkipActivity(activity: Activity) = activity is ErrorActivity

    /* 오류 리포팅을 위해 Crashlytics 에 알림 */
    override fun uncaughtException(t: Thread, e: Throwable) {
        lastActivity?.run {
            val stringWriter = StringWriter()
            e.printStackTrace(PrintWriter(stringWriter))

            // 오류 발생을 알리는 ErrorActivity 실행
            startErrorActivity(this, stringWriter.toString())
        }

        // 오류 리포팅
        crashlyticsExceptionHandler.uncaughtException(t, e)

        // 프로세스 종료
//        Process.killProcess(Process.myPid())
//        exitProcess(-1)
    }

    /* 오류 화면 실행 (+ 오류 내용 전달) */
    private fun startErrorActivity(activity: Activity, errorText: String) = activity.run {
        val errorActivityIntent = Intent(this, ErrorActivity::class.java)
            .apply {
                putExtra(ErrorActivity.EXTRA_INTENT, intent)
                putExtra(ErrorActivity.EXTRA_ERROR_TEXT, errorText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        startActivity(errorActivityIntent)
        finish()
    }
}

