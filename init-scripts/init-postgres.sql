-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Create documents table
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    upload_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    processing_status VARCHAR(50) DEFAULT 'PENDING',
    processing_started_at TIMESTAMP WITH TIME ZONE,
    processing_completed_at TIMESTAMP WITH TIME ZONE,
    error_message TEXT,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create document_sections table  
CREATE TABLE document_sections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    section_type VARCHAR(50) NOT NULL,
    title VARCHAR(500),
    content TEXT NOT NULL,
    hierarchy_path VARCHAR(255),
    hierarchy_level INTEGER NOT NULL,
    page_start INTEGER,
    page_end INTEGER,
    word_count INTEGER,
    char_count INTEGER,
    section_order INTEGER NOT NULL,
    parent_section_id UUID REFERENCES document_sections(id),
    weaviate_id VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create tags table
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create section_tags junction table
CREATE TABLE section_tags (
    section_id UUID REFERENCES document_sections(id) ON DELETE CASCADE,
    tag_id UUID REFERENCES tags(id) ON DELETE CASCADE,
    confidence DECIMAL(5,4),
    PRIMARY KEY (section_id, tag_id)
);

-- Create indexes for performance
CREATE INDEX idx_documents_status ON documents(processing_status);
CREATE INDEX idx_documents_upload_date ON documents(upload_date);
CREATE INDEX idx_sections_document_id ON document_sections(document_id);
CREATE INDEX idx_sections_hierarchy ON document_sections(hierarchy_path);
CREATE INDEX idx_sections_type ON document_sections(section_type);
CREATE INDEX idx_sections_parent ON document_sections(parent_section_id);
CREATE INDEX idx_sections_weaviate ON document_sections(weaviate_id);
CREATE INDEX idx_sections_content_gin ON document_sections USING gin(content gin_trgm_ops);
CREATE INDEX idx_tags_name ON tags(name);

-- Create triggers for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_documents_updated_at 
    BEFORE UPDATE ON documents 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

COMMIT;