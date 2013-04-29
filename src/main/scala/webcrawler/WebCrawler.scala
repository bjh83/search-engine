package search.webcrawler

import scala.slick.driver.H2Driver.simple._
import scala.collection.JavaConversions._
import slick.jdbc.meta.MTable
import java.sql.SQLException
import java.net.URL
import org.jsoup.{Jsoup, HttpStatusException}
import org.jsoup.nodes.Document

object Settings {
  var root = ""
}
  

class WebCrawler {
  private implicit val session = Database.forURL("jdbc:h2:~/webcrawler", driver = "org.h2.Driver").createSession()
  private var tableList = List[String]()
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

  def clean(raw: String) = {
    var url = new URL(raw)
    url.getProtocol + "://" + url.getHost + url.getPath
  }

  private def update(url: String, lastVisited: Long) = Pages.where(_.url === url).update(url, lastVisited)

  private def run(queue: PageQueue) {
    session.withTransaction {
      for((url, doc) <- queue) {
        try {
          getDivs(doc).foreach(link => {
              try {
                Pages.insert(clean(link), 0) 
              } catch {
                case _: SQLException => Unit
              } finally {
                update(url, System.currentTimeMillis)
              }
            })
        } catch {
          case e: Exception => { update(url, -1); println(e) }
        }
      }
    }
  }

  def crawl() {
    var queue = new PageQueue
    queue << Settings.root
    run(queue)
    while(alive) {
      queue = new PageQueue
      for(page <- Query(Pages); if page.lastVisited === 0l) {
        queue << page._1
      }
      if(queue.isEmpty) {
        return
      } else {
        run(queue)
      }
    }
  }

}

