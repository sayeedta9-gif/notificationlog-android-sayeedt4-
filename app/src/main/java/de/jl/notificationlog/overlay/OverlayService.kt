package de.jl.notificationlog.overlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ImageView
import de.jl.notificationlog.R
import de.jl.notificationlog.ui.AppListActivity

class OverlayService: Service() {
    private val windowManager: WindowManager by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onCreate() {
        super.onCreate()

        val buttonLayoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        ).apply {
            x = 0
            y = 0

            gravity = Gravity.TOP or Gravity.LEFT
        }

        fun open() {
            startActivity(
                    Intent(this, AppListActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
            )
        }

        windowManager.addView(
                ImageView(this).apply {
                    setImageResource(R.mipmap.ic_launcher)

                    var deltaX = 0
                    var deltaY = 0
                    var didMove = false

                    setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            deltaX = event.rawX.toInt() - buttonLayoutParams.x
                            deltaY = event.rawY.toInt() - buttonLayoutParams.y

                            didMove = false
                        } else if (event.action == MotionEvent.ACTION_MOVE) {
                            didMove = true

                            buttonLayoutParams.x = event.rawX.toInt() - deltaX
                            buttonLayoutParams.y = event.rawY.toInt() - deltaY

                            windowManager.updateViewLayout(this, buttonLayoutParams)
                        } else if (event.action == MotionEvent.ACTION_UP) {
                            if (!didMove) {
                                open()
                            }
                        }

                        false
                    }
                },
                buttonLayoutParams
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw NotImplementedError()
    }
}