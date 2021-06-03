import wartremover.WartRemover.autoImport.{Wart, Warts}

object SbtProjectInfo {
  final case class ProjectName(projectName: String) extends AnyVal

  def commonWarts(scalaBinaryVersion: String): Seq[wartremover.Wart] = scalaBinaryVersion match {
    case "2.10" | "3" =>
      Seq.empty
    case _ =>
      Warts.allBut(Wart.ImplicitConversion, Wart.ImplicitParameter)
  }

}
