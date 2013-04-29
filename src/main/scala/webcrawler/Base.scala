package search.webcrawler

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

object Pages extends Table[(String, Long)]("PAGES") {
  def url = column[String]("PAGE_URL", O.PrimaryKey)
  def lastVisited = column[Long]("PAGE_LAST_VISITED")

  def * = url ~ lastVisited
}

object Index extends Table[(String, String, Int)]("INDEX") {
  def url = column[String]("PAGE_URL")
  def word = column[String]("INDEX_WORD")
  def count = column[Int]("INDEX_COUNT")

  def * = url ~ word ~ count
  def page = foreignKey("PAGE_FK", url, Pages)(_.url)
}

