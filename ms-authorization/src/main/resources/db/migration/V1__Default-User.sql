
CREATE TABLE IF NOT EXISTS tb_users (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  login VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_tb_users_login
ON tb_users(login);

--{
--  "id": "d78258f3-ff6a-4720-8535-0dae096eb109",
--  "login": "felipe",
--  "password": "$2a$10$KgBD.NMo29sVgcSLuzp54erMNO48L35aCF7uH2giJwEKUkzqbNjJO",
--  "role": "ADMIN"
--}
-- password: 123
INSERT INTO tb_users (id, login, password, role)
VALUES (gen_random_uuid(), 'felipe', '$2a$10$KgBD.NMo29sVgcSLuzp54erMNO48L35aCF7uH2giJwEKUkzqbNjJO', 'ADMIN');