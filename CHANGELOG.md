# Changelog

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
