-- ========================================
-- SECCIÓN 3: GESTIÓN DE ACTIVIDADES
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
