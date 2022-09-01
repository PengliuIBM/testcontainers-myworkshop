CREATE TABLE IF NOT EXISTS talks(
    id    VARCHAR(64)  NOT NULL,
    title VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
    );

INSERT
INTO talks (id, title)
VALUES ('testcontainers-integration-testing', 'Modern Integration Testing with Testcontainers') 
    ---below line is postgresql synx, it will be failed while h2db(comment it will work).
    ON CONFLICT do nothing;

INSERT
INTO talks (id, title)
VALUES ('flight-of-the-flux', 'A look at Reactor execution model') 
    ---below line is postgresql synx, it will be failed while h2db(comment it will work).
    ON CONFLICT do nothing;
