
INSERT INTO users (username, email, password)
VALUES
('alice', 'alice@example.com', 'password123'),
('bob', 'bob@example.com', 'securepass'),
('charlie', 'charlie@example.com', 'charliepass');


INSERT INTO projects (name, description, owner_id)
VALUES
('Project Alpha', 'First project focused on backend development.', 1),
('Project Beta', 'Frontend-focused project for dashboard UI.', 2),
('Project Gamma', 'Full-stack project integrating Alpha and Beta.', 3);


INSERT INTO tasks (title, description, status, priority, due_date, project_id, assignee_id)
VALUES
('Setup Database', 'Create initial schema and tables using Flyway.', 'COMPLETED', 'HIGH', '2025-11-15', 1, 1),
('Implement REST API', 'Develop endpoints for Project entity.', 'IN_PROGRESS', 'MEDIUM', '2025-11-20', 1, 2),
('Design UI', 'Create mockups for the dashboard.', 'TODO', 'HIGH', '2025-11-25', 2, 3),
('Integrate Frontend and Backend', 'Connect UI with REST API.', 'TODO', 'HIGH', '2025-11-30', 3, 1),
('Write Tests', 'Add unit and integration tests.', 'TODO', 'MEDIUM', '2025-12-05', 3, 2);
