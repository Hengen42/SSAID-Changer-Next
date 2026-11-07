# Release signing

All releases starting with **v1.6.1** use the project's permanent release key.
Debug builds still use Android's debug key. Release builds fail if release credentials
are missing; they never fall back to a debug key.

## Local builds

Keep the keystore and its configuration outside the repository. Gradle reads
`~/.android/ssaid-changer-next-release/signing.properties`:

```properties
storeFile=/absolute/path/to/release.p12
storePassword=YOUR_PRIVATE_PASSWORD
keyAlias=YOUR_KEY_ALIAS
keyPassword=YOUR_PRIVATE_PASSWORD
```

Alternatively, set `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`,
`RELEASE_KEY_ALIAS`, and `RELEASE_KEY_PASSWORD` in the environment.
Never commit the keystore, passwords, or configuration. Back up the private key and
configuration to a secure location: losing them prevents signing future updates
with the same identity. Do not regenerate the key for each release.

## GitHub Actions

Repository Actions secrets:

- `RELEASE_KEYSTORE_BASE64`: base64 encoding of the permanent PKCS12 keystore.
- `RELEASE_KEYSTORE_PASSWORD`: keystore and private key password.
- `RELEASE_KEY_ALIAS`: signing key alias.

Repository Actions variable `RELEASE_CERT_SHA256` contains the public certificate's
SHA-256 fingerprint (lowercase hex, without colons). The workflow verifies every
release APK against it before publishing. The public fingerprint is kept in the
repository at `docs/release-certificate.sha256` for developers. Only the APK is
attached to GitHub releases so users have one installation file to choose.

## Moving from v1.6.0 and older

Earlier APKs used debug signing. Android generally cannot install the new release
over an APK with a different signing certificate. **Before uninstalling an old
version, record the original SSAIDs shown for your modified apps.** Uninstalling
clears app-private preferences, including saved per-app original SSAIDs and language
settings. The system-level `.ssaidchanger.bak` file is separate, but it is not a
replacement for preserving per-app records. No automatic data migration is provided.

Install v1.6.1 after preserving those records and removing the old app. Subsequent
releases use the same key and can update normally. Check the certificate before
attempting an upgrade; do not uninstall merely because an unexpected APK fails.

## Play Protect

A consistent signing identity fixes signing continuity. It does **not** guarantee
that Play Protect will stop warning about a previously unknown developer. Google
controls those checks. Do not ask users to disable Play Protect. If a published APK
continues to be blocked, use Google's official developer appeal process:
https://support.google.com/googleplay/android-developer/contact/protectappeals

Review the exact warning and Google's current submission requirements before
submitting; do not claim that the app is Google-certified or reviewed without evidence.
