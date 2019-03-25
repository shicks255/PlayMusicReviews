package models.tasks.albumCheck

import akka.actor.ActorSystem
import com.google.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class AlbumCheckerTask @Inject()(actorSystem: ActorSystem, albumCheck: AlbumCheck)(implicit executionContext: ExecutionContext){

  actorSystem.scheduler.schedule(5.seconds, 60.minute) {
    albumCheck.run
  }

}
