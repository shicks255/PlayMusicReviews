package models.artist

import models.ImageGetter
import models.artistImage.ArtistImage

case class ArtistFull(id: Long, name: String, mbid: String, summary: String, content: String, images: List[ArtistImage]) extends ImageGetter
