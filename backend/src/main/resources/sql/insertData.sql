INSERT INTO hallplan (id, name, description, is_template)
VALUES (-1, 'Hall A', 'Large concert hall', true),
       (-2, 'Hall B', 'Intimate theater', true),
       (-3, 'Hall C', 'Open-air amphitheater', true),
       (-4, 'Hall D', 'Conference center', true),
       (-5, 'Hall E', 'Sports arena', true),
       (-6, 'Hall F', 'Small music venue', false),
       (-7, 'Hall G', 'Cinema complex', false),
       (-8, 'Hall H', 'Museum exhibition space', true);

INSERT INTO seatrow (id, row_nr, hallplan_id)
VALUES
    (-1, 1, -1),
    (-2, 2, -1),
    (-3, 3, -1),
    (-4, 4, -1),
    (-5, 1, -2),
    (-6, 2, -2),
    (-7, 3, -3),
    (-8, 1, -4);

INSERT INTO section (id, name, color, price, hallplan_id)
VALUES
    (-1, 'VIP', 'gold', 100, -1),
    (-2, 'Front Row', 'red', 80, -1),
    (-3, 'Middle Section', 'green', 60, -1),
    (-4, 'Back Section', 'blue', 40, -1),
    (-5, 'Standing Area', 'gray', 20, -1),
    (-6, 'Balcony', 'purple', 70, -1),
    (-7, 'Family Section', 'orange', 50, -1),
    (-8, 'Disabled Section', 'pink', 30, -1);

INSERT INTO seat (id, status, type, capacity, order_nr, seat_nr, seatrow_id, section_id, bought_nr, reserved_nr)
VALUES
    (-1, 'FREE', 'SEAT', 1, 1, 1, -1, -1, 0, 0),
    (-2, 'FREE', 'SEAT', 1, 2, 2, -1, -1, 0, 0),
    (-3, 'FREE', 'SEAT', 1, 3, 3, -1, -2, 0, 0),
    (-4, 'OCCUPIED', 'SEAT', 1, 4, 4, -1, -2, 0, 0),
    (-5, 'FREE', 'VACANT_SEAT', 1, -1, 1, -2, -3, 0, 0),
    (-6, 'FREE', 'STANDING_SEAT', 1, 1, 1, -3, -4, 0, 0),
    (-7, 'RESERVED', 'SEAT', 2, 2, 2, -1, -1, 0, 0),
    (-8, 'FREE', 'SEAT', 1, 1, 3, -2, -3, 0, 0);


DELETE FROM Event_Date;
DELETE FROM EVENT;
INSERT INTO Event (id, title, duration, category, description, image, artist)
VALUES (1, 'Example Event', '02:00', 'Example Category', 'This is an example event description.', 'base64-encoded-image', 'John');

INSERT INTO Event (id, title, duration, category, description, image, artist)
VALUES (2, 'Rock Night', '03:30', 'Rock', 'Join us for a night of electrifying rock music!', 'base64-encoded-image', 'The Rockers');

INSERT INTO Event_Date (id, date, starting_time, area_code, address, city, hallplan_id, event_id)
VALUES (1, '2023-05-21', '02:15', 12345, '123 Example Street', 'Example City', -2, 1);

INSERT INTO Event_Date (id, date, starting_time, area_code, address, city, hallplan_id, event_id)
VALUES (2, '2023-05-22', '02:15', 54321, '404 Example Street', 'Sampleville', -3, 1);

INSERT INTO Event_Date (id, date, starting_time, area_code, address, city, hallplan_id, event_id)
VALUES (3, '2023-05-21', '02:15', 12345, '123 Example Street', 'Example City', -1, 1);


