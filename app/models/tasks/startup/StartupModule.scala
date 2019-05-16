package models.tasks.startup

import com.google.inject.AbstractModule
import play.api.inject.{SimpleModule, _}
//
class StartupModule extends AbstractModule{

  override def configure(): Unit = {
    bind(classOf[StartupTasks]).asEagerSingleton()
  }

}
//
//class StartupModule extends SimpleModule(bind[StartupTasks].toSelf.eagerly())