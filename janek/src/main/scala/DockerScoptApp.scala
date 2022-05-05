object DockerScoptApp extends App {

  import scopt.OParser

  val builder = OParser.builder[Config]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName("scopt"),
      head("scopt", "scoptApp"),
      opt[Int]('n', "number")
        .action((x, c) => c.copy(n = x))
    )
  }

  val input = args
  OParser.parse(parser1, args, Config()) match {
    case Some(config) => println(config)
    case _ => println("Noting provided")
  }
}
