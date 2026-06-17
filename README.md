# Azimuth Mysterium Bridge

Android library module integrating [Mysterium Network](https://mysterium.network) VPN provider node into the Azimuth Observer app. Enables users to contribute bandwidth to the decentralized VPN network and earn bonus rewards.

Licensed under GPL v3.

## Integration

Add as a Git submodule in your Android project:

```bash
git submodule add https://github.com/Azimuth-Official/azimuth-mysterium.git
```

In your `settings.gradle.kts`:
```kotlin
include(":mysterium-bridge")
project(":mysterium-bridge").projectDir = file("azimuth-mysterium/mysterium-bridge")
```

In your app `build.gradle.kts`:
```kotlin
implementation(project(":mysterium-bridge"))
```

## About Azimuth

[Azimuth](https://azimuth.day) is a Signals-of-Opportunity (SoOP) DePIN positioning and timing network.
