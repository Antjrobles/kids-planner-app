-- ========================================
-- SECCIÓN 4: PAGOS, DOCUMENTOS Y RECORDATORIOS
-- ========================================

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
