package site.jy

import RemoteFeatures
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Button(this).also {
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.text = "Launch to A"
            setContentView(it)
            it.setOnClickListener {
                RemoteFeatures.moduleA.launchToAModule(this)
            }
        }
    }
}