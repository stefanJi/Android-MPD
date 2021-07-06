package site.jiyang.module_a

import RemoteFeatures
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity

class ModuleAActivity : FragmentActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Button(this).also {
            it.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.text = "launch to B"
            setContentView(it)
            it.setOnClickListener {
                RemoteFeatures.moduleB.launchToBModule(this)
            }
        }
    }
}