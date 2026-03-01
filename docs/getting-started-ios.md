# iOS Build & Installation Guide

> **Project:** Helsmuth Dungeon ⁓ 
> **Framework:** LibGDX 1.14.0 + RoboVM 2.3.24 ⁓
> **Audience:** Internal developers

---

## Important: This Is Not an Xcode Project

Helsmuth Dungeon is a **Java/LibGDX project** — it is not opened or built directly in Xcode. Instead, the `ios` module uses **RoboVM** to compile Java to native iOS code. Xcode's toolchain is used under the hood by RoboVM, but your actual development workflow uses **Gradle** from the terminal.

---

## Prerequisites

Before building for iOS, make sure you have the following installed and configured:

- **macOS** (iOS builds require a Mac)
- **Xcode** (full install, not just Command Line Tools) with **iOS support** selected during setup
- A valid **Apple Developer account** (free or paid — see section below)
- A physical iOS device connected via USB (required for provisioning, even on a free account)

---

## One-Time Setup

These steps only need to be done once per developer.

### 1. Install Xcode

Download Xcode from the Mac App Store. When it launches for the first time, make sure to select **iOS** as a supported platform (8.39 GB download). macOS support is built in.

### 2. Create a Signing Certificate

1. Open Xcode and go to **Settings > Accounts**
2. Sign in with your Apple ID
3. Click **Manage Certificates**
4. Click **+** and select **Apple Development**

### 3. Create a Provisioning Profile

1. Connect your iPad to your Mac via USB
2. Unlock your iPad and tap **Trust** when prompted
3. Enable **Developer Mode** on your iPad: **Settings > Privacy & Security > Developer Mode** (requires a restart)
4. In Xcode, open any project (or create a throwaway one) and set the Bundle Identifier to `com.helsmuth.dungeon.apple` under **Signing & Capabilities**, with **Automatically manage signing** checked
5. Xcode will generate the provisioning profile automatically

### 4. Find Your Signing Identity

Run this in the terminal to get your exact signing identity string:

```bash
security find-identity -v -p codesigning
```

It will output something like:
```
1) D1BD561923AD5DBD17E92CA81E9FE14860EC5219 "Apple Development: you@example.com (6TAD2F6N48)"
```

### 5. Configure local.properties

Create a `local.properties` file in the root of the project (this file is gitignored and should never be committed). Add your signing details:

```properties
iosSignIdentity=Apple Development: you@example.com (6TAD2F6N48)
iosProvisioningProfile=com.helsmuth.dungeon.apple
```

This is required before any iOS Gradle tasks will work.

---

## Building and Installing

```bash
# Launch directly on a connected device (build + deploy in one step, recommended)
./gradlew ios:launchIOSDevice

# Launch on the iOS Simulator (no signing required)
./gradlew ios:launchIPhoneSimulator

# Build a distributable IPA file
./gradlew ios:createIPA
```

### After Installing: Trust the Developer Certificate

The first time you install, iOS will block the app with an "Untrusted Developer" message. To fix it:

1. On your iPad go to **Settings > General > VPN & Device Management**
2. Tap your Apple ID under "Developer App"
3. Tap **Trust** and confirm

---

## Installing an IPA on an iPad

If you've built an IPA and want to install it manually:

**Via Xcode:**
1. Open Xcode and go to **Window > Devices and Simulators**
2. Select your iPad from the left sidebar
3. Drag and drop the `.ipa` file onto the **Installed Apps** list

**Via Apple Configurator 2:**
> ⚠️ Requires a paid Apple Developer account. Does not work with a free account.

1. Install [Apple Configurator 2](https://apps.apple.com/app/apple-configurator-2/id1037126344) from the Mac App Store
2. Connect your iPad and drag the `.ipa` onto the device

---

## Free vs. Paid Apple Developer Account

| Feature | Free Account | Paid Account ($99/yr) |
|---|---|---|
| Install on your own device | ✅ | ✅ |
| Max apps installed at once | 3 | Unlimited |
| Certificate validity | **7 days** | 1 year |
| Distribute to other devices | ❌ | ✅ |
| TestFlight / App Store | ❌ | ✅ |
| Apple Configurator 2 sideloading | ❌ | ✅ |

### The 7-Day Expiry (Free Accounts)

With a free account, the signing certificate expires every **7 days**, after which the app will refuse to launch. To fix it, rebuild and reinstall, then re-trust the certificate:

**Settings > General > VPN & Device Management > [Your Apple ID] > Trust**

A paid account is only necessary when you're ready to distribute to testers or submit to the App Store.
