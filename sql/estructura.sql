--
-- PostgreSQL database dump
--

-- Dumped from database version 9.2.1
-- Dumped by pg_dump version 9.2.1
-- Started on 2013-08-01 10:41:07

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 172 (class 3079 OID 11727)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 1940 (class 0 OID 0)
-- Dependencies: 172
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 169 (class 1259 OID 173079)
-- Name: formulario; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--
DROP TABLE IF EXISTS formulario;
CREATE TABLE formulario (   
    id bigint NOT NULL,
    archivo character varying(255) NOT NULL,
    codigo character varying(255) NOT NULL,
    formversion character varying(255),
    nombre character varying(255) NOT NULL,
    url character varying(255),
    version integer,
    xsltransform_id bigint NOT NULL
);


ALTER TABLE public.formulario OWNER TO postgres;

--
-- TOC entry 168 (class 1259 OID 173077)
-- Name: formulario_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE formulario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.formulario_id_seq OWNER TO postgres;

--
-- TOC entry 1941 (class 0 OID 0)
-- Dependencies: 168
-- Name: formulario_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE formulario_id_seq OWNED BY formulario.id;


--
-- TOC entry 1942 (class 0 OID 0)
-- Dependencies: 168
-- Name: formulario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('formulario_id_seq', 1, false);


--
-- TOC entry 171 (class 1259 OID 173090)
-- Name: xsl; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--
DROP TABLE IF EXISTS xsl;
CREATE TABLE xsl (
    id bigint NOT NULL,
    archivo character varying(255) NOT NULL,
    nombre character varying(255) NOT NULL,
    url character varying(255),
    version integer,
    xlsversion character varying(255)
);


ALTER TABLE public.xsl OWNER TO postgres;

--
-- TOC entry 170 (class 1259 OID 173088)
-- Name: xsl_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE xsl_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.xsl_id_seq OWNER TO postgres;

--
-- TOC entry 1943 (class 0 OID 0)
-- Dependencies: 170
-- Name: xsl_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE xsl_id_seq OWNED BY xsl.id;


--
-- TOC entry 1944 (class 0 OID 0)
-- Dependencies: 170
-- Name: xsl_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('xsl_id_seq', 1, true);


--
-- TOC entry 1924 (class 2604 OID 173082)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY formulario ALTER COLUMN id SET DEFAULT nextval('formulario_id_seq'::regclass);


--
-- TOC entry 1925 (class 2604 OID 173093)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY xsl ALTER COLUMN id SET DEFAULT nextval('xsl_id_seq'::regclass);

ALTER TABLE ONLY formulario
    ADD CONSTRAINT formulario_codigo_key UNIQUE (codigo);

ALTER TABLE ONLY formulario
    ADD CONSTRAINT formulario_pkey PRIMARY KEY (id);


--
-- TOC entry 1929 (class 2606 OID 173098)
-- Name: xsl_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY xsl
    ADD CONSTRAINT xsl_pkey PRIMARY KEY (id);


--
-- TOC entry 1930 (class 2606 OID 173099)
-- Name: fkabd50212b093d15; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY formulario
    ADD CONSTRAINT fkabd50212b093d15 FOREIGN KEY (xsltransform_id) REFERENCES xsl(id);


--
-- TOC entry 1939 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2013-08-01 10:41:07

--
-- PostgreSQL database dump complete
--

