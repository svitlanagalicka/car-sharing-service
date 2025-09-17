DELETE FROM rentals;

INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted)
VALUES ( 1, '2025-09-05', '2025-09-10', NULL, 1, 1, 0);