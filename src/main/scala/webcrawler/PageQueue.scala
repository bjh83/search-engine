package search.webcrawler

import scala.collection._
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.LinkedList

class PageQueue extends Iterable[Future[Document]] {
  private val requests = new LinkedList[Future[Document]]

  class QueueIterator extends Iterator[Future[Document]] {
    private val iterator = requests.iterator

    def hasNext = iterator.hasNext

    def next = iterator.next
  }

  def << (address: String) {
    requests.add(future(Jsoup.connect(address).get))
  }

  def isEmpty = requests.isEmpty

  def apply: Document = for(value <- requests.remove()) yield value

  def iterator = new QueueIterator

}

