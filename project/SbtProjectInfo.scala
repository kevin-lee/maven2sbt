import wartremover.WartRemover.autoImport.{Wart, Warts}

object SbtProjectInfo {
  final case class ProjectName(projectName: String) extends AnyVal

  def commonWarts(scalaVersion: String): Seq[wartremover.Wart] =
    if (scalaVersion.startsWith("2.10"))
      Seq.empty
    else
      Warts.allBut(Wart.ImplicitConversion, Wart.ImplicitParameter)

}
