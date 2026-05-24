MERGE INTO sys_user (email, name, status, created_at, updated_at)
KEY (email)
VALUES ('test@selfsp.local', '测试用户', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
