package models

import com.steven.hicks.logic.{ArtistQueryBuilder, ArtistSearcher}
import scala.collection.JavaConverters._

object LastFMDao {

  def searchForLastFMArtists(name: String): List[com.steven.hicks.beans.Artist] = {
    val builder: ArtistQueryBuilder = new ArtistQueryBuilder.Builder().artistName(name).build()
    val searcher: ArtistSearcher = new ArtistSearcher
    val artists = searcher.search(builder).asScala
    artists.toList
  }

}
