package site.jiyang.module_a

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import site.jy.feature_center.AModuleFeature

@Keep
class AModuleFeatureImpl : AModuleFeature {

    override fun launchToAModule(context: Context) {
        context.startActivity(Intent(context, ModuleAActivity::class.java))
    }
}