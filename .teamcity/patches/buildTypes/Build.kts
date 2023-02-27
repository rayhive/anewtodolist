package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Build'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Build")) {
    vcs {
        expectEntry(DslContext.settingsRoot.id!!)
        root(DslContext.settingsRoot.id!!, "+:src/main => .", "+:src/test => src/test")
    }
}
