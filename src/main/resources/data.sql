-- testdaten, laedt automatisch beim start
-- (WHERE NOT EXISTS-Guards ergaenzt, damit bei persistenter DB kein duplicate-key-fehler entsteht)

-- 2 standorte, ca 1h auseinander (wichtig fuer vertretung/verteilung)
INSERT INTO studio (name, adresse)
SELECT * FROM (VALUES
    ('Standort A', 'Hauptstr. 12, 47798 Krefeld'),
    ('Standort B', 'Bahnhofallee 45, 47798 Krefeld')
) AS v(name, adresse)
WHERE NOT EXISTS (SELECT 1 FROM studio);

INSERT INTO raum (name, studio_id)
SELECT * FROM (VALUES
    ('Kursraum 1', 1),
    ('Kursraum 2', 1),
    ('Kursraum 1', 2),
    ('Kursraum 2', 2)
) AS v(name, studio_id)
WHERE NOT EXISTS (SELECT 1 FROM raum);

-- pw base64 "verschluesselt"
INSERT INTO mitarbeiter (vorname, nachname, email, passwort, rolle, typ, max_kurse_pro_woche, studio_id)
SELECT * FROM (VALUES
    ('Lisa', 'Fit', 'lisa.fit@fitaktiv.de', 'YWRtaW4xMjM=', 'ADMIN', NULL, 0, NULL),
    ('Fabian', 'Nguyen', 'fabian.nguyen@fitaktiv.de', 'dHJhaW5lcjEyMw==', 'TRAINER', 'VOLLZEIT', 20, 1),
    ('Ayse', 'Celik', 'ayse.celik@fitaktiv.de', 'dHJhaW5lcjEyMw==', 'TRAINER', 'TEILZEIT', 10, 1),
    ('Niklas', 'Brandt', 'niklas.brandt@fitaktiv.de', 'dHJhaW5lcjEyMw==', 'TRAINER', 'VOLLZEIT', 20, 1),
    ('Marie', 'Feldmann', 'marie.feldmann@fitaktiv.de', 'dHJhaW5lcjEyMw==', 'TRAINER', 'VOLLZEIT', 20, 2),
    ('Tobias', 'Dreher', 'tobias.dreher@fitaktiv.de', 'dHJhaW5lcjEyMw==', 'TRAINER', 'TEILZEIT', 10, 2),
    ('Ines', 'Pawlak', 'ines.pawlak@fitaktiv.de', 'dHJhaW5lcjEyMw==', 'TRAINER', 'VOLLZEIT', 20, 2)
) AS v(vorname, nachname, email, passwort, rolle, typ, max_kurse_pro_woche, studio_id)
WHERE NOT EXISTS (SELECT 1 FROM mitarbeiter);


INSERT INTO mitarbeiter (vorname, nachname, email, passwort, rolle, typ, max_kurse_pro_woche, studio_id)
VALUES ('Ines', 'Pawlak', 'ines.pawlak@fitaktiv.de', 'dHJhaW5lcjEyMw==', 'TRAINER', 'VOLLZEIT', 20, 2);

INSERT INTO mitglied (vorname, nachname, email)
SELECT * FROM (VALUES
    ('Anna', 'Weber', 'anna.weber@mail.de'),
    ('Tom', 'Fischer', 'tom.fischer@mail.de'),
    ('Lea', 'Hoffmann', 'lea.hoffmann@mail.de')
) AS v(vorname, nachname, email)
WHERE NOT EXISTS (SELECT 1 FROM mitglied);

INSERT INTO kurs (name, wochentag, startzeit, dauer_minuten, trainer_id, vertretung_trainer_id, raum_id, status)
SELECT * FROM (VALUES
    ('Yoga', 'Montag', '08:00', 60, 2, NULL, 1, 'GEPLANT'),
    ('Spinning', 'Montag', '18:00', 60, 3, NULL, 2, 'GEPLANT'),
    ('Krafttraining', 'Dienstag', '09:00', 60, 5, NULL, 3, 'GEPLANT'),
    ('Zumba', 'Mittwoch', '19:00', 60, 2, NULL, 2, 'GEPLANT'),
    ('Pilates', 'Donnerstag', '10:00', 60, NULL, NULL, 1, 'GEPLANT'),
    ('Bauch-Beine-Po', 'Freitag', '17:00', 60, NULL, NULL, 4, 'GEPLANT')
) AS v(name, wochentag, startzeit, dauer_minuten, trainer_id, vertretung_trainer_id, raum_id, status)
WHERE NOT EXISTS (SELECT 1 FROM kurs);

INSERT INTO teilnahme (kurs_id, mitglied_id)
SELECT * FROM (VALUES
    (1, 1),
    (1, 2),
    (2, 2),
    (2, 3),
    (4, 1)
) AS v(kurs_id, mitglied_id)
WHERE NOT EXISTS (SELECT 1 FROM teilnahme);

INSERT INTO verfuegbarkeit (trainer_id, wochentag, von, bis)
SELECT * FROM (VALUES
    (2, 'Montag', '07:00', '20:00'),
    (2, 'Mittwoch', '07:00', '20:00'),
    (3, 'Montag', '16:00', '20:00'),
    (3, 'Mittwoch', '17:00', '21:00'),
    (4, 'Montag', '07:00', '20:00'),
    (4, 'Dienstag', '07:00', '20:00'),
    (5, 'Dienstag', '07:00', '20:00'),
    (5, 'Freitag', '07:00', '20:00'),
    (6, 'Dienstag', '07:00', '14:00'),
    (7, 'Freitag', '07:00', '20:00')
) AS v(trainer_id, wochentag, von, bis)
WHERE NOT EXISTS (SELECT 1 FROM verfuegbarkeit);