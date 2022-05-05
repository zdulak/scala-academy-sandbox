import scala.collection.immutable.SortedMap
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}
import scopt.{OParser, OptionParser}
import java.io.File

object FileStats {
  def wordsStats(filename: String): Try[SortedMap[String, Int]] = {
//    type alias
    type Occurrence = Int
    Using(Source.fromFile(filename)) {
      source => {
        val stats = source.getLines()
          .flatMap(line => line.split("\\s+")).toSeq
          .groupBy(identity)
          .map { case (word, wordList) => (word, wordList.length)}

//        Alternative to toSeq.groupBy
//          .foldLeft(Map[String, Occurrence]()) { (result, word) =>
//            result + (word -> (result.getOrElse(word, 0) + 1))

//            Alternative for getOrElse
//            result.get(word) match {
//              case Some(occ) => result + (word -> (occ + 1))
//              case None => result + (word -> 1)
//            }

//            Alternative for match
//            if (result.isDefinedAt(word))
//              result + (word -> (result(word) + 1))
//            else
//              result + (word -> 1)
//          }

        SortedMap.from(stats)(Ordering.by(_.toLowerCase))
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val builder = OParser.builder[Config]
    val parser = {
      import builder._
      OParser.sequence(
        arg[String]("<file name>")
          .required()
          .validate(name => if (new File(name).exists()) success else failure("File does not exist"))
          .action {(name, config) => config.copy(fileName = name)}
          .text("Name of the file"),
        help("help").text("prints this usage text")
      )
    }

    OParser.parse(parser, args, Config()) match {
      case Some(config) => wordsStats(config.fileName).get.foreach(println)
      case None =>
    }
//    wordsStats(args(0)) match {
//      case Failure(exception) => println("Failed to read file", exception)
//      case Success(stats) => stats.foreach(println)
//    }
  }
}

case class Config(fileName: String = "")
