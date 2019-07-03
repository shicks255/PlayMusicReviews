package models.tasks.startup

import anorm._
import javax.inject.Inject
import play.api.Configuration
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

class DatabaseCreator @Inject()(db: Database, config: Configuration) {

  def run() = {
    implicit val global = ExecutionContext.global

    Future {
      val dbName = config.entrySet.filter(x => x._1 == "db.default.username").head._2

      println("creating albums table")
      db.withConnection { implicit c =>
        SQL(
          s"""
             CREATE TABLE IF NOT EXISTS public.albums (
                 id integer NOT NULL,
                 name character varying(255),
                 artist_id integer,
                 mbid character varying(50),
                 url character varying,
                 release_date date,
                 summary text,
                 content text
             );
             ALTER TABLE public.albums OWNER TO ${dbName.render()};
             CREATE SEQUENCE IF NOT EXISTS public.album_id_seq
                 AS integer
                 START WITH 1
                 INCREMENT BY 1
                 NO MINVALUE
                 NO MAXVALUE
                 CACHE 1;
             ALTER TABLE public.album_id_seq OWNER TO ${dbName.render()};
             ALTER SEQUENCE public.album_id_seq OWNED BY public.albums.id;
          """).execute()
      }

      println("creating album_images table")
      db.withConnection { implicit c =>
        SQL(
          s"""
             CREATE TABLE IF NOT EXISTS public.album_images (
                 album_id integer,
                 text character varying,
                 url character varying
             );
          ALTER TABLE public.album_images OWNER TO ${dbName.render()};

      """).execute()
      }
      //
      println("creating artists table")
      db.withConnection { implicit c =>
        SQL(
          s"""
             CREATE TABLE IF NOT EXISTS public.artists (
                 id integer NOT NULL,
                 name character varying(255),
                 summary text,
                 content text,
                 mbid character varying
             );
             ALTER TABLE public.artists OWNER TO ${dbName.render()};
             CREATE SEQUENCE IF NOT EXISTS public.artist_id_seq
                 AS integer
                 START WITH 1
                 INCREMENT BY 1
                 NO MINVALUE
                 NO MAXVALUE
                 CACHE 1;
             ALTER TABLE public.artist_id_seq OWNER TO ${dbName.render()};
             ALTER SEQUENCE public.artist_id_seq OWNED BY public.artists.id;
      """).execute()
      }

      println("creating artist_images table")
      db.withConnection { implicit c =>
        SQL(
          s"""
             CREATE TABLE IF NOT EXISTS public.artist_images (
                 artist_id integer,
                 text character varying,
                 url character varying
             );
             ALTER TABLE public.artist_images OWNER TO ${dbName.render()};
      """).execute()
      }

      println("creating reviews table")
      db.withConnection { implicit c =>
        SQL(
          s"""
             CREATE TABLE IF NOT EXISTS public.reviews (
                 id integer NOT NULL,
                 album_id integer,
                 user_id integer,
                 content text,
                 added_on timestamp with time zone,
                 rating real DEFAULT 0.0
             );
             ALTER TABLE public.reviews OWNER TO ${dbName.render()};
             CREATE SEQUENCE IF NOT EXISTS public.review_id_seq
                 AS integer
                 START WITH 1
                 INCREMENT BY 1
                 NO MINVALUE
                 NO MAXVALUE
                 CACHE 1;
             ALTER TABLE public.review_id_seq OWNER TO ${dbName.render()};
             ALTER SEQUENCE public.review_id_seq OWNED BY public.reviews.id;
      """).execute()
      }

      println("creating tracks table")
      db.withConnection { implicit c =>
        SQL(
          s"""
             CREATE TABLE IF NOT EXISTS public.tracks (
                 album_id integer,
                 name character varying,
                 rank numeric,
                 duration numeric
             );
             ALTER TABLE public.tracks OWNER TO ${dbName.render()};
      """).execute()
      }

      println("creating users table")
      db.withConnection { implicit c =>
        SQL(
          s"""
             CREATE TABLE IF NOT EXISTS public.users (
                 id integer NOT NULL,
                 username character(75),
                 password character(50),
                 email_list boolean DEFAULT false NOT NULL,
                 email_address character varying(75),
                 is_admin boolean
             );
             ALTER TABLE public.users OWNER TO ${dbName.render()};
             CREATE SEQUENCE IF NOT EXISTS public.users_id_seq
                 AS integer
                 START WITH 1
                 INCREMENT BY 1
                 NO MINVALUE
                 NO MAXVALUE
                 CACHE 1;
             ALTER TABLE public.users_id_seq OWNER TO ${dbName.render()};
             ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;
      """).execute()
      }

      println("other DB housekeeping")
      db.withTransaction { implicit c =>
        SQL(
          s"""
             ALTER TABLE ONLY public.albums ALTER COLUMN id SET DEFAULT nextval('public.album_id_seq'::regclass);
             ALTER TABLE ONLY public.artists ALTER COLUMN id SET DEFAULT nextval('public.artist_id_seq'::regclass);
             ALTER TABLE ONLY public.reviews ALTER COLUMN id SET DEFAULT nextval('public.review_id_seq'::regclass);
             ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);

             ALTER TABLE ONLY public.album_images DROP CONSTRAINT IF EXISTS "album_images_fk";
             ALTER TABLE ONLY public.albums DROP CONSTRAINT IF EXISTS "albums_FK";
             ALTER TABLE ONLY public.artist_images DROP CONSTRAINT IF EXISTS "artist_images_FK";
             ALTER TABLE ONLY public.reviews DROP CONSTRAINT IF EXISTS "review_FK";
             ALTER TABLE ONLY public.tracks DROP CONSTRAINT IF EXISTS "tracks_FK";
             ALTER TABLE ONLY public.reviews DROP CONSTRAINT IF EXISTS "tracks_FK2";

             ALTER TABLE ONLY public.artists DROP CONSTRAINT IF EXISTS "ARTIST_MBID_UNIQUE";
             ALTER TABLE ONLY public.artists DROP CONSTRAINT IF EXISTS "ARTIST_NAME_UNIQUE";
             ALTER TABLE ONLY public.albums DROP CONSTRAINT IF EXISTS "album_PK";
             ALTER TABLE ONLY public.artists DROP CONSTRAINT IF EXISTS "artists_PK";
             ALTER TABLE ONLY public.reviews DROP CONSTRAINT IF EXISTS "reviews_PK";
             ALTER TABLE ONLY public.users DROP CONSTRAINT IF EXISTS "unique_username";
             ALTER TABLE ONLY public.users DROP CONSTRAINT IF EXISTS "users_PK";

             ALTER TABLE ONLY public.artists ADD CONSTRAINT "ARTIST_MBID_UNIQUE" UNIQUE (mbid);
             ALTER TABLE ONLY public.artists ADD CONSTRAINT "ARTIST_NAME_UNIQUE" UNIQUE (name);
             ALTER TABLE ONLY public.albums ADD CONSTRAINT "album_PK" PRIMARY KEY (id);
             ALTER TABLE ONLY public.artists ADD CONSTRAINT "artists_PK" PRIMARY KEY (id);
             ALTER TABLE ONLY public.reviews ADD CONSTRAINT "reviews_PK" PRIMARY KEY (id);
             ALTER TABLE ONLY public.users ADD CONSTRAINT unique_username UNIQUE (username);
             ALTER TABLE ONLY public.users ADD CONSTRAINT "users_PK" PRIMARY KEY (id);

             CREATE INDEX IF NOT EXISTS fki_album_images_fk ON public.album_images USING btree (album_id);
             CREATE INDEX IF NOT EXISTS "fki_albums_FK" ON public.albums USING btree (artist_id);
             CREATE INDEX IF NOT EXISTS "fki_artist_images_FK" ON public.artist_images USING btree (artist_id);
             CREATE INDEX IF NOT EXISTS "fki_review_FK" ON public.reviews USING btree (album_id);
             CREATE INDEX IF NOT EXISTS "fki_tracks_FK" ON public.tracks USING btree (album_id);
             CREATE INDEX IF NOT EXISTS "fki_tracks_FK2" ON public.reviews USING btree (user_id);

             ALTER TABLE ONLY public.album_images ADD CONSTRAINT album_images_fk FOREIGN KEY (album_id) REFERENCES public.albums(id);
             ALTER TABLE ONLY public.albums ADD CONSTRAINT "albums_FK" FOREIGN KEY (artist_id) REFERENCES public.artists(id);
             ALTER TABLE ONLY public.artist_images ADD CONSTRAINT "artist_images_FK" FOREIGN KEY (artist_id) REFERENCES public.artists(id);
             ALTER TABLE ONLY public.reviews ADD CONSTRAINT "review_FK" FOREIGN KEY (album_id) REFERENCES public.albums(id);
             ALTER TABLE ONLY public.tracks ADD CONSTRAINT "tracks_FK" FOREIGN KEY (album_id) REFERENCES public.albums(id);
             ALTER TABLE ONLY public.reviews ADD CONSTRAINT "tracks_FK2" FOREIGN KEY (user_id) REFERENCES public.users(id);

             GRANT ALL ON TABLE public.albums TO ${dbName.render()};
             GRANT ALL ON SEQUENCE public.album_id_seq TO ${dbName.render()};
             GRANT ALL ON TABLE public.album_images TO ${dbName.render()};
             GRANT ALL ON TABLE public.artists TO ${dbName.render()};
             GRANT ALL ON SEQUENCE public.artist_id_seq TO ${dbName.render()};
             GRANT ALL ON TABLE public.artist_images TO ${dbName.render()};
             GRANT ALL ON TABLE public.reviews TO ${dbName.render()};
             GRANT ALL ON SEQUENCE public.review_id_seq TO ${dbName.render()};
             GRANT ALL ON TABLE public.tracks TO ${dbName.render()};
             GRANT ALL ON TABLE public.users TO ${dbName.render()};
             GRANT ALL ON SEQUENCE public.users_id_seq TO ${dbName.render()};
      """).execute()
      }
    }
  }
}
