# targeting-client

This repository contains shared code used by targeting, frontend, and MAPI to create and process targeted campaigns.

# Version mapping

|Targeting Client Version|Play Version|Scala Version     |Artefact                                            |
|------------------------|------------|------------------|----------------------------------------------------|
|0.14.8                  |2.6         |2.11 & 2.12       |"com.gu" % "targeting-client-play26_2.11" % "0.14.8"|
|1.0.x                   |2.7         |2.11 & 2.12 & 2.13|"com.gu" % "targeting-client_2.11" % "1.0.0"        |
|1.1.x                   |2.8         |2.12 & 2.13       |"com.gu" % "targeting-client_2.11" % "1.1.0"        |

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

1. Get access to oss.sonatype.org (ask a Dev Manager/team member if unsure on this - there is a []doc on this](https://docs.google.com/document/d/1M_MiE8qntdDn97QIRnIUci5wdVQ8_defCqpeAwoKY8g/edit#heading=h.7n25tzj28wmr))
2. Run `sbt release`
