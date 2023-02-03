import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.schedule
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {

    buildType(Build)
    buildType(FastTest)
    buildType(SlowTest)
    buildType(Package)

    sequential{
        buildType(Build)
        parallel{
            buildType(FastTest)
            buildType(SlowTest)
        }
        buildType(Package)
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "target/*.jar"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean compile"
            runnerArgs = "-DMaven.test.failure.ignore=true"
            dockerImage = "maven:latest"
        }
    }
})


object FastTest : BuildType({
    name = "Fast Test"

    artifactRules = "target/*.jar"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean test"
            runnerArgs = "-DMaven.test.failure.ignore=true -Dtest=*.unit.*Test"
            dockerImage = "maven:latest"
        }
    }
})

object SlowTest : BuildType({
    name = "Slow Test"

    artifactRules = "target/*.jar"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean test"
            runnerArgs = "-DMaven.test.failure.ignore=true -Dtest=*.integration.*Test"
            dockerImage = "maven:latest"
        }
    }
})

object Package : BuildType({
    name = "Package"

    artifactRules = "target/*.jar"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean package"
            dockerImage = "maven:latest"
        }
    }

    triggers {
        vcs {
        }
        schedule {
            schedulingPolicy = weekly {
            }
            triggerBuild = always()
        }
    }
})