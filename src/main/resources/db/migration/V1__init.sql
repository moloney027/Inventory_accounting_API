--
-- PostgreSQL database dump
--

---- Dumped from database version 15.0
---- Dumped by pg_dump version 15.0
--
--SET statement_timeout = 0;
--SET lock_timeout = 0;
--SET idle_in_transaction_session_timeout = 0;
--SET client_encoding = 'UTF8';
--SET standard_conforming_strings = on;
--SELECT pg_catalog.set_config('search_path', '', false);
--SET check_function_bodies = false;
--SET xmloption = content;
--SET client_min_messages = warning;
--SET row_security = off;
--
--SET default_tablespace = '';
--
--SET default_table_access_method = heap;

--
-- Name: document_moving; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.document_moving (
    id bigint NOT NULL,
    count bigint,
    number character varying(255),
    from_storage_id bigint NOT NULL,
    product_id bigint NOT NULL,
    to_storage_id bigint NOT NULL
);


--ALTER TABLE public.document_moving OWNER TO postgres;

--
-- Name: document_receipt; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.document_receipt (
    id bigint NOT NULL,
    count bigint,
    number character varying(255),
    purchase_price numeric(19,2),
    product_id bigint NOT NULL,
    storage_id bigint NOT NULL
);


--ALTER TABLE public.document_receipt OWNER TO postgres;

--
-- Name: document_sale; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.document_sale (
    id bigint NOT NULL,
    count bigint,
    number character varying(255),
    sale_price numeric(19,2),
    product_id bigint NOT NULL,
    storage_id bigint NOT NULL
);


--ALTER TABLE public.document_sale OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: inventory_control; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventory_control (
    id bigint NOT NULL,
    count bigint,
    product_id bigint,
    storage_id bigint
);


--ALTER TABLE public.inventory_control OWNER TO postgres;

--
-- Name: product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product (
    id bigint NOT NULL,
    archive boolean DEFAULT false,
    article character varying(255),
    last_purchase_price numeric(19,2),
    last_sale_price numeric(19,2),
    name character varying(255)
);


--ALTER TABLE public.product OWNER TO postgres;

--
-- Name: storage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.storage (
    id bigint NOT NULL,
    archive boolean DEFAULT false,
    name character varying(255)
);


--ALTER TABLE public.storage OWNER TO postgres;

--
-- Name: document_moving document_moving_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_moving
    ADD CONSTRAINT document_moving_pkey PRIMARY KEY (id);


--
-- Name: document_receipt document_receipt_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_receipt
    ADD CONSTRAINT document_receipt_pkey PRIMARY KEY (id);


--
-- Name: document_sale document_sale_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_sale
    ADD CONSTRAINT document_sale_pkey PRIMARY KEY (id);


--
-- Name: inventory_control inventory_control_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.inventory_control
    ADD CONSTRAINT inventory_control_pkey PRIMARY KEY (id);


--
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- Name: storage storage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.storage
    ADD CONSTRAINT storage_pkey PRIMARY KEY (id);


--
-- Name: document_receipt fk14loencwpystgh7oq4n52010; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_receipt
    ADD CONSTRAINT fk14loencwpystgh7oq4n52010 FOREIGN KEY (product_id) REFERENCES public.product(id) ON DELETE CASCADE;


--
-- Name: inventory_control fk45dg8rhdp5jadbikgv4u58voh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.inventory_control
    ADD CONSTRAINT fk45dg8rhdp5jadbikgv4u58voh FOREIGN KEY (product_id) REFERENCES public.product(id) ON DELETE CASCADE;


--
-- Name: document_sale fkc5i81ff7ric93hm6patwmdq02; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_sale
    ADD CONSTRAINT fkc5i81ff7ric93hm6patwmdq02 FOREIGN KEY (storage_id) REFERENCES public.storage(id) ON DELETE CASCADE;


--
-- Name: document_moving fkedv60vf7dg336df0b7adbw1nm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_moving
    ADD CONSTRAINT fkedv60vf7dg336df0b7adbw1nm FOREIGN KEY (to_storage_id) REFERENCES public.storage(id) ON DELETE CASCADE;


--
-- Name: document_sale fkfifodh9xw54rho10a0kslj1md; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_sale
    ADD CONSTRAINT fkfifodh9xw54rho10a0kslj1md FOREIGN KEY (product_id) REFERENCES public.product(id) ON DELETE CASCADE;


--
-- Name: inventory_control fkio8v0oqi6ewl4vtatxic39521; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.inventory_control
    ADD CONSTRAINT fkio8v0oqi6ewl4vtatxic39521 FOREIGN KEY (storage_id) REFERENCES public.storage(id) ON DELETE CASCADE;


--
-- Name: document_receipt fkluwqel0d8f3k5w2yjmo9dle4s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_receipt
    ADD CONSTRAINT fkluwqel0d8f3k5w2yjmo9dle4s FOREIGN KEY (storage_id) REFERENCES public.storage(id) ON DELETE CASCADE;


--
-- Name: document_moving fknbdmj4noyxt5gu9fnf5fmrqfn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_moving
    ADD CONSTRAINT fknbdmj4noyxt5gu9fnf5fmrqfn FOREIGN KEY (from_storage_id) REFERENCES public.storage(id) ON DELETE CASCADE;


--
-- Name: document_moving fkqowfn3nst7y11a9bar3p9anf6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE public.document_moving
    ADD CONSTRAINT fkqowfn3nst7y11a9bar3p9anf6 FOREIGN KEY (product_id) REFERENCES public.product(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

