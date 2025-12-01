CREATE TABLE tb_user (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email     VARCHAR(200) NOT NULL,
    password  VARCHAR(129) NOT NULL,
    name      VARCHAR(120),
    role      VARCHAR(20) NOT NULL
);

CREATE TABLE tb_user_external_project (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    name         VARCHAR(120) NOT NULL,

    CONSTRAINT fk_tb_user_external_project_user
        FOREIGN KEY (user_id) REFERENCES tb_user (id)
);

INSERT INTO tb_user (email, password, name, role)
VALUES
    ('alice@example.com', '123456', 'Alice Johnson', 'USER'),
    ('bob@example.com', '123456', 'Bob Smith', 'USER'),
    ('carol@example.com', '123456', 'Carol White', 'USER'),
    ('david@example.com', '123456', 'David Brown', 'USER'),
    ('eve@example.com', '123456', 'Eve Adams', 'USER');

INSERT INTO tb_user_external_project (user_id, name)
VALUES
    (1, 'GitHub Repo Alpha'),
    (1, 'Jira Board Payments'),
    (2, 'GitHub Repo Beta'),
    (2, 'GitLab Project Core'),
    (3, 'Asana Task Manager'),
    (4, 'Trello Kanban Flow'),
    (5, 'GitHub Security Scanner');