package ted.gun0912.bubbletrashsample

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TedPermission.with(this)
            .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    val intent = Intent(this@MainActivity, SampleService::class.java)
                    startService(intent)
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                }
            })
            .check()


    }
}