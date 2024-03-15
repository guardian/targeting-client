# targeting-client
[![Release](https://github.com/guardian/targeting-client/actions/workflows/release.yml/badge.svg)](https://github.com/guardian/targeting-client/actions/workflows/release.yml)

This repository contains shared code used by targeting, frontend, and MAPI to create and process targeted campaigns.

Versions
--------

### Supported Play Versions

* Play **3.0** : use [![client-play-json-v30 Scala version support](https://index.scala-lang.org/guardian/targeting-client/client-play-json-v30/latest-by-scala-version.svg?platform=jvm)](https://index.scala-lang.org/guardian/targeting-client/client-play-json-v30)
  ```
  libraryDependencies += "com.gu.targeting-client" %% "client-play-json-v30" % "[maven version number]"
  ```
* Play **2.8** : use [![client-play-json-v28 Scala version support](https://index.scala-lang.org/guardian/targeting-client/client-play-json-v28/latest-by-scala-version.svg?platform=jvm)](https://index.scala-lang.org/guardian/targeting-client/client-play-json-v28)
  ```
  libraryDependencies += "com.gu.targeting-client" %% "client-play-json-v28" % "[maven version number]"
  ```

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

# Publishing a new release

This repo uses [`gha-scala-library-release-workflow`](https://github.com/guardian/gha-scala-library-release-workflow)
to automate publishing releases (both full & preview releases) - see
[**Making a Release**](https://github.com/guardian/gha-scala-library-release-workflow/blob/main/docs/making-a-release.md).
