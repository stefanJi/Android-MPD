package site.jiyang.module_b

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import site.jy.feature_center.BModuleFeature

@Keep
class BModuleFeatureImpl : BModuleFeature {

    override fun launchToBModule(context: Context) {
        context.startActivity(Intent(context, ModuleBActivity::class.java))
    }
}