package site.jy.feature_center

import android.content.Context
import site.jy.RemoteFeature

@RemoteFeature("moduleA", "site.jiyang.module_a.AModuleFeatureImpl")
interface AModuleFeature {
    fun launchToAModule(context: Context)
}