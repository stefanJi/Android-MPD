package site.jy.feature_center

import android.content.Context
import site.jy.RemoteFeature

@RemoteFeature("moduleB", "site.jiyang.module_b.BModuleFeatureImpl")
interface BModuleFeature {
    fun launchToBModule(context: Context)
}