package search.webcrawler

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

object Pages extends Table[(String, Long)]("PAGES") {
  def url = column[String]("PAGE_URL", O.PrimaryKey)
  def lastVisited = column[Long]("PAGE_LAST_VISITED")

  def * = url ~ lastVisited
}

