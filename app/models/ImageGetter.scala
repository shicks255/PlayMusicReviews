package models


abstract class ImageGetter(images: List[Image]) {
  def getSmallImage: Option[Image] = {
    val smallImage = images.filter(x => x.getSize == "small")
    smallImage match {
      case h :: t => Some(h)
      case _ => None
    }
  }

  def getMediumImage: Option[Image] = {
    val mediumImage = images.filter(x => x.getSize == "medium")
    mediumImage match {
      case h :: t => Some(h)
      case _ => None
    }
  }

  def getLargeImage = {
    val largeImage = images.find(x => x.getSize == "large")
    largeImage match {
      case Some(x) => x
      case _ => images(0)
    }
  }
}
