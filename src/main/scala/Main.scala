import search.webcrawler._

object Run extends App {
  Settings.root = "http://www.case.edu"
  new WebCrawler().crawl
}

