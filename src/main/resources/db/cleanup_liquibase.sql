-- Script para eliminar tablas de control de Liquibase
-- Ejecutar en PostgreSQL antes de iniciar la aplicación con DDL auto

DROP TABLE IF EXISTS databasechangeloglock CASCADE;
DROP TABLE IF EXISTS databasechangelog CASCADE;

-- Verificar que las tablas fueron eliminadas
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_name LIKE 'database%';
