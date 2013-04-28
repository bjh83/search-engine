package search.webcrawler

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

case class Page(id: Option[Int], url: String, lastVisited: Long)

object Pages extends Table[Page]("PAGES") {
  def id = column[Int]("PAGE_ID", O.PrimaryKey, O.AutoInc)
  def url = column[String]("PAGE_URL")
  def lastVisited = column[Long]("PAGE_LAST_VISITED")

  def * = id.? ~ url ~ lastVisited <> (Page, Page.unapply _)
  def autoInc = id.? ~ url ~ lastVisited <> (Page, Page.unapply _) returning id
}

