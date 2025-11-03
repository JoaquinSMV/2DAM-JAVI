CREATE TABLE alumno
(
    id_alumno SERIAL PRIMARY KEY,
    nombre    VARCHAR(100)        NOT NULL,
    email     VARCHAR(100) UNIQUE NOT NULL
);
CREATE TABLE modulo
(
    id_modulo SERIAL PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    horas     INT CHECK (horas > 0)
);
CREATE TABLE matricula
(
    id_alumno INT NOT NULL REFERENCES alumno (id_alumno) ON DELETE CASCADE,
    id_modulo INT NOT NULL REFERENCES modulo (id_modulo) ON DELETE CASCADE,
    fecha     DATE DEFAULT CURRENT_DATE,
    PRIMARY KEY (id_alumno, id_modulo)
);