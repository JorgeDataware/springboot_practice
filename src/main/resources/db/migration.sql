-- 1. Insertar Divisiones
INSERT INTO division (cve, name, active) VALUES
    ('DTAI', 'División de Tecnologías', true),
    ('DEA', 'División Económico Administrativa', true),
    ('IND', 'División Industrial', true),
    ('IDI', 'División de Idiomas', true);

-- 2. Insertar Ofertas Educativas vinculadas a su División
INSERT INTO oferta_educativa (id, nombre, modalidad, image_url, division_id) VALUES
    -- División Industrial (IND)
    (gen_random_uuid(), 'Ingeniería Mecatrónica', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/ME.png', (SELECT id FROM division WHERE cve = 'IND')),
    (gen_random_uuid(), 'Ingeniería en Energía y Desarrollo Sostenible', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LIEDS2.png', (SELECT id FROM division WHERE cve = 'IND')),
    (gen_random_uuid(), 'Ingeniería Ambiental y Sustentabilidad', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LIAS.png', (SELECT id FROM division WHERE cve = 'IND')),
    (gen_random_uuid(), 'Agricultura Sustentable y Protegida', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/asyp.png', (SELECT id FROM division WHERE cve = 'IND')),
    (gen_random_uuid(), 'Ingeniería en Mantenimiento Industrial', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LIMI.png', (SELECT id FROM division WHERE cve = 'IND')),
    (gen_random_uuid(), 'Ingeniería en Nanotecnología', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LIN.png', (SELECT id FROM division WHERE cve = 'IND')),
    (gen_random_uuid(), 'Ingeniería Industrial', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LII.png', (SELECT id FROM division WHERE cve = 'IND')),
    (gen_random_uuid(), 'Ingeniería Mecánica', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LIM.png', (SELECT id FROM division WHERE cve = 'IND')),
    (gen_random_uuid(), 'Ingeniería Mecánica Automotríz', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/mecanica.png', (SELECT id FROM division WHERE cve = 'IND')),
    (gen_random_uuid(), 'Maestría en Ingeniería para la Manufactura Inteligente en Competencias Profesionales', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/MIMI.png', (SELECT id FROM division WHERE cve = 'IND')),

    -- División de Tecnologías (DTAI)
    (gen_random_uuid(), 'Ingeniería en Tecnologías de la Información e Innovación Digital', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LTII.png', (SELECT id FROM division WHERE cve = 'DTAI')),
    (gen_random_uuid(), 'Ingeniería en Microelectrónica y Semiconductores', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/semiconductores.png', (SELECT id FROM division WHERE cve = 'DTAI')),

    -- División Económico Administrativa (DEA)
    (gen_random_uuid(), 'Licenciatura en Administración', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LA.png', (SELECT id FROM division WHERE cve = 'DEA')),
    (gen_random_uuid(), 'Licenciatura en Negocios y Mercadotecnia', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LNM.png', (SELECT id FROM division WHERE cve = 'DEA')),
    (gen_random_uuid(), 'Ingeniería en Logística', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LIL.png', (SELECT id FROM division WHERE cve = 'DEA')),
    (gen_random_uuid(), 'Licenciatura en Contaduría', 'Modalidad vespertina y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/contaduria.png', (SELECT id FROM division WHERE cve = 'DEA')),
    (gen_random_uuid(), 'Maestría en Dirección Logística y Cadena de Suministro Sostenible en Competencias Profesionales', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/MDICSG.png', (SELECT id FROM division WHERE cve = 'DEA')),
    (gen_random_uuid(), 'Maestría en Economía Circular', 'Modalidad intensiva y mixta', 'https://www.uteq.edu.mx/Images/OfertaEducativa/MEC.png', (SELECT id FROM division WHERE cve = 'DEA')),

    -- División de Idiomas (IDI)
    (gen_random_uuid(), 'Licenciatura en Educación', 'en Enseñanza del Idioma Inglés', 'https://www.uteq.edu.mx/Images/OfertaEducativa/LE.png', (SELECT id FROM division WHERE cve = 'IDI'));