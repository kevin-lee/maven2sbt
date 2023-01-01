logLevel := sbt.Level.Warn

addSbtPlugin("com.geirsson"    % "sbt-ci-release"      % "1.5.7")
addSbtPlugin("org.wartremover" % "sbt-wartremover"     % "3.0.7")
addSbtPlugin("org.scoverage"   % "sbt-scoverage"       % "2.0.6")
addSbtPlugin("org.scoverage"   % "sbt-coveralls"       % "1.3.5")
addSbtPlugin("com.github.sbt"  % "sbt-native-packager" % "1.9.11")
addSbtPlugin("org.scalameta"   % "sbt-native-image"    % "0.3.0")

val sbtDevOopsVersion = "2.24.0"
addSbtPlugin("io.kevinlee" % "sbt-devoops-scala"     % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-sbt-extra" % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-github"    % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-starter"   % sbtDevOopsVersion)

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
addSbtPlugin("io.kevinlee"  % "sbt-docusaur"  % "0.13.0")
