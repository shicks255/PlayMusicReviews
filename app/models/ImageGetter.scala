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
    val largeImage = images.filter(x => x.getSize == "large")
    largeImage match {
      case h :: t => Some(h)
      case _ => None
    }
  }

  def getExtraLargeImage = {
    val extraLarge = images.filter(x => x.getSize == "extralarge")
    extraLarge match {
      case h :: t => Some(h)
      case _ => None
    }
  }

  def getMegaImage = {
    val mega = images.filter(x => x.getSize == "mega")
    mega match {
      case h :: t => Some(h)
      case _ => None
    }
  }
}
