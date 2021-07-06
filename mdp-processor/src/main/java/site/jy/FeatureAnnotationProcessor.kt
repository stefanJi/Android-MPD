package site.jy

import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@Target(AnnotationTarget.CLASS)
annotation class RemoteFeature(val name: String, val impl: String)

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("site.jy.RemoteFeature")
@SupportedOptions(FeatureAnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class FeatureAnnotationProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val annotatedElements = roundEnv.getElementsAnnotatedWith(RemoteFeature::class.java)
        if (annotatedElements.isEmpty()) return false

        try {
            val features = TypeSpec.objectBuilder("RemoteFeatures")
            annotatedElements.forEach {
                processAnnotation(it, features)
            }

            val file = FileSpec.builder("", "RemoteFeatures")
                .addImport("java.lang.reflect", "Proxy", "InvocationHandler", "Method")
                .addType(features.build())
                .build()

            file.writeTo(File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!))
        } catch (e: Exception) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "[FeatureAnnotationProcessor] $e"
            )
            throw e
        }

        return true
    }

    private fun processAnnotation(element: Element, features: TypeSpec.Builder) {
        val remoteFeature = element.getAnnotation(RemoteFeature::class.java)
        processingEnv.messager.printMessage(
            Diagnostic.Kind.NOTE,
            "[FeatureAnnotationProcessor] Feature impl: ${remoteFeature.impl} ${element.asType()} ${element.enclosingElement.asType()} ${element.enclosedElements}"
        )

        val type =
            try {
                element.asType().asTypeName()
            } catch (e: Exception) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "[FeatureAnnotationProcessor] $e"
                )
                throw e
            }

        val simpleName = element.simpleName

        val property =
            PropertySpec.builder(remoteFeature.name, type)
                .delegate(
                    CodeBlock.builder()
                        .beginControlFlow(
                            "lazy(mode = %T.SYNCHRONIZED)",
                            LazyThreadSafetyMode::class.asTypeName()
                        )
                        .add(
                            """
                              var feature: ${simpleName}? = null
        try {
            feature = Class.forName("${remoteFeature.impl}").newInstance() as $simpleName
        } catch (e: ClassNotFoundException) {
        }

        // 如果 feature_x 模块未没编译，FeatureX 类将找不到，就动态生成一个 Proxy 类
        if (feature == null) {
            feature = Proxy.newProxyInstance(
                RemoteFeatures::class.java.classLoader,
                arrayOf<Class<*>>(${simpleName}::class.java),
                object : InvocationHandler {

                    override operator fun invoke(
                        proxy: Any?,
                        method: Method,
                        args: Array<Any?>?
                    ): Any? {
                        val returnType: Class<*> = method.returnType
                        // 让原始类型返回零值
                        if (returnType == Boolean::class.javaPrimitiveType) {
                            return false
                        }
                        if (returnType == Int::class.javaPrimitiveType) {
                            return 0
                        }
                        return if (returnType == Float::class.javaPrimitiveType) {
                            0f
                        } else null
                        //...
                        // 让引用类型返回 null
                    }
                }) as $simpleName
        }

        feature
                        """.trimIndent()
                        )
                        .endControlFlow()
                        .build()
                )
                .build()

        features.addProperty(property)
    }
}