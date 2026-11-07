package dev.hengen.ssaidchangernext.data.update

import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class GitHubUpdateCheckerTest {
    private fun release(tag: String, flags: String = "", url: String =
        "https://github.com/Hengen42/SSAID-Changer-Next/releases/tag/$tag"
    ) = """{"tag_name":"$tag","html_url":"$url"$flags}"""

    @Test fun newerStableReleaseIsOffered() {
        val update = GitHubUpdateChecker.parseRelease(release("v1.6.0"), "1.5.0")!!
        assertEquals("1.6.0", update.version)
        assertTrue(update.pageUrl.endsWith("/v1.6.0"))
    }

    @Test fun versionsAreComparedNumerically() {
        assertNotNull(GitHubUpdateChecker.parseRelease(release("v1.10.0"), "1.9.0"))
        assertNotNull(GitHubUpdateChecker.parseRelease(release("v2.0.0"), "1.99.99"))
        assertNull(GitHubUpdateChecker.parseRelease(release("v1.9.0"), "1.10.0"))
    }

    @Test fun equalAndOlderVersionsAreNotOffered() {
        assertNull(GitHubUpdateChecker.parseRelease(release("v1.5.0"), "1.5.0"))
        assertNull(GitHubUpdateChecker.parseRelease(release("v1.5"), "1.5.0"))
        assertNull(GitHubUpdateChecker.parseRelease(release("v1.4.0"), "1.5.0"))
    }

    @Test fun draftsAndPrereleasesAreIgnored() {
        assertNull(GitHubUpdateChecker.parseRelease(release("v2.0.0", ",\"draft\":true"), "1.5.0"))
        assertNull(GitHubUpdateChecker.parseRelease(release("v2.0.0-beta", ",\"prerelease\":true"), "1.5.0"))
    }

    @Test(expected = IOException::class) fun malformedVersionIsRejected() {
        GitHubUpdateChecker.parseRelease(release("latest"), "1.5.0")
    }

    @Test(expected = IOException::class) fun unrelatedReleaseUrlIsRejected() {
        GitHubUpdateChecker.parseRelease(release("v2.0.0", url = "https://example.com/app.apk"), "1.5.0")
    }

    @Test(expected = IOException::class) fun insecureReleaseUrlIsRejected() {
        GitHubUpdateChecker.parseRelease(release("v2.0.0", url =
            "http://github.com/Hengen42/SSAID-Changer-Next/releases/tag/v2.0.0"), "1.5.0")
    }
}
