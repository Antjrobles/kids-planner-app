-- ========================================
-- SECCIÓN 1: DROPS
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
