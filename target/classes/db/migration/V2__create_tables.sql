-- Album Table
CREATE TABLE IF NOT EXISTS albums (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Photo Table
CREATE TABLE IF NOT EXISTS photos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    file_path VARCHAR(255),
    description TEXT,
    album_id BIGINT,
    FOREIGN KEY (album_id) REFERENCES albums(id)
); 