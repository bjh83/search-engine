package search.webcrawler

import scala.collection._
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.LinkedList

class PageQueue extends Iterable[Pair[String, Document]] {
  private val requests = new LinkedList[Pair[String, Future[Document]]]

  class QueueIterator extends Iterator[Pair[String, Document]] {
    private val iterator = requests.iterator

    override def hasNext = iterator.hasNext

    override def next = {
      var pair = iterator.next
      try Pair(pair._1, Await.result(pair._2, 1 seconds)) catch {
        case _: Exception => Pair(pair._1, null)
      }
    }
  }

  def << (address: String) {
    requests.add(Pair(address, future(Jsoup.connect(address).get)))
  }

  def << (addresses: List[String]) {
    for(address <- addresses) {
      this << address
    }
  }

  override def isEmpty = requests.isEmpty

  def apply() = {
    var pair = requests.remove()
    Pair(pair._1, Await.result(pair._2, 1 seconds))
  }

  override def iterator = new QueueIterator

}

