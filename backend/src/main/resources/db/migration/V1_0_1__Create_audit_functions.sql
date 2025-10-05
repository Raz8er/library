CREATE OR REPLACE FUNCTION generic_audit_function()
    RETURNS TRIGGER AS
$$
DECLARE
    app_user    TEXT;
    audit_table TEXT;
BEGIN
    app_user := COALESCE(current_setting('my.audit_user', true), 'postgres');
    audit_table := format('%I.%I_audit', TG_TABLE_SCHEMA, TG_TABLE_NAME);

    IF TG_OP = 'INSERT' THEN
        EXECUTE format(
                'INSERT INTO %s (operation, changed_by, new_data) VALUES ($1, $2, $3)',
                audit_table
                ) USING TG_OP, app_user, to_jsonb(NEW);
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        EXECUTE format(
                'INSERT INTO %s (operation, changed_by, old_data, new_data) VALUES ($1, $2, $3, $4)',
                audit_table
                ) USING TG_OP, app_user, to_jsonb(OLD), to_jsonb(NEW);
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        EXECUTE format(
                'INSERT INTO %s (operation, changed_by, old_data) VALUES ($1, $2, $3)',
                audit_table
                ) USING TG_OP, app_user, to_jsonb(OLD);
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_audit_for_table(target_schema text, target_table text)
    RETURNS void AS
$$
BEGIN
    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I.%I_audit (
            audit_id BIGSERIAL PRIMARY KEY,
            operation TEXT,
            changed_by TEXT,
            changed_at TIMESTAMPTZ DEFAULT now(),
            old_data JSONB,
            new_data JSONB
        )
    ', target_schema, target_table);

    EXECUTE format('
        DROP TRIGGER IF EXISTS trg_audit_%I ON %I.%I;
        CREATE TRIGGER trg_audit_%I
        AFTER INSERT OR UPDATE OR DELETE ON %I.%I
        FOR EACH ROW EXECUTE FUNCTION generic_audit_function();
    ', target_table, target_schema, target_table, target_table, target_schema, target_table);
END;
$$ LANGUAGE plpgsql;
