package models.tasks.startup

import anorm._
import javax.inject.Inject
import play.api.Configuration
import play.api.db.Database

class DatabaseCreator @Inject()(db: Database, config: Configuration) {

  def run() = {

    val dbName = config.entrySet.filter(x => x._1 == "db.default.username").head._2

    println("creating albums table")
    db.withConnection{implicit c =>
      SQL("CREATE TABLE IF NOT EXISTS public.albums" +
        "(id serial NOT NULL, " +
        "name character varying(255) COLLATE pg_catalog.\"default\", " +
        "year integer DEFAULT 1900, " +
        "artist_id integer, " +
        "mbid character varying(50) COLLATE pg_catalog.\"default\", " +
        "url character varying COLLATE pg_catalog.\"default\", " +
        "CONSTRAINT \"ALBUM_MBID_UNIQUE\" UNIQUE (mbid) ) " +
        "WITH ( OIDS = FALSE ) TABLESPACE pg_default; " +
        "ALTER TABLE public.albums OWNER to " + dbName + "; " +
        "GRANT ALL ON TABLE public.albums TO " + dbName + ";")
        .execute()
    }

    println("creating album_images table")
    db.withConnection{implicit c =>
      SQL("CREATE TABLE IF NOT EXISTS public.album_images " +
        "(album_id numeric, " +
        "text character varying COLLATE pg_catalog.\"default\", " +
        "url character varying COLLATE pg_catalog.\"default\" ) " +
        "WITH ( OIDS = FALSE ) " +
        "TABLESPACE pg_default; " +
        "ALTER TABLE public.album_images OWNER to " + dbName + "; " +
        "GRANT ALL ON TABLE public.album_images TO " + dbName + ";")
        .execute()
    }

    println("creating artists table")
    db.withConnection{implicit c =>
      SQL("CREATE TABLE IF NOT EXISTS public.artists " +
        "(id serial NOT NULL, " +
        "name character varying(255) COLLATE pg_catalog.\"default\", " +
        "summary text COLLATE pg_catalog.\"default\", " +
        "content text COLLATE pg_catalog.\"default\", " +
        "mbid character varying COLLATE pg_catalog.\"default\", " +
        "CONSTRAINT \"ARTIST_MBID_UNIQUE\" UNIQUE (mbid) , " +
        "CONSTRAINT \"ARTIST_NAME_UNIQUE\" UNIQUE (name) ) " +
        "WITH ( OIDS = FALSE ) " +
        "TABLESPACE pg_default; " +
        "ALTER TABLE public.artists OWNER to " + dbName + "; " +
        "GRANT ALL ON TABLE public.artists TO " + dbName + ";")
        .execute()
    }

    println("creating artist_images table")
    db.withConnection{implicit c =>
      SQL("CREATE TABLE IF NOT EXISTS public.artist_images " +
        "(artist_id numeric, " +
        "text character varying COLLATE pg_catalog.\"default\", " +
        "url character varying COLLATE pg_catalog.\"default\" ) " +
        "WITH ( OIDS = FALSE ) TABLESPACE pg_default; " +
        "ALTER TABLE public.artist_images OWNER to " + dbName + ";" +
        "GRANT ALL ON TABLE public.artist_images TO " + dbName + ";")
        .execute()
    }

    println("creating reviews table")
    db.withConnection{implicit c =>
      SQL("CREATE TABLE IF NOT EXISTS public.reviews " +
        "(id serial NOT NULL, " +
        "album_id integer, " +
        "user_id integer, " +
        "content text COLLATE pg_catalog.\"default\", " +
        "added_on timestamp with time zone ) " +
        "WITH ( OIDS = FALSE ) TABLESPACE pg_default; " +
        "ALTER TABLE public.reviews OWNER to " + dbName + "; " +
        "GRANT ALL ON TABLE public.reviews TO " + dbName + ";")
        .execute()
    }

    println("creating tracks table")
    db.withConnection{implicit c =>
      SQL("CREATE TABLE public.tracks " +
        "(album_id numeric, " +
        "name character varying COLLATE pg_catalog.\"default\", " +
        "rank numeric, " +
        "duration numeric ) " +
        "WITH ( OIDS = FALSE ) TABLESPACE pg_default; " +
        "ALTER TABLE public.tracks OWNER to " + dbName + "; " +
        "GRANT ALL ON TABLE public.tracks TO " + dbName + ";")
        .execute()
    }

    println("creating users table")
    db.withConnection{implicit c =>
      SQL("CREATE TABLE public.tracks " +
        "(album_id numeric, " +
        "name character varying COLLATE pg_catalog.\"default\", " +
        "rank numeric, " +
        "duration numeric ) " +
        "WITH ( OIDS = FALSE ) TABLESPACE pg_default; " +
        "ALTER TABLE public.tracks OWNER to " + dbName + "; " +
        "GRANT ALL ON TABLE public.tracks TO " + dbName + ";")
        .execute()
    }

  }

}
