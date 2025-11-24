CREATE OR REPLACE FUNCTION count_enrollments(student_id INT)
RETURNS INT AS $$
DECLARE
total INT;
BEGIN
SELECT COUNT(*) INTO total FROM matricula WHERE id_alumno = student_id;
RETURN total;
END;
$$ LANGUAGE plpgsql;
