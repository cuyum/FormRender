--
-- PostgreSQL database dump
--

-- Dumped from database version 9.2.1
-- Dumped by pg_dump version 9.2.1
-- Started on 2013-06-25 18:47:43

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 176 (class 3079 OID 11727)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 1974 (class 0 OID 0)
-- Dependencies: 176
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_with_oids = false;

--
-- TOC entry 169 (class 1259 OID 138191)
-- Name: accesos; Type: TABLE; Schema: public; Owner: -
--
DROP TABLE IF EXISTS accesos;
CREATE TABLE accesos (
    idacceso character varying NOT NULL,
    acceso character varying NOT NULL
);


--
-- TOC entry 171 (class 1259 OID 138271)
-- Name: accesos_servicio; Type: TABLE; Schema: public; Owner: -
--
DROP TABLE IF EXISTS accesos_servicio;
CREATE TABLE accesos_servicio (
    idacceso character varying NOT NULL,
    idservicio character varying NOT NULL
);


--
-- TOC entry 175 (class 1259 OID 138385)
-- Name: areas; Type: TABLE; Schema: public; Owner: -
--
DROP TABLE IF EXISTS areas;
CREATE TABLE areas (
    id_localidad bigint NOT NULL,
    id_area_local bigint NOT NULL,
    descr character varying
);


--
-- TOC entry 174 (class 1259 OID 138351)
-- Name: localidades; Type: TABLE; Schema: public; Owner: -
--
DROP TABLE IF EXISTS localidades;
CREATE TABLE localidades (
    descr character varying,
    id_partido bigint NOT NULL,
    id_localidad bigint NOT NULL
);


--
-- TOC entry 173 (class 1259 OID 138307)
-- Name: partidos; Type: TABLE; Schema: public; Owner: -
--
DROP TABLE IF EXISTS partidos;
CREATE TABLE partidos (
    id_prov bigint NOT NULL,
    id_partido bigint NOT NULL,
    descr character varying NOT NULL
);


--
-- TOC entry 168 (class 1259 OID 129330)
-- Name: prestadores; Type: TABLE; Schema: public; Owner: -
--
DROP TABLE IF EXISTS prestadores;
CREATE TABLE prestadores (
    id_bp bigint NOT NULL,
    rs_ap_nom character varying(255),
    tipo_idf character varying(4),
    idf character varying(11),
    estado integer
);


--
-- TOC entry 172 (class 1259 OID 138289)
-- Name: provincias; Type: TABLE; Schema: public; Owner: -
--
DROP TABLE IF EXISTS provincias;
CREATE TABLE provincias (
    id_prov bigint NOT NULL,
    descr character varying
);


--
-- TOC entry 170 (class 1259 OID 138199)
-- Name: servicios; Type: TABLE; Schema: public; Owner: -
--
DROP TABLE IF EXISTS servicios;
CREATE TABLE servicios (
    idservicio character varying NOT NULL,
    servicio character varying NOT NULL
);


--
-- TOC entry 1952 (class 2606 OID 138198)
-- Name: accesos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY accesos
    ADD CONSTRAINT accesos_pkey PRIMARY KEY (idacceso);


--
-- TOC entry 1956 (class 2606 OID 138278)
-- Name: accesos_servicio_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY accesos_servicio
    ADD CONSTRAINT accesos_servicio_pkey PRIMARY KEY (idacceso, idservicio);


--
-- TOC entry 1964 (class 2606 OID 138394)
-- Name: areas_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY areas
    ADD CONSTRAINT areas_pkey PRIMARY KEY (id_localidad, id_area_local);


--
-- TOC entry 1962 (class 2606 OID 138380)
-- Name: localidades_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY localidades
    ADD CONSTRAINT localidades_pkey PRIMARY KEY (id_partido, id_localidad);


--
-- TOC entry 1960 (class 2606 OID 138313)
-- Name: partidos_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partidos
    ADD CONSTRAINT partidos_pkey PRIMARY KEY (id_prov, id_partido);


--
-- TOC entry 1950 (class 2606 OID 129335)
-- Name: prestadores_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY prestadores
    ADD CONSTRAINT prestadores_pkey PRIMARY KEY (id_bp);


--
-- TOC entry 1958 (class 2606 OID 138299)
-- Name: provincias_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY provincias
    ADD CONSTRAINT provincias_pkey PRIMARY KEY (id_prov);


--
-- TOC entry 1954 (class 2606 OID 138206)
-- Name: servicios_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY servicios
    ADD CONSTRAINT servicios_pkey PRIMARY KEY (idservicio);


--
-- TOC entry 1965 (class 2606 OID 138279)
-- Name: accesos_servicio_idacceso_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY accesos_servicio
    ADD CONSTRAINT accesos_servicio_idacceso_fkey FOREIGN KEY (idacceso) REFERENCES accesos(idacceso);


--
-- TOC entry 1966 (class 2606 OID 138284)
-- Name: accesos_servicio_idservicio_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY accesos_servicio
    ADD CONSTRAINT accesos_servicio_idservicio_fkey FOREIGN KEY (idservicio) REFERENCES servicios(idservicio);


--
-- TOC entry 1967 (class 2606 OID 138328)
-- Name: partidos_id_prov_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY partidos
    ADD CONSTRAINT partidos_id_prov_fkey FOREIGN KEY (id_prov) REFERENCES provincias(id_prov);


-- Completed on 2013-06-25 18:47:43

--
-- PostgreSQL database dump complete
--

