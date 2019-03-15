package models

import com.steven.hicks.beans.album.Album

import scala.collection.mutable

object Cache {

  private val lastFMAlbumCache: mutable.WeakHashMap[(String, String), List[Album]] =
    new mutable.WeakHashMap()

  def checkIfExistsInCache(mbid: String, name: String): Boolean = {
    lastFMAlbumCache.contains((mbid, name))
  }

  def putInCache(mbid: String, name: String, albums: List[Album]) = {
    println("storing " + name + " in cache")
    lastFMAlbumCache.put((mbid, name), albums)
  }

  def getFromCache(mbid: String, name: String) = {
    println("retreiving " + name + " from cache")
    lastFMAlbumCache.get((mbid, name))
  }

  def printCache() = {
    lastFMAlbumCache.keys.foreach(x => println(x._2))
  }

}
