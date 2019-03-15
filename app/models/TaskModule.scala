package models

import models.tasks.DatabaseTask
import play.api.inject.{SimpleModule, _}

class TaskModule extends SimpleModule(bind[DatabaseTask].toSelf.eagerly())

