-- Dominio funcional de Red de Visionarios (ver docs/ARCHITECTURE.md y designdownloaded/DESIGN_SPEC.md).

CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    profile VARCHAR(20) NOT NULL,
    profession VARCHAR(255),
    years_experience INTEGER,
    current_company VARCHAR(255),
    business_product VARCHAR(255),
    operating_time VARCHAR(100),
    company_name VARCHAR(255),
    employee_count INTEGER,
    years_with_company INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE events (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    starts_at TIMESTAMPTZ NOT NULL,
    place VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    target_profile VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE event_rsvps (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    confirmed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (event_id, user_id)
);

CREATE TABLE materials (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    meta VARCHAR(255) NOT NULL,
    drive_url VARCHAR(1000) NOT NULL,
    visible_profile VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE admin_invites (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
