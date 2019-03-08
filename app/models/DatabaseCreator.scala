package models

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import play.api.db.Database

class DatabaseCreator @Inject()(db: Database) {

  def run() = {

    println("creating albums table")
    db.withConnection{implicit c =>
      SQL("CREATE TABLE IF NOT EXISTS public.albums " +
        "(id serial NOT NULL, " +
        "name character varying(255) COLLATE pg_catalog.\"default\", " +
        "year integer DEFAULT 1900, " +
        "artist_id integer, " +
        "mbid character varying(50) COLLATE pg_catalog.\"default\", " +
        "url character varying COLLATE pg_catalog.\"default\", " +
        "CONSTRAINT \"ALBUM_MBID_UNIQUE\" UNIQUE (mbid) ) " +
        "WITH ( OIDS = FALSE ) TABLESPACE pg_default; " +
        "ALTER TABLE public.albums OWNER to shicks; " +
        "GRANT ALL ON TABLE public.albums TO shicks;")
        .execute()
    }

  }

}
