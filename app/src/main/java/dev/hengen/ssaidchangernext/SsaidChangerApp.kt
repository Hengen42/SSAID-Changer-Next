package dev.hengen.ssaidchangernext

import android.app.Application
import com.topjohnwu.superuser.Shell

class SsaidChangerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Shell.enableVerboseLogging = false
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_MOUNT_MASTER)
                .setTimeout(15)
        )
    }
}
