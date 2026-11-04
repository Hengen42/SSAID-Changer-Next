# Changelog

## 1.3.0
- **Smart App Sorting:** Previously modified applications are now pinned at the very top of the list with a neon `MODIFIED` badge.
- **In-App Language Selector:** Added interactive language switcher (System Auto, Turkish, English) accessible directly from the app bar and root screen without requiring app restarts.
- **Hardware Locale Resolution:** Fixed locale bug where selecting "System Default" failed to resolve the hardware language after a manual switch.
- **Instant Root Re-check:** Fixed shell caching in root gate; tapping "GRANT ROOT" or "RELOAD" flushes stale sessions and immediately requests fresh `su` privilege.
- **Critical Restart Protection:** Added explicit high-priority warnings to prevent users from opening target banking apps prior to device reboot, preventing newly spoofed SSAIDs from getting immediately blacklisted.
- **Version Code Synchronization:** Synced `versionCode` to 130 matching `versionName` 1.3.0 across all Gradle and UI components.
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
