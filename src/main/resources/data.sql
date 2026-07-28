-- Seed de eventos reales de la escena de grappling/BJJ en Catalunya.
--
-- ON CONFLICT evita duplicados: la tabla tiene una restricción UNIQUE
-- sobre (name, date), así que reiniciar el backend no vuelve a insertar
-- filas que ya existan.
INSERT INTO events (name, date, location, image_url, description, created_at, updated_at) VALUES
    ('Torredembarra Challenge Summer 2025', '2025-06-21', 'Torredembarra, Tarragona',
     '/images/events/torredembarra-challenge-summer-2025.jpg',
     'Torneo de grappling gi y no-gi en Torredembarra, con divisiones para todos los niveles y un ambiente muy familiar en la costa de Tarragona.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Vinyols Challenge Spring 2026', '2026-05-02', 'Vinyols i els Arcs, Tarragona',
     '/images/events/vinyols-challenge-spring-2026.jpg',
     'Edición de primavera del Vinyols Challenge, con formato gi y no-gi y divisiones desde los 4 años hasta veteranos.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Polaris Open', '2026-08-29', 'Sabadell, Barcelona',
     '/images/events/polaris-barcelona-2026.jpg',
     'Polaris Open llega a Sabadell con superfights de nivel profesional y algunos de los mejores grapplers de Europa sobre el tatami.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Cambrils Beach Challenge', '2026-09-20', 'Cambrils, Tarragona',
     '/images/events/cambrils-beach-challenge-2026.png',
     'Torneo de grappling en la playa de Cambrils, con formato gi y no-gi y un ambiente único junto al mar en la Costa Daurada.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Penedès Challenge', '2026-10-10', 'Bellvei, Tarragona',
     '/images/events/penedes-challenge-2026.jpg',
     'Torneo de grappling del Penedès celebrado en Bellvei, con divisiones gi y no-gi para todas las edades y niveles en un entorno cercano y competitivo.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Vinyols Challenge Fall', '2026-11-14', 'Vinyols i els Arcs, Tarragona',
     '/images/events/vinyols-challenge-fall-2026.png',
     'Edición de otoño del Vinyols Challenge, cerrando el año de competición en el Camp de Tarragona con gi y no-gi.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name, date) DO NOTHING;

-- Extras de cobertura de atleta (ver shuttermats_precios_cobertura_atleta.pdf V1.0).
-- Precio base (primer combate 35€, combate extra +25€) se calcula más adelante,
-- de momento solo se seedan los extras opcionales con precio fijo.
INSERT INTO coverage_extra (name, price, active, created_at, updated_at) VALUES
    ('Calentamiento', 15.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Entrega rápida', 20.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
