CREATE SCHEMA IF NOT EXISTS smart_backend;

CREATE TABLE IF NOT EXISTS smart_backend.chat (
    id UUID PRIMARY KEY,
    system_prompt text NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS smart_backend.message (
    id UUID PRIMARY KEY,
    chat_id UUID REFERENCES smart_backend.chat(id) NOT NULL,
    role varchar (36) NOT NULL,
    number integer NOT NULL,
    content TEXT NOT NULL,
    extracted_content TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT unique_chat_message UNIQUE(chat_id,number)
);

CREATE TABLE IF NOT EXISTS smart_backend.feature (
    id UUID PRIMARY KEY,
    name VARCHAR(200),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS smart_backend.api (
    id UUID PRIMARY KEY,
    feature_id UUID REFERENCES smart_backend.feature(id) NOT NULL,
    method VARCHAR(10) NOT NULL,
    path VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS smart_backend.api_parameter (
    id UUID PRIMARY KEY,
    api_id UUID REFERENCES smart_backend.api(id) NOT NULL,
    name VARCHAR(50) NOT NUll,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS smart_backend.api_result (
    id UUID PRIMARY KEY,
    api_id UUID REFERENCES smart_backend.api(id) NOT NULL,
    name VARCHAR(50) NOT NUll,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS smart_backend.ddl_query (
    id UUID PRIMARY KEY,
    feature_id UUID REFERENCES smart_backend.feature(id) NOT NULL,
    query TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS smart_backend.dml_query (
    id UUID PRIMARY KEY,
    api_id UUID REFERENCES smart_backend.api(id) NOT NULL,
    query TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT dml_unique_api Unique(api_Id)
);