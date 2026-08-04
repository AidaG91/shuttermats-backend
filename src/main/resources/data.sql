-- Tarifa general de cobertura (precio base del primer combate + precio del
-- combate extra), la que se aplica a cualquier evento salvo que se le
-- asigne otra tarifa distinta al crearlo. Va primero porque los eventos de
-- abajo la referencian. Editable desde el admin (Ajustes > Tarifas) sin
-- necesidad de tocar código ni redeploy.
INSERT INTO pricing_plans (id, name, base_price, extra_match_price, is_default, created_at, updated_at) VALUES
    (1, 'General', 35.00, 25.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- El insert de arriba fija id=1 a mano, pero la secuencia interna de la
-- columna IDENTITY no se entera de eso y sigue pensando que el próximo id
-- libre es el 1: la siguiente tarifa creada desde el admin choca con un
-- "llave duplicada" en pricing_plans_pkey. Este setval sincroniza la
-- secuencia con el id máximo real ya insertado, cada vez que arranca el
-- backend, así que es idempotente.
SELECT setval(pg_get_serial_sequence('pricing_plans', 'id'), COALESCE((SELECT MAX(id) FROM pricing_plans), 1));

-- Seed de eventos reales de la escena de grappling/BJJ en Catalunya.
--
-- ON CONFLICT evita duplicados: la tabla tiene una restricción UNIQUE
-- sobre (name, date), así que reiniciar el backend no vuelve a insertar
-- filas que ya existan. Todos parten de la tarifa General (id=1); cámbialo
-- desde el admin si alguno necesita una tarifa propia (p.ej. Polaris).
INSERT INTO events (name, date, location, image_url, description, registration_url, base_price, extra_match_price, pricing_plan_id, created_at, updated_at) VALUES
    ('Torredembarra Challenge Summer 2025', '2025-06-21', 'Torredembarra, Tarragona',
     '/images/events/torredembarra-challenge-summer-2025.jpg',
     'Torneo de grappling gi y no-gi en Torredembarra, con divisiones para todos los niveles y un ambiente muy familiar en la costa de Tarragona.',
     NULL, 35.00, 25.00, 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Vinyols Challenge Spring 2026', '2026-05-02', 'Vinyols i els Arcs, Tarragona',
     '/images/events/vinyols-challenge-spring-2026.jpg',
     'Edición de primavera del Vinyols Challenge, con formato gi y no-gi y divisiones desde los 4 años hasta veteranos.',
     NULL, 35.00, 25.00, 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Polaris Open', '2026-08-29', 'Sabadell, Barcelona',
     '/images/events/polaris-barcelona-2026.jpg',
     'Polaris Open llega a Sabadell con superfights de nivel profesional y algunos de los mejores grapplers de Europa sobre el tatami.',
     NULL, 35.00, 25.00, 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Cambrils Beach Challenge', '2026-09-20', 'Cambrils, Tarragona',
     '/images/events/cambrils-beach-challenge-2026.png',
     'Torneo de grappling en la playa de Cambrils, con formato gi y no-gi y un ambiente único junto al mar en la Costa Daurada.',
     NULL, 35.00, 25.00, 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Penedès Challenge', '2026-10-10', 'Bellvei, Tarragona',
     '/images/events/penedes-challenge-2026.jpg',
     'Torneo de grappling del Penedès celebrado en Bellvei, con divisiones gi y no-gi para todas las edades y niveles en un entorno cercano y competitivo.',
     NULL, 35.00, 25.00, 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Vinyols Challenge Fall', '2026-11-14', 'Vinyols i els Arcs, Tarragona',
     '/images/events/vinyols-challenge-fall-2026.png',
     'Edición de otoño del Vinyols Challenge, cerrando el año de competición en el Camp de Tarragona con gi y no-gi.',
     NULL, 35.00, 25.00, 1,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name, date) DO NOTHING;

-- Extras de cobertura de atleta (ver shuttermats_precios_cobertura_atleta.pdf V1.0).
INSERT INTO coverage_extra (name, price, active, created_at, updated_at) VALUES
    ('Calentamiento', 15.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Entrega rápida', 20.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
