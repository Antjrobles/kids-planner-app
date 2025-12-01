-- ========================================
-- SECCIÓN 2: TABLAS BASE (Usuarios y Familias)
-- ========================================

-- Registro los usuarios del sistema con email único, nombre completo y teléfono
CREATE TABLE public.users (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  email text NOT NULL UNIQUE,
  full_name text,
  phone text,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- Defino los grupos familiares creados por usuarios para gestionar sus hijos y actividades
CREATE TABLE public.families (
  id bigint NOT NULL DEFAULT nextval('families_id_seq'::regclass),
  name text NOT NULL,
  created_by uuid NOT NULL,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT families_pkey PRIMARY KEY (id),
  CONSTRAINT families_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id)
);

-- Relaciono usuarios con familias asignando roles (admin, member) para permisos
CREATE TABLE public.family_members (
  id bigint NOT NULL DEFAULT nextval('family_members_id_seq'::regclass),
  family_id bigint NOT NULL,
  user_id uuid NOT NULL,
  role text DEFAULT 'member'::text,
  joined_at timestamp with time zone DEFAULT now(),
  CONSTRAINT family_members_pkey PRIMARY KEY (id),
  CONSTRAINT family_members_family_id_fkey FOREIGN KEY (family_id) REFERENCES public.families(id),
  CONSTRAINT family_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id)
);

-- Guardo los perfiles de los niños con datos personales, médicos y foto
CREATE TABLE public.children (
  id bigint NOT NULL DEFAULT nextval('children_id_seq'::regclass),
  family_id bigint NOT NULL,
  name text NOT NULL,
  birthdate date,
  grade text,
  allergies text,
  medical_notes text,
  photo_url text,
  notes text,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT children_pkey PRIMARY KEY (id),
  CONSTRAINT children_family_id_fkey FOREIGN KEY (family_id) REFERENCES public.families(id)
);
