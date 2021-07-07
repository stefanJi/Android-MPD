# Android-MPD

A kotlin annotation processor util to solve the coupling problem of multiple projects.

## Usage

[![](https://jitpack.io/v/stefanJi/Android-MPD.svg)](https://jitpack.io/#stefanJi/Android-MPD)

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```
dependencies {
    implementation 'com.github.stefanJi:Android-MPD:Tag'
}
```

## RemoteFeature

An annotator to decorate the interface exposed by a module.

`RemoteFeature` source code:

```
@Target(AnnotationTarget.CLASS)
annotation class RemoteFeature(val name: String, val impl: String)
```

- name: Module name
- impl: Full reference of the implementation class of the interface exposed by the module


## Demo project architecture

![](https://github.com/stefanJi/Android-MPD/blob/main/demo/graphviz.png?raw=true)

## Step

### 1. Create a feature_center module

*feature_center/build.gradle*

```
//...
dependencies {
    //...
    kapt(project(':mpd-processor'))
    api(project(':mpd-processor'))
}
```

### 2. Declare the external interface of the module in feature_center and use the `RemoteFeature` annotation to decorate

*site.jy.feature_center.AModuleFeature*:

```
@RemoteFeature("moduleA", "site.jiyang.module_a.AModuleFeatureImpl")
interface AModuleFeature {
    fun launchToAModule(context: Context)
}
```

### 3. Implement the interface inside the module

*site.jiyang.module_a.AModuleFeatureImpl*:

```
@Keep
class AModuleFeatureImpl : AModuleFeature {

    override fun launchToAModule(context: Context) {
        context.startActivity(Intent(context, ModuleAActivity::class.java))
    }
}
```

### 4. Build project

*mdp-processor* will generate a **RemoteFeatures** class, which will contain the implementation of all interfaces decorated with `RemoteFeature` annotation.

Example *demo/feature_center/build/generated/source/kaptKotlin/debug/RemoteFeatures.kt*:

```
public object RemoteFeatures {
  public val moduleB: BModuleFeature by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        var feature: BModuleFeature? = null
        try {
            feature = Class.forName("site.jiyang.module_b.BModuleFeatureImpl").newInstance() as
            BModuleFeature
        } catch (e: ClassNotFoundException) {
        }

        if (feature == null) {
            feature = Proxy.newProxyInstance(
                RemoteFeatures::class.java.classLoader,
                arrayOf<Class<*>>(BModuleFeature::class.java),
                object : InvocationHandler {

                    override operator fun invoke(
                        proxy: Any?,
                        method: Method,
                        args: Array<Any?>?
                    ): Any? {
                        val returnType: Class<*> = method.returnType
                        if (returnType == Boolean::class.javaPrimitiveType) {
                            return false
                        }
                        if (returnType == Int::class.javaPrimitiveType) {
                            return 0
                        }
                        return if (returnType == Float::class.javaPrimitiveType) {
                            0f
                        } else null
                    }
                }) as BModuleFeature
        }
        feature}
}
```

### 5. Call a module exposed interface

Use `RemoteFeatures` to call the interfaces of other modules withou directly depended on other modules.

```
RemoteFeatures.moduleA.launchToAModule(context)
```