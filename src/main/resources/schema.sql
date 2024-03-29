CREATE TABLE users (id SERIAL PRIMARY KEY, username VARCHAR(32), email VARCHAR(255), password VARCHAR(255), role VARCHAR(255), reset_password_token VARCHAR(255));

CREATE TABLE portfolio_items (id SERIAL PRIMARY KEY, name VARCHAR(255), description VARCHAR(255), image_src VARCHAR(255), uploaded_date TIMESTAMP, disabled BOOLEAN);