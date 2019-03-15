package models.tasks.albumCheck

import play.api.inject.{SimpleModule, _}

class AlbumCheckModule extends SimpleModule(bind[AlbumCheckerTask].toSelf.eagerly())
