-- Flyway migration script for initial RAG application schema
-- Version: V1
-- Description: Create initial database schema for documents and document sections

-- Create documents table
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    processing_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processing_started_at TIMESTAMP,
    processing_completed_at TIMESTAMP,
    error_message TEXT,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create document_sections table
CREATE TABLE document_sections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id UUID NOT NULL,
    section_type VARCHAR(20) NOT NULL,
    title VARCHAR(500),
    content TEXT NOT NULL,
    hierarchy_path VARCHAR(100),
    hierarchy_level INTEGER NOT NULL,
    page_start INTEGER,
    page_end INTEGER,
    word_count BIGINT,
    char_count BIGINT,
    section_order INTEGER NOT NULL,
    parent_section_id UUID,
    weaviate_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Foreign key constraints
    CONSTRAINT fk_document_sections_document 
        FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    CONSTRAINT fk_document_sections_parent 
        FOREIGN KEY (parent_section_id) REFERENCES document_sections(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_documents_processing_status ON documents(processing_status);
CREATE INDEX idx_documents_uploaded_at ON documents(uploaded_at);
CREATE INDEX idx_documents_filename ON documents(filename);
CREATE INDEX idx_documents_created_at ON documents(created_at);

CREATE INDEX idx_document_sections_document_id ON document_sections(document_id);
CREATE INDEX idx_document_sections_section_type ON document_sections(section_type);
CREATE INDEX idx_document_sections_hierarchy_path ON document_sections(hierarchy_path);
CREATE INDEX idx_document_sections_hierarchy_level ON document_sections(hierarchy_level);
CREATE INDEX idx_document_sections_parent_section_id ON document_sections(parent_section_id);
CREATE INDEX idx_document_sections_section_order ON document_sections(section_order);
CREATE INDEX idx_document_sections_weaviate_id ON document_sections(weaviate_id);
CREATE INDEX idx_document_sections_created_at ON document_sections(created_at);

-- Create composite indexes for common query patterns
CREATE INDEX idx_documents_status_uploaded_at ON documents(processing_status, uploaded_at);
CREATE INDEX idx_document_sections_document_order ON document_sections(document_id, section_order);
CREATE INDEX idx_document_sections_document_type ON document_sections(document_id, section_type);

-- Create full-text search indexes for content search
CREATE INDEX idx_document_sections_content_gin ON document_sections USING gin(to_tsvector('english', content));
CREATE INDEX idx_document_sections_title_gin ON document_sections USING gin(to_tsvector('english', title));

-- Create JSONB indexes for metadata queries
CREATE INDEX idx_documents_metadata_gin ON documents USING gin(metadata);

-- Add comments for documentation
COMMENT ON TABLE documents IS 'Stores document metadata and processing information';
COMMENT ON TABLE document_sections IS 'Stores document sections with hierarchical structure';

COMMENT ON COLUMN documents.id IS 'Unique identifier for the document';
COMMENT ON COLUMN documents.filename IS 'System-generated filename for storage';
COMMENT ON COLUMN documents.original_filename IS 'Original filename as uploaded by user';
COMMENT ON COLUMN documents.file_size IS 'Size of the file in bytes';
COMMENT ON COLUMN documents.mime_type IS 'MIME type of the document';
COMMENT ON COLUMN documents.processing_status IS 'Current processing status (PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED)';
COMMENT ON COLUMN documents.uploaded_at IS 'Timestamp when document was uploaded';
COMMENT ON COLUMN documents.processing_started_at IS 'Timestamp when processing started';
COMMENT ON COLUMN documents.processing_completed_at IS 'Timestamp when processing completed';
COMMENT ON COLUMN documents.error_message IS 'Error message if processing failed';
COMMENT ON COLUMN documents.metadata IS 'Additional metadata stored as JSON';
COMMENT ON COLUMN documents.created_at IS 'Timestamp when record was created';
COMMENT ON COLUMN documents.updated_at IS 'Timestamp when record was last updated';
COMMENT ON COLUMN documents.version IS 'Version for optimistic locking';

COMMENT ON COLUMN document_sections.id IS 'Unique identifier for the section';
COMMENT ON COLUMN document_sections.document_id IS 'Reference to parent document';
COMMENT ON COLUMN document_sections.section_type IS 'Type of section (CHAPTER, SUB_CHAPTER, SECTION, etc.)';
COMMENT ON COLUMN document_sections.title IS 'Title or heading of the section';
COMMENT ON COLUMN document_sections.content IS 'Content of the section';
COMMENT ON COLUMN document_sections.hierarchy_path IS 'Hierarchical path (e.g., "1.2.3")';
COMMENT ON COLUMN document_sections.hierarchy_level IS 'Numeric level in hierarchy';
COMMENT ON COLUMN document_sections.page_start IS 'Starting page number';
COMMENT ON COLUMN document_sections.page_end IS 'Ending page number';
COMMENT ON COLUMN document_sections.word_count IS 'Word count of content';
COMMENT ON COLUMN document_sections.char_count IS 'Character count of content';
COMMENT ON COLUMN document_sections.section_order IS 'Order within parent section';
COMMENT ON COLUMN document_sections.parent_section_id IS 'Reference to parent section for tree structure';
COMMENT ON COLUMN document_sections.weaviate_id IS 'Weaviate vector database identifier';
COMMENT ON COLUMN document_sections.created_at IS 'Timestamp when record was created';
COMMENT ON COLUMN document_sections.updated_at IS 'Timestamp when record was last updated';
COMMENT ON COLUMN document_sections.version IS 'Version for optimistic locking';
