package ted.gun0912.bubbletrashsample

import android.annotation.SuppressLint
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager

class DragTouchListener(
    private val windowManager: WindowManager,
    private val params: WindowManager.LayoutParams,
    private val dragView: View,
    private val bubbleTouchListener: BubbleTouchListener
) : OnTouchListener {

    private val metrics = DisplayMetrics().apply {
        windowManager.defaultDisplay.getMetrics(this)
    }

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var needInitialize = true
    private val maxX = metrics.widthPixels - dragView.width
    private val maxY = metrics.heightPixels - dragView.height


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> bubbleTouchListener.onTouch(v, event, params)
            MotionEvent.ACTION_UP -> {
                dragView.setOnTouchListener(null)
                dragView.isClickable = true
                needInitialize = true
                bubbleTouchListener.onTouch(v, event, params)
            }
            MotionEvent.ACTION_MOVE -> {
                if (needInitialize) {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    needInitialize = false
                }
                val resultX = initialX + (event.rawX - initialTouchX).toInt()
                if (resultX in 0..maxX) {
                    params.x = resultX
                }
                val resultY = initialY + (event.rawY - initialTouchY).toInt()
                if (resultY in 0..maxY) {
                    params.y = resultY
                }
                windowManager.updateViewLayout(dragView, params)
            }
        }
        bubbleTouchListener.onTouch(v, event, params)
        return false
    }

    interface BubbleTouchListener {
        fun onTouch(v: View, event: MotionEvent, params: WindowManager.LayoutParams)
    }

}