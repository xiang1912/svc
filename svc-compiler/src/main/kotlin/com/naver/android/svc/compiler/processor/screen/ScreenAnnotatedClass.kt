/*
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package com.naver.android.svc.compiler.processor.screen

import com.google.common.base.VerifyException
import com.naver.android.svc.annotation.*
import com.naver.android.svc.compiler.processor.CommonAnnotatedClass
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

class ScreenAnnotatedClass @Throws(VerifyException::class)
constructor(val annotatedElement: TypeElement, elementUtils: Elements) : CommonAnnotatedClass {
    val packageName: String?
    val clazzName: String

    var baseView: ClassName
    val baseViewName: String
        get() = baseView.simpleName

    var controlTower: ClassName
    val controlTowerName: String
        get() = controlTower.simpleName

    var dialogListener: ClassName? = null
    val dialogListenerName: String?
        get() {
            dialogListener ?: return null
            return dialogListener!!.simpleName
        }
    var superClass: TypeName? = null

    init {
        val packageElement = elementUtils.getPackageOf(annotatedElement)
        this.packageName = if (packageElement.isUnnamed) null else packageElement.qualifiedName.toString()
        this.clazzName = annotatedElement.simpleName.toString()

        val requireViews = annotatedElement.getAnnotation(RequireViews::class.java)
        this.baseView = getClass(requireViews)

        val requireControlTower = annotatedElement.getAnnotation(RequireControlTower::class.java)
        this.controlTower = getClass(requireControlTower)

        val svcActivity = annotatedElement.getAnnotation(SvcActivity::class.java)
        val svcFragment = annotatedElement.getAnnotation(SvcFragment::class.java)
        val svcDialogFragment = annotatedElement.getAnnotation(SvcDialogFragment::class.java)

        when {
            svcActivity != null -> this.superClass = ClassName("com.naver.android.svc.core.screen", "SvcActivity")
            svcFragment != null -> this.superClass = ClassName("com.naver.android.svc.core.screen", "SvcFragment")
            svcDialogFragment != null -> {
                val requireListener = annotatedElement.getAnnotation(RequireListener::class.java)
                if (requireListener != null) {
                    this.dialogListener = getClass(requireListener)
                } else {
                    this.dialogListener = ClassName("kotlin", "Unit")
                }
                val svcDialogFragmentClassName = ClassName("com.naver.android.svc.core.screen", "SvcDialogFragment")
                this.superClass = svcDialogFragmentClassName.parameterizedBy(this.dialogListener!!)
            }
        }
    }
}
