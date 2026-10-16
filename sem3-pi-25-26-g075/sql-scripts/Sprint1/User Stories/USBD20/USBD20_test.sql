INSERT INTO "Line" (line_id, owner_id, start_station_id, end_station_id, gauge_id, "name") VALUES (8, 'PT503933813', 7, 5, 2, 'Ramal São Bento - Barcelos');
INSERT INTO "Line" (line_id, owner_id, start_station_id, end_station_id, gauge_id, "name") VALUES (9, 'PT503933813', 5, 20, 2, 'Ramal Camapanhã - Viana');

INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order") VALUES (2, 8, 'Yes', 8000, 2618, 4, 1);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order") VALUES (3, 8, 'Yes', 8000, 29003, 2, 2);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order") VALUES (4, 8, 'Yes', 8000, 10000, 2, 3);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order") VALUES (5, 8, 'Yes', 8000, 5286, 2, 4);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order") VALUES (6, 9, 'Yes', 8000, 6000, 2, 1);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order") VALUES (7, 9, 'Yes', 8000, 10387, 2, 2);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order") VALUES (8, 9, 'Yes', 8000, 12000, 2, 3);