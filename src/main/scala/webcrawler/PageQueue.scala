package search.webcrawler

import scala.collection._
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.LinkedList

class PageQueue extends Iterable[Document] {
  private val requests = new LinkedList[Future[Document]]

  class QueueIterator extends Iterator[Document] {
    private val iterator = requests.iterator

    override def hasNext = iterator.hasNext

    override def next = Await.result(iterator.next, 1 seconds)
  }

  def << (address: String) {
    requests.add(future(Jsoup.connect(address).get))
  }

  override def isEmpty = requests.isEmpty

  def apply() = Await.result(requests.remove, 1 seconds)

  override def iterator = new QueueIterator

}

