-- V1__Create_employee_table.sql

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS public.employee
(
    id serial PRIMARY KEY,
    email character varying(255) COLLATE pg_catalog."default",
    ad character varying(255) COLLATE pg_catalog."default",
    soyad character varying(255) COLLATE pg_catalog."default",
    sifre character varying(255) COLLATE pg_catalog."default",
    flag boolean DEFAULT false,
    uuid uuid NOT NULL DEFAULT uuid_generate_v4(),
    dogrulama boolean DEFAULT false,
    giris_saati timestamp without time zone,
    cikis_saati timestamp without time zone
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.employee
    OWNER to postgres;

REVOKE ALL ON TABLE public.employee FROM employee;

GRANT ALL ON TABLE public.employee TO employee;
GRANT ALL ON TABLE public.employee TO postgres;

GRANT UPDATE, SELECT, DELETE, INSERT ON TABLE public.employee TO employee;

-- Sample insert statement (adjust as necessary)
INSERT INTO public.employee(
    id, email, ad, soyad, sifre, flag, uuid, dogrulama, giris_saati, cikis_saati)
    VALUES (56, 'tokatabdussamed@gmail.com', 'samet', 'dağ', '99', true, '6ced5c70-14f1-4af7-a28f-70a539ae612d', true, '2024-07-19 11:36:43.887524', '');
