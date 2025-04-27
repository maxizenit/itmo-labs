CREATE INDEX idx_sessions_user_id ON sessions (user_id);
CREATE INDEX idx_sessions_user_id_login_time ON sessions (user_id, login_time);