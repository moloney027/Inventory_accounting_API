CREATE TABLE public."user" (
    id bigint NOT NULL constraint user_pk primary key,
    login character varying(255) UNIQUE,
    password character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    role character varying(255) NOT NULL,
    password_secret character varying(255) NOT NULL
);

CREATE TABLE public.user_refresh_token (
    user_id bigint NOT NULL constraint uft_pk primary key constraint uft_user_fk references public."user",
    refresh_token character varying(255) NOT NULL
);

