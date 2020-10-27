package ted.gun0912.bubbletrashsample

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.*
import androidx.annotation.LayoutRes
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import ted.gun0912.bubbletrashsample.DragTouchListener.BubbleTouchListener
import java.util.*

object BubbleManager {

    private const val TRASH_BOUND = 0.25

    private val windowManager: WindowManager by lazy {
        SampleApplication.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val viewList: MutableList<View> = ArrayList()
    private lateinit var bubbleTrashLayout: BubbleTrashLayout
    private lateinit var bubbleTouchListener: BubbleTouchListener

    fun addBubble(view: View, x: Int, y: Int): WindowManager.LayoutParams {
        val layoutParams = buildLayoutParamsForBubble(x, y, ParamWidth.WRAP_CONTENT)
        setTouchListener(view, layoutParams)
        addViewToWindow(view, layoutParams)
        return layoutParams
    }

    private fun buildLayoutParamsForBubble(
        x: Int,
        y: Int,
        paramWidth: ParamWidth
    ): WindowManager.LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        return WindowManager.LayoutParams(
            paramWidth.value,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            this.x = x
            this.y = y
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(view: View, layoutParams: WindowManager.LayoutParams) {
        val dragTouchListener =
            DragTouchListener(windowManager, layoutParams, view, bubbleTouchListener)
        view.setOnLongClickListener {
            view.setOnTouchListener(dragTouchListener)
            showTrashView(bubbleTouchListener)
            true
        }
    }

    private fun addViewToWindow(view: View, layoutParams: WindowManager.LayoutParams) {
        windowManager.addView(view.rootView, layoutParams)
        viewList.add(view)
    }

    private fun showTrashView(bubbleTouchListener: BubbleTouchListener?) {
        if (bubbleTouchListener == null) {
            return
        }
        bubbleTrashLayout.show()
    }

    fun onDestroy() {
        for (view in viewList) {
            if (view.isAttachedToWindow) {
                windowManager.removeView(view)
            }

        }
    }


    fun addTrashView(context: Context, @LayoutRes trashViewResId: Int) {
        bubbleTrashLayout = BubbleTrashLayout(context).apply {
            LayoutInflater.from(context).inflate(trashViewResId, this, true)
            val layoutParams =
                buildLayoutParamsForBubble(0, 0, ParamWidth.MATCH_PARENT)
            addViewToWindow(this, layoutParams)
        }

        with(bubbleTrashLayout) {
            doOnNextLayout {
                translationY = -bubbleTrashLayout.height.toFloat()
                visibility = View.INVISIBLE
            }
        }

        bubbleTouchListener =
            object : BubbleTouchListener {
                override fun onTouch(
                    v: View,
                    event: MotionEvent,
                    params: WindowManager.LayoutParams
                ) {
                    when (event.action) {
                        MotionEvent.ACTION_MOVE ->
                            if (checkIfBubbleIsOverTrash(v, params)) {
                                applyTrashMagnetismToBubble(v, params)
                            } else {
                                bubbleTrashLayout.releaseMagnetism()
                            }
                        MotionEvent.ACTION_UP ->
                            if (checkIfBubbleIsOverTrash(v, params)) {
                                context.stopService(Intent(context, SampleService::class.java))
                            } else {
                                bubbleTrashLayout.hide()
                            }
                    }
                }
            }
    }

    private fun checkIfBubbleIsOverTrash(
        view: View,
        params: WindowManager.LayoutParams
    ): Boolean {
        if (bubbleTrashLayout.isVisible) {
            with(bubbleTrashLayout) {
                val trashWidth = measuredWidth
                val trashHeight = measuredHeight

                val trashLeft = left - trashWidth * TRASH_BOUND
                val trashRight = left + trashWidth + trashWidth * TRASH_BOUND
                val trashTop = top - trashHeight * TRASH_BOUND
                val trashBottom = top + trashHeight + trashHeight * TRASH_BOUND

                val bubbleWidth = view.measuredWidth
                val bubbleHeight = view.measuredHeight

                val bubbleLeft = params.x
                val bubbleRight = bubbleLeft + bubbleWidth
                val bubbleTop = params.y
                val bubbleBottom = bubbleTop + bubbleHeight

                return bubbleLeft >= trashLeft && bubbleRight <= trashRight && bubbleTop >= trashTop && bubbleBottom <= trashBottom
            }

        }
        return false
    }

    private fun applyTrashMagnetismToBubble(view: View, params: WindowManager.LayoutParams) {
        with(bubbleTrashLayout) {
            applyMagnetism()
            vibrate()
            val trashCenterX = left + measuredWidth / 2
            val trashCenterY = top + measuredHeight / 2
            params.apply {
                x = trashCenterX - view.measuredWidth / 2
                y = trashCenterY - view.measuredHeight / 2
            }
            windowManager.updateViewLayout(view, params)
        }

    }

    fun updateViewLayout(view: View, params: WindowManager.LayoutParams) {
        windowManager.updateViewLayout(view, params)
    }

    enum class ParamWidth(val value: Int) {
        WRAP_CONTENT(WindowManager.LayoutParams.WRAP_CONTENT),
        MATCH_PARENT(WindowManager.LayoutParams.MATCH_PARENT)
    }

}