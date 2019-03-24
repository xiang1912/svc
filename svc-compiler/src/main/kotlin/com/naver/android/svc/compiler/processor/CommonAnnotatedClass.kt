package com.naver.android.svc.compiler.processor

import com.squareup.kotlinpoet.ClassName

/**
 * @author bs.nam@navercorp.com
 */
interface CommonAnnotatedClass{
    fun getClass(annotation: Annotation): ClassName {
        val packgeIndexActivity = annotation.toString().lastIndexOf("=")
        val activityPackage = annotation
            .toString()
            .substring(packgeIndexActivity + 1, annotation.toString().length - 1)

        var indexActivity = activityPackage.lastIndexOf(".")
        if (indexActivity == -1) indexActivity = activityPackage.lastIndexOf("\\.")

        val activityName = activityPackage.substring(indexActivity + 1)
        val activity = ClassName(activityPackage.substring(0, indexActivity), activityName)
        return activity
    }
}