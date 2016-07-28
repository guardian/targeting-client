# targeting-client

This repository contains shared code used by targeting, frontend, and MAPI to create and process targeted campaigns.

# Usage

To use this library add the following to your dependency libraries in your projects `build.sbt`:

`"com.gu" %% "targeting-client" % "0.1.0"`

Instead of version `0.1.0` insert the version you'd like to use (probably the latest). For a full listing of versions check [here](https://bintray.com/guardian/editorial-tools/targeting-client).

Some people have issues accessing the BinTray jcenter repository, usually this can be fixed by adding the following to your `build.sbt`:

`resolvers += "Guardian Bintray" at "https://dl.bintray.com/guardian/editorial-tools"`

# Adding a new Campaign Type

Campaign types are expressed as a set of fields. To add a new set of fields edit the Fields.scala file in the following way. I'll use the example set of a charity drive campaign.

1. Decide what your fields* are going to be. In my example I'll say I want a charity name, and website url.
2. Create a new `case class` representing the fields and ensure this class `extends Fields`. In keeping with the naming conventions we'll call our example `CharityDriveFields`.
3. Now you need to make sure you can serialize and deserialize to the correct types. This is done by adding a new format using the `Jsonx` library and modifying the `fieldWrites` and `fieldReads` functions. You can follow the pattern in the file or find someone who has done this before using the git blame feature.
  1. `fieldWrites` takes in a `Fields`, finds the subtype, and selects the correct formatter to write the JSON.
  2. `fieldReads` is similar except it searches a block of JSON for a `type` variable and uses it to select the correct subclass of `Fields`
4. You should be done now! Ensure the library still compiles and then continue onto the following section on how to publish a new version.

> \* Note: you cannot have a variable called `__type` since the JSON serializers use this string in order to know what type of field is being read.

# Publishing a new version

Now you've added your new campaign type you need to publish a new version to make it available to all users.

## You will need a Bintray account!
In order to publish a new version of this schema you'll need a Bintray account.

1. Go to `bintray.com` and login with your GitHub account.
2. Someone will need to invite you to The Guardian Bintray org. Ask super nicely and they just might do it. They should also make you an admin.
3. You will need an API key.
  1. Go to your profile
  2. Click the Edit button near your profile name (top left).
  3. At the bottom of the list on the left will be the API key section containing your key. Keep this key handy for the next step.
4. Setup your username/API key locally.
  1. In this project run `sbt bintrayChangeCredentials`
  2. Enter your username and API key as prompted.
  3. This will save your creds locally and you shouldn't need to change them unless you refresh your API key.

## How to publish a new version
So you've made some changes and you want to publish a new version of this schema as a package to Bintray jcenter...

1. Make your changes
2. Bump the version in `version.sbt`. It's useful to follow the [SemVer guidelines](http://semver.org/) (see the summary section).
3. Ensure the project builds.
4. Run `sbt publish`
5. If you setup your Bintray account correctly then this should publish your new version to Bintray!
6. If for some reason you want to remove your package from Bintray you can run `sbt bintrayUnpublish` which will remove the package *at the current version*.

