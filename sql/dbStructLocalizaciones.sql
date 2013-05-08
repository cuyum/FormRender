SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE TABLE localidades_poblacion (
    id_provincia bigint NOT NULL,
    id_municipio bigint NOT NULL,
    id_localidad bigint NOT NULL,
    localidad character varying,
    id_area_local bigint NOT NULL,
    detalle character varying,
    observaciones character varying,
    id_area_local_access bigint,
    poblacion_2001 bigint,
    poblacion_2002 bigint,
    poblacion_2003 bigint,
    poblacion_2004 bigint,
    poblacion_2005 bigint,
    poblacion_2006 bigint,
    poblacion_2007 bigint,
    poblacion_2008 bigint,
    poblacion_2009 bigint,
    poblacion_2010 bigint,
    poblacion_2011 bigint,
    poblacion_2012 bigint,
    poblacion_2013 bigint,
    poblacion_2014 bigint,
    poblacion_2015 bigint,
    id_loc_pob bigint,
    tipo_vinc character varying,
    detalle_vinc character varying
);


ALTER TABLE public.localidades_poblacion OWNER TO postgres;

--
-- TOC entry 182 (class 1259 OID 109701)
-- Name: localidades_poblacion_id_area_local_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE localidades_poblacion_id_area_local_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.localidades_poblacion_id_area_local_seq OWNER TO postgres;

--
-- TOC entry 1993 (class 0 OID 0)
-- Dependencies: 182
-- Name: localidades_poblacion_id_area_local_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE localidades_poblacion_id_area_local_seq OWNED BY localidades_poblacion.id_area_local;


--
-- TOC entry 1994 (class 0 OID 0)
-- Dependencies: 182
-- Name: localidades_poblacion_id_area_local_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('localidades_poblacion_id_area_local_seq', 1, false);


--
-- TOC entry 181 (class 1259 OID 109699)
-- Name: localidades_poblacion_id_localidad_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE localidades_poblacion_id_localidad_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.localidades_poblacion_id_localidad_seq OWNER TO postgres;

--
-- TOC entry 1995 (class 0 OID 0)
-- Dependencies: 181
-- Name: localidades_poblacion_id_localidad_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE localidades_poblacion_id_localidad_seq OWNED BY localidades_poblacion.id_localidad;


--
-- TOC entry 1996 (class 0 OID 0)
-- Dependencies: 181
-- Name: localidades_poblacion_id_localidad_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('localidades_poblacion_id_localidad_seq', 1, false);


--
-- TOC entry 180 (class 1259 OID 109697)
-- Name: localidades_poblacion_id_municipio_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE localidades_poblacion_id_municipio_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.localidades_poblacion_id_municipio_seq OWNER TO postgres;

--
-- TOC entry 1997 (class 0 OID 0)
-- Dependencies: 180
-- Name: localidades_poblacion_id_municipio_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE localidades_poblacion_id_municipio_seq OWNED BY localidades_poblacion.id_municipio;


--
-- TOC entry 1998 (class 0 OID 0)
-- Dependencies: 180
-- Name: localidades_poblacion_id_municipio_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('localidades_poblacion_id_municipio_seq', 1, false);


--
-- TOC entry 179 (class 1259 OID 109695)
-- Name: localidades_poblacion_id_provincia_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE localidades_poblacion_id_provincia_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.localidades_poblacion_id_provincia_seq OWNER TO postgres;

--
-- TOC entry 1999 (class 0 OID 0)
-- Dependencies: 179
-- Name: localidades_poblacion_id_provincia_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE localidades_poblacion_id_provincia_seq OWNED BY localidades_poblacion.id_provincia;


--
-- TOC entry 2000 (class 0 OID 0)
-- Dependencies: 179
-- Name: localidades_poblacion_id_provincia_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('localidades_poblacion_id_provincia_seq', 1, false);


--
-- TOC entry 178 (class 1259 OID 109581)
-- Name: maestro_areas_locales; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE maestro_areas_locales (
    id_provincia bigint NOT NULL,
    id_area_local bigint NOT NULL,
    area_local character varying,
    indic_inter bigint NOT NULL,
    id_area_local_access bigint NOT NULL
);


ALTER TABLE public.maestro_areas_locales OWNER TO postgres;



CREATE SEQUENCE maestro_areas_locales_id_area_local_access_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.maestro_areas_locales_id_area_local_access_seq OWNER TO postgres;


ALTER SEQUENCE maestro_areas_locales_id_area_local_access_seq OWNED BY maestro_areas_locales.id_area_local_access;




SELECT pg_catalog.setval('maestro_areas_locales_id_area_local_access_seq', 1, false);



CREATE SEQUENCE maestro_areas_locales_id_area_local_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.maestro_areas_locales_id_area_local_seq OWNER TO postgres;



ALTER SEQUENCE maestro_areas_locales_id_area_local_seq OWNED BY maestro_areas_locales.id_area_local;



SELECT pg_catalog.setval('maestro_areas_locales_id_area_local_seq', 1, false);


CREATE SEQUENCE maestro_areas_locales_id_provincia_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.maestro_areas_locales_id_provincia_seq OWNER TO postgres;



ALTER SEQUENCE maestro_areas_locales_id_provincia_seq OWNED BY maestro_areas_locales.id_provincia;

SELECT pg_catalog.setval('maestro_areas_locales_id_provincia_seq', 1, false);

CREATE SEQUENCE maestro_areas_locales_indic_inter_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.maestro_areas_locales_indic_inter_seq OWNER TO postgres;


ALTER SEQUENCE maestro_areas_locales_indic_inter_seq OWNED BY maestro_areas_locales.indic_inter;

SELECT pg_catalog.setval('maestro_areas_locales_indic_inter_seq', 1, false);


CREATE TABLE maestro_provincias (
    id_prov bigint NOT NULL,
    descripcion character varying
);


ALTER TABLE public.maestro_provincias OWNER TO postgres;


CREATE SEQUENCE maestro_provincias_id_prov_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.maestro_provincias_id_prov_seq OWNER TO postgres;

ALTER SEQUENCE maestro_provincias_id_prov_seq OWNED BY maestro_provincias.id_prov;


SELECT pg_catalog.setval('maestro_provincias_id_prov_seq', 1, false);


CREATE TABLE p_dpto_partido_id (
    id_prov bigint,
    id_dpto_partido bigint,
    descripcion character varying
);


ALTER TABLE public.p_dpto_partido_id OWNER TO postgres;


ALTER TABLE ONLY localidades_poblacion ALTER COLUMN id_provincia SET DEFAULT nextval('localidades_poblacion_id_provincia_seq'::regclass);

ALTER TABLE ONLY localidades_poblacion ALTER COLUMN id_municipio SET DEFAULT nextval('localidades_poblacion_id_municipio_seq'::regclass);

ALTER TABLE ONLY localidades_poblacion ALTER COLUMN id_localidad SET DEFAULT nextval('localidades_poblacion_id_localidad_seq'::regclass);

ALTER TABLE ONLY localidades_poblacion ALTER COLUMN id_area_local SET DEFAULT nextval('localidades_poblacion_id_area_local_seq'::regclass);


ALTER TABLE ONLY maestro_areas_locales ALTER COLUMN id_provincia SET DEFAULT nextval('maestro_areas_locales_id_provincia_seq'::regclass);

ALTER TABLE ONLY maestro_areas_locales ALTER COLUMN id_area_local SET DEFAULT nextval('maestro_areas_locales_id_area_local_seq'::regclass);
ALTER TABLE ONLY maestro_areas_locales ALTER COLUMN indic_inter SET DEFAULT nextval('maestro_areas_locales_indic_inter_seq'::regclass);
ALTER TABLE ONLY maestro_areas_locales ALTER COLUMN id_area_local_access SET DEFAULT nextval('maestro_areas_locales_id_area_local_access_seq'::regclass);
ALTER TABLE ONLY maestro_provincias ALTER COLUMN id_prov SET DEFAULT nextval('maestro_provincias_id_prov_seq'::regclass);
