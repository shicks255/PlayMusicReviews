package models

trait ImageGetter {
  def getSmallImage(images: List[Image]): Option[Image] = {
    val smallImage = images.filter(x => x.getSize == "small")
    smallImage match {
      case h :: t => Some(h)
      case _ => None
    }
  }

  def getMediumImage(images: List[Image]): Option[Image] = {
    val mediumImage = images.filter(x => x.getSize == "medium")
    mediumImage match {
      case h :: t => Some(h)
      case _ => None
    }
  }

  def getLargeImage(images: List[Image]) = {
    val largeImage = images.filter(x => x.getSize == "large")
    largeImage match {
      case h :: t => Some(h)
      case _ => None
    }
  }

  def getExtraLargeImage(images: List[Image]) = {
    val extraLarge = images.filter(x => x.getSize == "extralarge")
    extraLarge match {
      case h :: t => Some(h)
      case _ => None
    }
  }

  def getMegaImage(images: List[Image]) = {
    val mega = images.filter(x => x.getSize == "mega")
    mega match {
      case h :: t => Some(h)
      case _ => None
    }
  }
}
