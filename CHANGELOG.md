# Changelog

## 1.5.0
- **Direct Application List Landing:** Completely redesigned the main screen to launch immediately into the installed app list with integrated top-bar actions, eliminating the empty placeholder card.
- **Neon "Randomize" Action:** Restyled the Randomize button with a full-width vibrant neon green accent for enhanced usability and prominence.
- **Duplicate SSAID Guard:** Added equality validation on apply; prevents accidental re-application when candidate SSAID matches the active SSAID and warns the user.
- **Automated GitHub Releases Workflow:** Pushing to the main branch automatically builds and publishes a GitHub Release with prebuilt APKs attached, eliminating manual release steps.
- **Contributing & Pull Requests:** Added open-source contribution guidelines (Pull Requests) and issue reporting sections to project documentation.
- **Streamlined Documentation:** Polished bilingual README files to focus on end-user APK installation and removed obsolete soft reboot references.

## 1.4.0
- **Full-Screen App Picker:** Converted bottom sheet to an immersive full-screen view with a back arrow, completely eliminating gesture conflicts with pull-to-refresh.
- **Native Pull-to-Refresh:** Added ultra-fast pull-to-refresh with visible neon indicator on the application picker to instantly detect newly installed apps.
- **Smart Back Navigation:** System back button now deselects the active application returning to home state instead of unexpectedly closing the app.
- **Framed Dynamic Warning Button:** Post-apply restart dialog now features a red-framed button displaying the exact target package name to prevent accidental blacklisting.
- **Simplified Controls:** Removed redundant reload button giving the Randomize button full-width prominence; streamlined top-bar layout.
- **Cleaned Reboot Workflow:** Removed experimental soft reboot to eliminate user confusion and ensure 100% reliable hardware restarts.
- **Checkbox Label Simplification:** Streamlined app data cleanup label to explicitly highlight `(RECOMMENDED!!)`.
- **Full Localization Sync:** Synchronized all updated UI strings and dialog layouts across Turkish and English.

## 1.3.0
- **Smart App Sorting:** Previously modified applications are now pinned at the very top of the list with a neon `MODIFIED` badge.
- **In-App Language Selector:** Added interactive language switcher (System Auto, Turkish, English) accessible directly from the app bar and root screen without requiring app restarts.
- **Hardware Locale Resolution:** Fixed locale bug where selecting "System Default" failed to resolve the hardware language after a manual switch.
- **Instant Root Re-check:** Fixed shell caching in root gate; tapping "GRANT ROOT" or "RELOAD" flushes stale sessions and immediately requests fresh `su` privilege.
- **Critical Restart Protection:** Added explicit high-priority warnings to prevent users from opening target banking apps prior to device reboot.
- **Rebranding:** Formally rebranded to **SSAID Changer Next**.

## 1.2.0
- **Two-Tier Backup & Restore:** Added Per-App original factory ID restoration and Full Database recovery from `.ssaidchanger.bak`.
- **Localization (i18n):** Added native English and Turkish translations.
- **Root Gate:** Added complete lock screen when root privilege is missing.

## 1.1.0
- **Zero-Lag Asynchronous App Icon Loading:** Fixed 2-3 second UI freeze on 300+ app systems using on-demand `LruCache` icon decoding.
- **ABX Parsing Optimization:** Streamlined SSAID extraction on Android 12 through 16.

## 1.0.0
- Initial release of SSAID Changer Next.
