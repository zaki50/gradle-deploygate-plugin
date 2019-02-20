package com.deploygate.gradle.plugins.internal.gradle

import com.deploygate.gradle.plugins.internal.VersionString
import org.gradle.api.Project
import org.gradle.api.Task

import javax.annotation.Nonnull

class GradleCompat {
    private static VersionString GRADLE_VERSION

    private GradleCompat() {
    }

    static void init(@Nonnull Project project) {
        GRADLE_VERSION = VersionString.tryParse(project.gradle.gradleVersion)
    }

    @Nonnull
    static VersionString getVersion() {
        if (!GRADLE_VERSION) {
            throw new IllegalStateException("must be initialized")
        }

        return GRADLE_VERSION
    }

    @Nonnull
    static <T extends Task> LazyConfigurableTask<T> newLazyConfigurableTask(@Nonnull Project project, @Nonnull String taskName, @Nonnull Class<T> klass) {
        if (version.major > 4 || version.major == 4 && version.minor >= 8) {
            return new TaskProvider(project.tasks.register(taskName, klass))
        } else {
            return new SingleTask(project.tasks.create(taskName, klass))
        }
    }

    static void configureEach(@Nonnull any, @Nonnull Closure closure) {
        if (version.major > 4 || version.major == 4 && version.minor >= 9) {
            any.configureEach(closure)
        } else {
            any.all(closure)
        }
    }
}
