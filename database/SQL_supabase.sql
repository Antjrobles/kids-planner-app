-- ========================================
-- DROPS: Eliminar tablas existentes
-- ========================================
-- Orden: primero tablas dependientes, luego tablas principales

-- Tablas con dependencias de nivel 3
DROP TABLE IF EXISTS public.reminders CASCADE;
DROP TABLE IF EXISTS public.documents CASCADE;
DROP TABLE IF EXISTS public.attendance CASCADE;

-- Tablas con dependencias de nivel 2
DROP TABLE IF EXISTS public.materials CASCADE;
DROP TABLE IF EXISTS public.payments CASCADE;

-- Tablas con dependencias de nivel 1
DROP TABLE IF EXISTS public.activities CASCADE;
DROP TABLE IF EXISTS public.family_members CASCADE;
DROP TABLE IF EXISTS public.children CASCADE;

-- Tablas base
DROP TABLE IF EXISTS public.families CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;

-- ========================================
-- CREACIÓN DE TABLAS
-- ========================================

-- Almaceno las actividades extraescolares de los niños con horarios, profesores y ubicaciones
CREATE TABLE public.activities (
  id bigint NOT NULL DEFAULT nextval('activities_id_seq'::regclass),
  family_id bigint NOT NULL,
  child_id bigint NOT NULL,
  title text NOT NULL,
  description text,
  place text,
  teacher_name text,
  teacher_phone text,
  teacher_email text,
  weekday text,
  start_time time without time zone,
  end_time time without time zone,
  is_weekend boolean DEFAULT false,
  is_match boolean DEFAULT false,
  match_location text,
  match_opponent text,
  fee numeric,
  frequency text DEFAULT 'weekly'::text,
  active boolean DEFAULT true,
  notes text,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT activities_pkey PRIMARY KEY (id),
  CONSTRAINT activities_family_id_fkey FOREIGN KEY (family_id) REFERENCES public.families(id),
  CONSTRAINT activities_child_id_fkey FOREIGN KEY (child_id) REFERENCES public.children(id)
);

-- Registro la asistencia de los niños a cada actividad con fecha y notas
CREATE TABLE public.attendance (
  id bigint NOT NULL DEFAULT nextval('attendance_id_seq'::regclass),
  activity_id bigint NOT NULL,
  child_id bigint NOT NULL,
  attended boolean DEFAULT true,
  attended_date date NOT NULL,
  notes text,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT attendance_pkey PRIMARY KEY (id),
  CONSTRAINT attendance_activity_id_fkey FOREIGN KEY (activity_id) REFERENCES public.activities(id),
  CONSTRAINT attendance_child_id_fkey FOREIGN KEY (child_id) REFERENCES public.children(id)
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

-- Almaceno documentos asociados a familias, niños o actividades con URL del archivo
CREATE TABLE public.documents (
  id bigint NOT NULL DEFAULT nextval('documents_id_seq'::regclass),
  family_id bigint NOT NULL,
  child_id bigint,
  activity_id bigint,
  doc_type text,
  title text NOT NULL,
  file_url text,
  uploaded_at timestamp with time zone DEFAULT now(),
  CONSTRAINT documents_pkey PRIMARY KEY (id),
  CONSTRAINT documents_family_id_fkey FOREIGN KEY (family_id) REFERENCES public.families(id),
  CONSTRAINT documents_child_id_fkey FOREIGN KEY (child_id) REFERENCES public.children(id),
  CONSTRAINT documents_activity_id_fkey FOREIGN KEY (activity_id) REFERENCES public.activities(id)
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

-- Gestiono los materiales necesarios para cada actividad con checklist de verificación
CREATE TABLE public.materials (
  id bigint NOT NULL DEFAULT nextval('materials_id_seq'::regclass),
  activity_id bigint NOT NULL,
  name text NOT NULL,
  description text,
  quantity integer DEFAULT 1,
  checked boolean DEFAULT false,
  notes text,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT materials_pkey PRIMARY KEY (id),
  CONSTRAINT materials_activity_id_fkey FOREIGN KEY (activity_id) REFERENCES public.activities(id)
);

-- Controlo los pagos de actividades con fechas de vencimiento, estado y método de pago
CREATE TABLE public.payments (
  id bigint NOT NULL DEFAULT nextval('payments_id_seq'::regclass),
  family_id bigint NOT NULL,
  activity_id bigint NOT NULL,
  amount numeric NOT NULL,
  due_date date NOT NULL,
  status text DEFAULT 'pending'::text,
  paid_date date,
  payment_method text,
  notes text,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT payments_pkey PRIMARY KEY (id),
  CONSTRAINT payments_family_id_fkey FOREIGN KEY (family_id) REFERENCES public.families(id),
  CONSTRAINT payments_activity_id_fkey FOREIGN KEY (activity_id) REFERENCES public.activities(id)
);

-- Programo recordatorios para actividades y pagos con notificaciones automáticas
CREATE TABLE public.reminders (
  id bigint NOT NULL DEFAULT nextval('reminders_id_seq'::regclass),
  family_id bigint NOT NULL,
  activity_id bigint,
  payment_id bigint,
  title text NOT NULL,
  description text,
  reminder_type text NOT NULL,
  reminder_time timestamp with time zone NOT NULL,
  is_sent boolean DEFAULT false,
  is_read boolean DEFAULT false,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT reminders_pkey PRIMARY KEY (id),
  CONSTRAINT reminders_family_id_fkey FOREIGN KEY (family_id) REFERENCES public.families(id),
  CONSTRAINT reminders_activity_id_fkey FOREIGN KEY (activity_id) REFERENCES public.activities(id),
  CONSTRAINT reminders_payment_id_fkey FOREIGN KEY (payment_id) REFERENCES public.payments(id)
);

-- Registro los usuarios del sistema con email único, nombre completo y teléfono
CREATE TABLE public.users (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  email text NOT NULL UNIQUE,
  full_name text,
  phone text,
  created_at timestamp with time zone DEFAULT now(),
  CONSTRAINT users_pkey PRIMARY KEY (id)
);