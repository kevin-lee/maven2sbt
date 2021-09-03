logLevel := sbt.Level.Warn

addSbtPlugin("com.geirsson"     % "sbt-ci-release"      % "1.5.7")
addSbtPlugin("org.wartremover"  % "sbt-wartremover"     % "2.4.15")
addSbtPlugin("org.scoverage"    % "sbt-scoverage"       % "1.6.1")
addSbtPlugin("org.scoverage"    % "sbt-coveralls"       % "1.2.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.1")
addSbtPlugin("org.scalameta"    % "sbt-native-image"    % "0.3.0")

addSbtPlugin("io.kevinlee" % "sbt-devoops-scala"     % "2.6.0")
addSbtPlugin("io.kevinlee" % "sbt-devoops-sbt-extra" % "2.6.0")
addSbtPlugin("io.kevinlee" % "sbt-devoops-github"    % "2.6.0")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")
addSbtPlugin("io.kevinlee"  % "sbt-docusaur"  % "0.6.0")
