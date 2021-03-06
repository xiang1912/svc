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


import com.naver.android.svc.compiler.processor.CommonGenerator
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class CustomScreenExtendsGenerator(private val packageName: String, private val screenAnnotatedClass: CustomScreenAnnotatedClass) :
    CommonGenerator {

    private val createControlTowerMethodSpec: FunSpec
        get() = FunSpec.builder("createControlTower")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addStatement(
                String.format("return %s()", screenAnnotatedClass.controlTowerName))
            .returns(screenAnnotatedClass.controlTower)
            .build()

    private val createViewsMethodSpec: FunSpec
        get() = FunSpec.builder("createViews")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.OVERRIDE)
            .addStatement(String.format("return %s()", screenAnnotatedClass.baseViewName))
            .returns(screenAnnotatedClass.baseView)
            .build()

    private val controlTowerParamSpec: PropertySpec
        get() = PropertySpec.builder("controlTower", screenAnnotatedClass.controlTower)
            .addModifiers(KModifier.PUBLIC)
            .getter(FunSpec.getterBuilder()
                .addStatement("return baseControlTower as ${screenAnnotatedClass.controlTowerName}")
                .build())
            .build()

    private val viewsParamSpec: PropertySpec
        get() = PropertySpec.builder("views", screenAnnotatedClass.baseView)
            .addModifiers(KModifier.PUBLIC)
            .getter(FunSpec.getterBuilder()
                .addStatement("return baseViews as ${screenAnnotatedClass.baseViewName}")
                .build())
            .build()

    override val extendsName: String
        get() = "SVC_" + this.screenAnnotatedClass.simpleName

    fun generate(): TypeSpec {
        val builder = TypeSpec.classBuilder(extendsName)
            .addKdoc(
                "Generated by SVC processor. (https://github.com/naver/svc). Don't change this class.")
            .addModifiers(KModifier.PUBLIC)
            .addModifiers(KModifier.ABSTRACT)
            .addFunction(createViewsMethodSpec)
            .addFunction(createControlTowerMethodSpec)
            .addProperty(viewsParamSpec)
            .addProperty(controlTowerParamSpec)
            .superclass(this.screenAnnotatedClass.superClass)
        return builder.build()
    }
}
