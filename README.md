# Additional Notes for running an updated Android build and self hosted Maven repo

## Building the latest MaplibreAndroidSDK

This is all done from Docker using the following commands

- _docker run --rm -it ghcr.io/maplibre/android-ndk-r21b_

from inside the running container then run

- _git clone --recurse-submodules --depth 1 https://github.com/maplibre/maplibre-gl-native.git_
- _cd maplibre-gl-native/platform/android_

To build and run the tests run:

- _make android-check_

To build the aar package run:

- _make apackage_

It's possible to prefix the build type

- _BUILDTYPE=Debug make apackage_ or

- _BUILDTYPE=Release make apackage_

copying a file from the container to the host desktop can be down with

- _docker container ls_ to return the names of the running containers

- _docker cp container_id_from_above:/foo.txt foo.txt_

## Maven upload commands for generic repository (currently using repsy)

Install Maven by downloading, extracting and confguring PATH variable

Add a settings.xml file C:\Users\<username>\.m2 containing the following settings.xml

```xml
<settings
	xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
	<localRepository/>
	<interactiveMode/>
	<offline/>
	<pluginGroups/>
	<servers>
		<server>
			<id>repsy</id>
			<username>username</username>
			<password>password</password> <!-- using &amp; for & -->
		</server>
	</servers>
	<mirrors/>
	<proxies/>
	<profiles/>
	<activeProfiles/>
</settings>
```

### Deploy without a pom file (will upload the aar file but no dependencies will be resolved (_NOT THE PREFERRED SOLUTION!!_)

- _mvn -X deploy:deploy-file "-DrepositoryId=repsy" "-Durl=https://repo.repsy.io/mvn/stevehayles/maplibre-sdk/" "-DgeneratePom=true" "-DgroupId=org.maplibre.gl" "-DartifactId=android-sdk" "-Dversion=9.5.2-SNAPSHOT" "-Dfile=android-sdk.aar"_

### Upload the AAR and POM file

- _mvn -X deploy:deploy-file "-DrepositoryId=repsy" "-Durl=https://repo.repsy.io/mvn/stevehayles/maplibre-sdk/" "-DgeneratePom=false" "-DpomFile=android-sdk-9.5.2.pom" "-Dfile=android-sdk-9.5.2.aar"_

## Cache issues and notes

Clearing the gradle cache of artifacts can solve a lot of problems. On Windows its located at 'C:\Users\<username>\.gradle\caches' and searches for the aar file (ie. 'android-sdk-9.5.2.aar') may yield lots of files which can be safely deleted. The folder 'C:\Users\<username>\.gradle\caches\modules-2\files-2.1\<group-id> cann be safely deleted in the event of Android build issues

# Flutter Maplibre GL

[![Flutter CI](https://github.com/m0nac0/flutter-maplibre-gl/actions/workflows/flutter_ci.yml/badge.svg)](https://github.com/m0nac0/flutter-maplibre-gl/actions/workflows/flutter_ci.yml)
[![Generate docs](https://github.com/m0nac0/flutter-maplibre-gl/actions/workflows/generate_docs.yml/badge.svg)](https://github.com/m0nac0/flutter-maplibre-gl/actions/workflows/generate_docs.yml)

This Flutter plugin allows to show **embedded interactive and customizable vector maps** as a Flutter widget.

For the Android and iOS integration, we use [maplibre-gl-native](https://github.com/maplibre/maplibre-gl-native). For web, we rely on [maplibre-gl-js](https://github.com/maplibre/maplibre-gl-js). This project only supports a subset of the API exposed by these libraries.

This project is a fork of [https://github.com/tobrun/flutter-mapbox-gl](https://github.com/tobrun/flutter-mapbox-gl), replacing its usage of Mapbox GL libraries with the open source [Maplibre GL](https://github.com/maplibre) libraries.

**Please note that this project is community driven and is not affiliated with the company Mapbox.** <br>
It does use some of their amazing open source libraries/tools, though. Thank you, Mapbox, for all the open-source work you do!

## Using the plugin in your project

This project is not yet available on pub.dev.
You can use it by referencing it in your `pubspec.yaml` like this:

```yaml
dependencies:
    ...
    maplibre_gl:
      git:
        url: https://github.com/m0nac0/flutter-maplibre-gl.git
        ref: main
```

This will get you the very latest changes from the main branch.
You can replace `main` with the name of the [latest release](https://github.com/m0nac0/flutter-maplibre-gl/releases)
to get a more stable version.

Compared to flutter-mapbox-gl, the only breaking API changes are:

- `MapboxMap` <--> `MaplibreMap`
- `MapboxMapController` <--> `MaplibreMapController`

### Documentation

Documentation is available on the docs branch in the doc/api folder and automatically updated on each push to the main branch. You can easily preview the [documentation / API reference here.](https://htmlpreview.github.io/?https://github.com/m0nac0/flutter-maplibre-gl/blob/docs/doc/api/index.html)

Please visit [https://github.com/maplibre/maplibre-gl-js](https://github.com/maplibre/maplibre-gl-js) and [https://github.com/maplibre/maplibre-gl-native](https://github.com/maplibre/maplibre-gl-native) for more information about the Maplibre libraries.

### iOS

To use this plugin with iOS, you need to add the source repository and 2 additional pods to your Podfile, as shown in the example app: https://github.com/m0nac0/flutter-maplibre-gl/blob/main/example/ios/Podfile

```ruby
source 'https://cdn.cocoapods.org/'
source 'https://github.com/m0nac0/flutter-maplibre-podspecs.git'

pod 'MapLibre'
pod 'MapLibreAnnotationExtension'
```

### Web

Include the following JavaScript and CSS files in the `<head>` of the `web/index.html` file.

```html
<script src="https://unpkg.com/maplibre-gl@latest/dist/maplibre-gl.js"></script>
<link
  href="https://unpkg.com/maplibre-gl@latest/dist/maplibre-gl.css"
  rel="stylesheet"
/>
```

## Supported API

| Feature       |      Android       |        iOS         |        Web         |
| ------------- | :----------------: | :----------------: | :----------------: |
| Style         | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| Camera        | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| Gesture       | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| User Location | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| Symbol        | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| Circle        | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| Line          | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| Fill          | :white_check_mark: | :white_check_mark: | :white_check_mark: |

## Map Styles

Map styles can be supplied by setting the `styleString` in the `MapOptions`. The following formats are supported:

1. Passing the URL of the map style. This should be a custom map style served remotely using a URL that start with 'http(s)://'
2. Passing the style as a local asset. Create a JSON file in the `assets` and add a reference in `pubspec.yml`. Set the style string to the relative path for this asset in order to load it into the map.
3. Passing the style as a local file. create an JSON file in app directory (e.g. ApplicationDocumentsDirectory). Set the style string to the absolute path of this JSON file.
4. Passing the raw JSON of the map style. This is only supported on Android.

### Tile sources requiring an API key

If your tile source requires an API key, we recommend directly specifying a source url with the API key included.
For example:

`https://tiles.example.com/{z}/{x}/{y}.vector.pbf?api_key={your_key}`

## Location features

### Android

Add the `ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION` permission in the application manifest `android/app/src/main/AndroidManifest.xml` to enable location features in an **Android** application:

```
<manifest ...
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

Starting from Android API level 23 you also need to request it at runtime. This plugin does not handle this for you. The example app uses the flutter ['location' plugin](https://pub.dev/packages/location) for this.

### iOS

To enable location features in an **iOS** application:

If you access your users' location, you should also add the following key to `ios/Runner/Info.plist` to explain why you need access to their location data:

```
xml ...
    <key>NSLocationWhenInUseUsageDescription</key>
    <string>[Your explanation here]</string>
```

A possible explanation could be: "Shows your location on the map".

## Getting Help

- **Need help with your code?**: Check the [discussions](https://github.com/m0nac0/flutter-maplibre-gl/discussions) on this repo or open a new one.
  Or look for previous questions on the [#maplibre tag](https://stackoverflow.com/questions/tagged/maplibre) — or [ask a new question](https://stackoverflow.com/questions/tagged/maplibre).
- **Have a bug to report?** [Open an issue](https://github.com/m0nac0/flutter-maplibre-gl/issues/new). If possible, include a full log and information which shows the issue.
- **Have a feature request?** [Open an issue](https://github.com/m0nac0/flutter-maplibre-gl/issues/new). Tell us what the feature should do and why you want the feature.

## Running in GitHub Codespaces

When you open this project in GitHub Codespaces, you can run the example app on web with the command `flutter run -d web-server --web-hostname=0.0.0.0`

Codespaces should automatically take care of the necessary port forwarding, so that you can view the running web app on your local device or in a new tab.

**Please note:** the Docker image used to setup the Codespace is from CirrusCI and sets the Git username and email to CirrusCI default values. You should set these correctly,
if you plan on committing from the Codespace.

## Fixing common issues

### Avoid Android UnsatisfiedLinkError

Update buildTypes in `android\app\build.gradle`

```gradle
buildTypes {
    release {
        // other configs
        ndk {
            abiFilters 'armeabi-v7a','arm64-v8a','x86_64', 'x86'
        }
    }
}
```

### iOS app crashes on startup

Please include the `NSLocationWhenInUseUsageDescription` as described [here](#location-features)

## Contributing

[Feedback](https://github.com/m0nac0/flutter-maplibre-gl/issues) and contributions are very welcome!
