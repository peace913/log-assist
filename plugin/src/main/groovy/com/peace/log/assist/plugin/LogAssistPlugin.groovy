package com.peace.log.assist.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class LogAssistPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.android.registerTransform(new LogAssistTransform(project))
    }
}