DELETE FROM users_roles;
DELETE FROM roles;
DELETE FROM users;

INSERT INTO roles (id, role)
VALUES (1, 'CUSTOMER'), (2, 'MANAGER');

INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES
  (1, 'john.doe@example.com', 'password', 'John', 'Doe', 0),
  (2, 'jane.doe@example.com', 'password', 'Jane', 'Doe', 0);

INSERT INTO users_roles (user_id, role_id)
VALUES (2, 1);