logLevel := sbt.Level.Warn

addSbtPlugin("com.github.sbt"    % "sbt-ci-release"      % "1.9.3")
addSbtPlugin("org.wartremover" % "sbt-wartremover"     % "3.3.1")
addSbtPlugin("org.scoverage"   % "sbt-scoverage"       % "2.3.1")
addSbtPlugin("org.scoverage"   % "sbt-coveralls"       % "1.3.15")
addSbtPlugin("com.github.sbt"  % "sbt-native-packager" % "1.11.1")
addSbtPlugin("org.scalameta"   % "sbt-native-image"    % "0.3.0")

val sbtDevOopsVersion = "3.2.0"
addSbtPlugin("io.kevinlee" % "sbt-devoops-scala"     % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-sbt-extra" % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-github"    % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-starter"   % sbtDevOopsVersion)

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
addSbtPlugin("io.kevinlee"  % "sbt-docusaur"  % "0.17.0")

addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.0")
