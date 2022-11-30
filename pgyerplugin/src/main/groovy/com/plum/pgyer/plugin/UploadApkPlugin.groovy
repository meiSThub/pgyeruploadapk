package com.plum.pgyer.plugin

import com.plum.pgyer.plugin.bean.PgyerExtension
import com.plum.pgyer.plugin.tasks.UploadApkTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 使用 蒲公英的 API，上传APK到 蒲公英平台，并分享Apk，方便下载与安装
 */
class UploadApkPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "插件生效"
        if (!project.plugins.hasPlugin("com.android.application")) {
            println "只有App工程，才可以使用该插件上传Apk"
            return
        }
        // 创建扩展属性
        def extension = project.extensions.create('pgyer', PgyerExtension)

        // 创建上传Apk任务
        project.android.applicationVariants.all { variant ->
            println "varant=$variant"
            println "variant name = ${variant.name.capitalize()}"
            UploadApkTask task = project.task("upload${variant.name.capitalize()}Apk", type: UploadApkTask)
            task.group = "pgyer"
            task.description = "Upload an APK file of ${variant.name}"
            println "buildType=${variant.buildType.name}"
            println "productFlavor=${variant.productFlavors}"
            task.buildType = variant.buildType.name
            if (variant.productFlavors != null && variant.productFlavors.first() != null) {
                println "productFlavor=${variant.productFlavors.first().name}"
                task.flavor = variant.productFlavors.first().name
            }
            def assembleTaskName = "assemble${variant.name.capitalize()}"
            println "assembleTaskName=$assembleTaskName"
            def assembleTask = project.tasks.findByPath(assembleTaskName)
            println "assembleTask=$assembleTask"
            // 打包任务执行完成之后，在执行上传Apk的任务
            assembleTask.finalizedBy(task)
        }
    }
}