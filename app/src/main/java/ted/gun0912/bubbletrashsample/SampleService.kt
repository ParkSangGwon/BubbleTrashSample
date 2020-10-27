package ted.gun0912.bubbletrashsample

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.doOnLayout

class SampleService : Service() {

    private lateinit var viewBubble: View
    private lateinit var viewTrash: View

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()


        val layoutInflater = LayoutInflater.from(this)
        viewBubble = layoutInflater.inflate(R.layout.view_bubble, null, false)
        viewTrash = layoutInflater.inflate(R.layout.view_trash, null, false)

        addBubble()
    }

    private fun addBubble() {
        BubbleManager.addTrashView(this, R.layout.view_trash)

        val layoutParams =
            BubbleManager.addBubble(viewBubble, 0, 0)
        viewBubble.doOnLayout { view ->
            val displayMetrics = resources.displayMetrics
            layoutParams.x = displayMetrics.widthPixels / 2 - view.width / 2
            layoutParams.y = displayMetrics.heightPixels - view.height / 2
            BubbleManager.updateViewLayout(viewBubble, layoutParams)
        }

    }

    override fun onDestroy() {
        BubbleManager.onDestroy()
        super.onDestroy()
    }
}