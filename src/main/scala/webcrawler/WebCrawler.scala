package search.webcrawler

import scala.slick.driver.H2Driver.simple._
import scala.collection.JavaConversions._
import slick.jdbc.meta.MTable
import org.jsoup.{Jsoup, HttpStatusException}
import org.jsoup.nodes.Document

object Settings {
  var root = ""
}
  

class WebCrawler {
  private implicit val session = Database.forURL("jdbc:h2:~/webcrawler", driver = "org.h2.Driver").createSession()
  private var tableList = List[String]()
  private var pageQueue = new PageQueue
  private var alive = true
  private var startWithRoot = false
  MTable.getTables.list()(session).foreach(t => tableList ::= t.name.name)

  session.withTransaction {
    if(!tableList.contains(Pages.tableName)) {
      Pages.ddl.create
      startWithRoot = true
    }
  }

  def kill {
    alive = false
  }

  def getDivs(document: Document): List[String] = {
    var list = List[String]()
    var elements = document.select("a")
    for(link <- elements.subList(0, elements.size)) {
      var rawLink = link.attr("abs:href")
      if(rawLink contains Settings.root) {
        list ::= rawLink
      }
    }
    list
  }

  private def run {
    session.withTransaction {
      while(!pageQueue.isEmpty) {
        try {
          var document = pageQueue()
          getDivs(document).foreach(link => {
              pageQueue << link
              Pages.autoInc.insert(Page(None, link, System.currentTimeMillis()))
              println(link)
            })
        } catch {
          case _: HttpStatusException => Unit
          case e: Exception => println(e)
        }
      }
    }
    session.close
  }

  def crawl {
    pageQueue << Settings.root
    this.run
  }

}

