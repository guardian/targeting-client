# targeting-client

This repository contains shared code used by targeting, frontend, and MAPI to create and process targeted campaigns.

Versions
--------

### Supported Play Versions

* Play **3.0** : use [![client-play-json-v30 Scala version support](https://index.scala-lang.org/guardian/targeting-client/client-play-json-v30/latest-by-scala-version.svg?platform=jvm)](https://index.scala-lang.org/guardian/targeting-client/client-play-json-v30)
  ```
  libraryDependencies += "com.gu.targeting-client" %% "client-play-json-v30" % "[maven version number]"
  ```
* Play **2.8** : use [![targeting-client artifacts](https://index.scala-lang.org/guardian/grid/targeting-client/client-play-json-v28/latest-by-scala-version.svg)](https://index.scala-lang.org/guardian/grid/targeting-client/client-play-json-v28/)
  ```
  libraryDependencies += "com.gu.targeting-client" %% "client-play-json-v28" % "[maven version number]"
  ```
* Play **2.7** : use [![targeting-client artifacts](https://img.shields.io/badge/targeting--client_--_JVM-1.0.0_(Scala_2.13,_2.12,_2.11)-green.svg)](https://index.scala-lang.org/guardian/grid/targeting-client)
  ```
  libraryDependencies += "com.gu" %% "targeting-client" % "[maven version number]"
  ```
* Play **2.6** : use [![targeting-client artifacts](https://index.scala-lang.org/guardian/grid/targeting-client-play26/latest-by-scala-version.svg)](https://index.scala-lang.org/guardian/grid/targeting-client-play26)
  ```
  libraryDependencies += "com.gu" %% "targeting-client" % "[maven version number]"
  ```

# Usage

To use this library add the following to your dependency libraries in your projects `build.sbt`:

`"com.gu" %% "targeting-client" % "1.1.0"`

Instead of version `0.1.0` insert the version you'd like to use (probably the latest). 


# Adding a new Campaign Type

Campaign types are expressed as a set of fields. To add a new set of fields edit the Fields.scala file in the following way. I'll use the example set of a charity drive campaign.

1. Decide what your fields* are going to be. In my example I'll say I want a charity name, and website url.
2. Create a new `case class` representing the fields and ensure this class `extends Fields`. In keeping with the naming conventions we'll call our example `CharityDriveFields`.
3. Select a type string for your new Fields, this will be set to the `_type` field in the resulting JSON. Make sure you add it to the `allFields` member.
4. Now you need to make sure you can serialize and deserialize to the correct types. You can follow the pattern in the file or find someone who has done this before using the git blame feature.
  1. `fieldWrites` takes in a `Fields`, finds the subtype, and selects the correct formatter to write the JSON.
  2. `fieldReads` is similar except it searches a block of JSON for a `type` variable and uses it to select the correct subclass of `Fields`
5. Add the new case class to the match on the file Campaign.scala in the function getFieldsType.
6. You should be done now! Ensure the library still compiles and then continue onto the following section on how to publish a new version.

> \* Note: you cannot have a variable called `_type` since the JSON serializers use this string in order to know what type of field is being read.

# Publishing a new version

This repo is using the [`gha-scala-library-release-workflow`](https://github.com/guardian/gha-scala-library-release-workflow) library. Detailed instructions for how to release a new version can be found [here](https://github.com/guardian/gha-scala-library-release-workflow/blob/main/docs/making-a-release.md).
