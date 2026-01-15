-- Borrar versiones anteriores para evitar conflictos de tipo
DROP PROCEDURE IF EXISTS count_enrollments(INT);
DROP FUNCTION IF EXISTS count_enrollments(INT);

CREATE OR REPLACE PROCEDURE count_enrollments(
    IN student_id_param INT,
    OUT total INT
)
LANGUAGE plpgsql
AS $$
BEGIN
SELECT COUNT(*) INTO total
FROM "matricula"
WHERE id_alumno = student_id_param;
END;
$$;
^^