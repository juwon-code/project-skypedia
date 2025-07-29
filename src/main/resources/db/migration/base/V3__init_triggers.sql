-- 공지 메시지 만료 일자 설정 함수
-- 공지 메시지의 INSERT 발생 시 expired_at 컬럼을 created_at에 7일 더한 시간으로 변경합니다.
CREATE OR REPLACE FUNCTION set_expired_at()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.notify_type = 'NOTICE' THEN
        NEW.expired_at := NEW.created_at + INTERVAL '7 days';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 공지 메시지 만료 일자 설정 트리거 적용
-- notify_message 테이블에 set_expired_at() 함수를 적용합니다.
CREATE TRIGGER trg_notify_message_set_expired_at
BEFORE INSERT ON notify_message
FOR EACH ROW
EXECUTE FUNCTION set_expired_at();

-- 업데이트 일자 설정 함수
-- UPDATE 발생 시 자동으로 updated_at 컬럼을 현재 시간으로 변경합니다.
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 업데이트 일자 트리거 적용
-- updated_at 컬럼을 가진 모든 테이블에 트리거를 자동으로 적용합니다.
DO $$
DECLARE
    t TEXT;
BEGIN
    FOR t IN
        SELECT table_name FROM information_schema.columns WHERE column_name = 'updated_at'
    LOOP
        IF NOT EXISTS (
            SELECT 1 FROM pg_trigger
            WHERE tgname = format('trg_%s_set_update_at', t)
        ) THEN
            EXECUTE format(
                'CREATE TRIGGER trg_%I_set_update_at
                 BEFORE UPDATE ON %I
                 FOR EACH ROW
                 EXECUTE FUNCTION set_updated_at()',
                t, t
            );
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;