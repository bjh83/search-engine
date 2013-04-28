package search.webcrawler

import dispatch._, Defaults._

class PageQueue {
  private var requests = List[Future[String]]()

  def << (address: String) {
    var request = url(address)
    requests +:= Http(request OK as.String)
    println(requests)
  }

  def isEmpty = requests.isEmpty

  def apply() = {
    var head = requests.head
    requests = requests.drop(1)
    head
  }
}

