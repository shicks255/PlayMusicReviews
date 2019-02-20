# PlayMusicReviews
My first application using Scala and the Play framework.  Simple site for music album reviews.

This project also requires my Last_FM Api 
(https://github.com/shicks255/LastFM_API)

Setup the necessary database info in application-sample.config, and rename removing the -sample

To run, setup a PostgreSQL database with the following commands:
Make sure to grant correct priviliges to user defined in application.config

album Table<br/>
CREATE TABLE public.albums
(
    id integer NOT NULL DEFAULT nextval('album_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    year integer DEFAULT 1900,
    artist_id integer,
    mbid character varying(50) COLLATE pg_catalog."default",
    url character varying COLLATE pg_catalog."default",
    CONSTRAINT "ALBUM_MBID_UNIQUE" UNIQUE (mbid)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.albums
    OWNER to postgres;
GRANT ALL ON TABLE public.albums TO postgres;
---------------------------------------------
album_images table
CREATE TABLE public.album_images
(
    album_id numeric,
    text character varying COLLATE pg_catalog."default",
    url character varying COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.album_images
    OWNER to postgres;
GRANT ALL ON TABLE public.album_images TO postgres;
-----------------------------------------------
artists table
CREATE TABLE public.artists
(
    id integer NOT NULL DEFAULT nextval('artist_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    summary text COLLATE pg_catalog."default",
    content text COLLATE pg_catalog."default",
    mbid character varying COLLATE pg_catalog."default",
    CONSTRAINT "ARTIST_MBID_UNIQUE" UNIQUE (mbid)
,
    CONSTRAINT "ARTIST_NAME_UNIQUE" UNIQUE (name)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.artists
    OWNER to postgres;
GRANT ALL ON TABLE public.artists TO postgres;
---------------------------------------------
artist_images table
CREATE TABLE public.artist_images
(
    artist_id numeric,
    text character varying COLLATE pg_catalog."default",
    url character varying COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.artist_images
    OWNER to postgres;
GRANT ALL ON TABLE public.artist_images TO postgres;
---------------------------------------------------
reviews table
CREATE TABLE public.reviews
(
    id integer NOT NULL DEFAULT nextval('review_id_seq'::regclass),
    album_id integer,
    user_id integer,
    content text COLLATE pg_catalog."default",
    added_on timestamp with time zone
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.reviews
    OWNER to postgres;
GRANT ALL ON TABLE public.reviews TO postgres;
----------------------------------------------
tracks table
CREATE TABLE public.tracks
(
    album_id numeric,
    name character varying COLLATE pg_catalog."default",
    rank numeric,
    duration numeric
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.tracks
    OWNER to postgres;
GRANT ALL ON TABLE public.tracks TO postgres;
---------------------------------------------
users table
CREATE TABLE public.users
(
    id integer NOT NULL DEFAULT nextval('users_id_seq'::regclass),
    username character(75) COLLATE pg_catalog."default",
    password character(50) COLLATE pg_catalog."default",
    CONSTRAINT unique_username UNIQUE (username)

)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.users
    OWNER to postgres;
GRANT ALL ON TABLE public.users TO postgres;