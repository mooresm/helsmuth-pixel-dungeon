# iOS Build & Installation Guide

> **Project:** Shattered Pixel Dungeon v3.3.5 (Build 890)
> **Framework:** LibGDX 1.14.0 + RoboVM 2.3.24
> **Audience:** Internal developers

---

## Important: This Is Not an Xcode Project

Shattered Pixel Dungeon is a **Java/LibGDX project** — it is not opened or built directly in Xcode. Instead, the `ios` module uses **RoboVM** to compile Java to native iOS code. Xcode's toolchain is used under the hood by RoboVM, but you work through **IntelliJ IDEA** and **Gradle**, not Xcode itself.

---

## Prerequisites

Before building for iOS, make sure you have the following installed and configured:

- **macOS** (iOS builds require a Mac)
- **Xcode** with Command Line Tools (`xcode-select --install`)
- **IntelliJ IDEA** with the RoboVM plugin
- A valid **Apple Developer account** (free or paid — see section below)
- Your iPad's UDID registered, if using a paid account (free accounts handle this automatically via Xcode)

---

## Compiling the iOS Build

Open the project in IntelliJ IDEA and run one of the following Gradle tasks from the terminal:

```bash
# Launch directly on a connected device (build + deploy in one step)
./gradlew ios:launchIOSDevice

# Launch on the iOS Simulator
./gradlew ios:launchIPhoneSimulator

# Build a distributable IPA file
./gradlew ios:createIPA
```

---

## Installing on an iPad

There are three options for getting the app onto a physical iPad.

### Option 1: Direct Deploy via Gradle (Recommended)

The simplest workflow for active development.

1. Connect your iPad to your Mac via USB.
2. Unlock your iPad and tap **Trust** when prompted.
3. Run:
   ```bash
   ./gradlew ios:launchIOSDevice
   ```
   This builds and installs the app in a single step.

### Option 2: Manual Install via Xcode

Use this if you already have an IPA and want to install it without rebuilding.

1. Build the IPA first:
   ```bash
   ./gradlew ios:createIPA
   ```
2. Open Xcode and go to **Window > Devices and Simulators**.
3. Select your iPad from the left sidebar.
4. Drag and drop the `.ipa` file onto the **Installed Apps** list.

### Option 3: Apple Configurator 2

> ⚠️ **Requires a paid Apple Developer account.** This will not work with a free account.

1. Install [Apple Configurator 2](https://apps.apple.com/app/apple-configurator-2/id1037126344) from the Mac App Store.
2. Build the IPA: `./gradlew ios:createIPA`
3. Connect your iPad and drag the `.ipa` onto the device in Apple Configurator.

---

## Free vs. Paid Apple Developer Account

| Feature | Free Account | Paid Account ($99/yr) |
|---|---|---|
| Install on your own device | ✅ | ✅ |
| Max apps installed at once | 3 | Unlimited |
| Certificate validity | **7 days** | 1 year |
| UDID registration required | No (Xcode handles it) | Yes |
| Distribute to other devices | ❌ | ✅ |
| TestFlight / App Store | ❌ | ✅ |
| Apple Configurator 2 sideloading | ❌ | ✅ |

### The 7-Day Expiry (Free Accounts)

With a free account, the app's signing certificate expires every **7 days**, after which the app will refuse to launch. To fix this, simply rebuild and reinstall. Each time you do, you'll also need to re-trust the certificate on the iPad:

**Settings > General > VPN & Device Management > [Your Apple ID] > Trust**

For active development this is manageable, since you'll likely be rebuilding regularly anyway. A paid account is only necessary when you're ready to distribute to testers or submit to the App Store.

---

## Before Any of This Will Work

Make sure the following are in order in your project config:

- `ios/robovm.xml` — must have a valid signing identity and provisioning profile
- `ios/Info.plist.xml` — must have a valid bundle ID matching your Apple Developer account

If you run into code signing errors, check that your provisioning profile is up to date in Xcode under **Settings > Accounts > [Your Apple ID] > Manage Certificates**.
