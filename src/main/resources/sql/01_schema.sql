CREATE TABLE IF NOT EXISTS "alumno" (
    "id_alumno" SERIAL PRIMARY KEY,
    "nif" VARCHAR(9) UNIQUE NOT NULL,
    "nombre" VARCHAR(100) NOT NULL,
    "email" VARCHAR(100) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS "modulo" (
     "id_modulo" SERIAL PRIMARY KEY,
     "codigo" VARCHAR(40) UNIQUE NOT NULL,
    "nombre" VARCHAR(100) NOT NULL,
    "horas" INT CHECK ("horas" > 0)
    );

CREATE TABLE IF NOT EXISTS "matricula" (
     "id_alumno" INT NOT NULL REFERENCES "alumno"("id_alumno") ON DELETE CASCADE,
    "id_modulo" INT NOT NULL REFERENCES "modulo"("id_modulo") ON DELETE CASCADE,
    "fecha" DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY ("id_alumno", "id_modulo")
    );