# RAG Application

A comprehensive Retrieval-Augmented Generation (RAG) application built with Spring Boot, designed for enterprise document processing and management.

## 🏗️ Architecture Overview

This application follows **Clean Architecture** principles and **SOLID** design patterns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Controllers   │  │  Global Exception│  │   DTOs      │ │
│  │                 │  │     Handler      │  │             │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Business Logic Layer                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │    Services     │  │    Mappers      │  │   Utils     │ │
│  │   (Interface)   │  │   (MapStruct)   │  │             │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Data Access Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Repositories  │  │     Models      │  │  Exceptions │ │
│  │  (Spring Data)  │  │   (JPA Entities)│  │             │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   PostgreSQL    │  │   Flyway        │  │   Docker    │ │
│  │   Database      │  │   Migrations    │  │   Compose   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Features

### Core Functionality
- **Document Upload & Management**: Upload PDF, Word, and text documents
- **Document Processing Pipeline**: Track processing status and progress
- **Hierarchical Document Structure**: Organize documents into chapters, sections, and subsections
- **Search & Retrieval**: Advanced search capabilities with pagination
- **Processing Statistics**: Monitor system performance and usage

### Technical Features
- **RESTful API**: Comprehensive REST endpoints with OpenAPI documentation
- **Database Migration**: Automated schema management with Flyway
- **Exception Handling**: Centralized error handling with custom exceptions
- **Validation**: Input validation and file type checking
- **Logging**: Comprehensive logging with SLF4J
- **Monitoring**: Health checks and metrics with Spring Boot Actuator

## 🛠️ Technology Stack

### Backend
- **Java 21**: Latest LTS version with modern features
- **Spring Boot 3.2.1**: Enterprise-grade application framework
- **Spring Data JPA**: Data access layer with Hibernate
- **PostgreSQL**: Robust relational database
- **Flyway**: Database migration tool
- **MapStruct**: Type-safe object mapping
- **Lombok**: Boilerplate code reduction

### Development Tools
- **Maven**: Build automation and dependency management
- **Docker**: Containerization for development and deployment
- **Testcontainers**: Integration testing with real databases
- **JaCoCo**: Code coverage analysis
- **SpotBugs**: Static code analysis
- **Checkstyle**: Code style enforcement

### Documentation & Testing
- **OpenAPI/Swagger**: API documentation and testing interface
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework for testing

## 📁 Project Structure

```
rag-application/
├── src/
│   ├── main/
│   │   ├── java/com/mystudypartner/rag/
│   │   │   ├── RagApplication.java              # Main application class
│   │   │   ├── controller/                      # REST API controllers
│   │   │   │   └── DocumentController.java
│   │   │   ├── service/                         # Business logic layer
│   │   │   │   ├── DocumentService.java         # Service interface
│   │   │   │   └── impl/
│   │   │   │       └── DocumentServiceImpl.java # Service implementation
│   │   │   ├── repository/                      # Data access layer
│   │   │   │   ├── DocumentRepository.java
│   │   │   │   └── DocumentSectionRepository.java
│   │   │   ├── model/                           # JPA entities
│   │   │   │   ├── Document.java
│   │   │   │   ├── DocumentSection.java
│   │   │   │   ├── ProcessingStatus.java
│   │   │   │   └── SectionType.java
│   │   │   ├── dto/                             # Data Transfer Objects
│   │   │   │   ├── DocumentDto.java
│   │   │   │   ├── DocumentSectionDto.java
│   │   │   │   └── UploadResponse.java
│   │   │   ├── mapper/                          # Object mappers
│   │   │   │   ├── DocumentMapper.java
│   │   │   │   └── DocumentSectionMapper.java
│   │   │   ├── exception/                       # Custom exceptions
│   │   │   │   ├── DocumentNotFoundException.java
│   │   │   │   ├── DocumentProcessingException.java
│   │   │   │   ├── FileUploadException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── config/                          # Configuration classes
│   │   │   │   └── OpenApiConfig.java
│   │   │   └── utils/                           # Utility classes
│   │   │       └── FileUtils.java
│   │   └── resources/
│   │       ├── application.yml                  # Main configuration
│   │       ├── application-dev.yml              # Development profile
│   │       └── db/migration/                    # Database migrations
│   │           └── V1__Create_initial_schema.sql
│   └── test/                                     # Test classes
├── uploads/                                      # File upload directory
├── models/                                       # ML models directory
├── init-scripts/                                 # Database initialization
├── docker-compose.yml                           # Production Docker setup
├── docker-compose.dev.yml                       # Development Docker setup
├── Dockerfile.prod                              # Production Dockerfile
├── Dockerfile.dev                               # Development Dockerfile
├── pom.xml                                      # Maven configuration
└── README.md                                    # This file
```

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.8+
- Docker and Docker Compose
- PostgreSQL 15+ (if running locally)

### Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd rag-application
   ```

2. **Start the database**
   ```bash
   docker-compose -f docker-compose.dev.yml up -d postgres
   ```

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Actuator: http://localhost:8080/actuator

### Production Setup

1. **Build the Docker image**
   ```bash
   docker build -f Dockerfile.prod -t rag-application .
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

## 📚 API Documentation

### Core Endpoints

#### Document Management
- `POST /api/v1/documents/upload` - Upload a document
- `GET /api/v1/documents/{id}` - Get document by ID
- `GET /api/v1/documents` - Get all documents (paginated)
- `GET /api/v1/documents/status/{status}` - Get documents by processing status
- `GET /api/v1/documents/search` - Search documents by filename
- `GET /api/v1/documents/date-range` - Get documents by upload date range
- `PUT /api/v1/documents/{id}/status` - Update processing status
- `DELETE /api/v1/documents/{id}` - Delete document

#### Statistics & Monitoring
- `GET /api/v1/documents/statistics` - Get processing statistics
- `GET /actuator/health` - Application health check
- `GET /actuator/metrics` - Application metrics

### Example Usage

#### Upload a Document
```bash
curl -X POST "http://localhost:8080/api/v1/documents/upload" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@document.pdf"
```

#### Get Document by ID
```bash
curl -X GET "http://localhost:8080/api/v1/documents/{document-id}" \
  -H "Accept: application/json"
```

#### Search Documents
```bash
curl -X GET "http://localhost:8080/api/v1/documents/search?filenamePattern=report&page=0&size=10" \
  -H "Accept: application/json"
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `5432` |
| `DB_NAME` | Database name | `rag_db` |
| `DB_USERNAME` | Database username | `rag_user` |
| `DB_PASSWORD` | Database password | `rag_password` |
| `SERVER_PORT` | Application port | `8080` |
| `FILE_UPLOAD_MAX_SIZE` | Maximum file upload size | `100MB` |

### Application Properties

Key configuration files:
- `application.yml` - Main configuration
- `application-dev.yml` - Development profile
- `application-prod.yml` - Production profile (create as needed)

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run with coverage
mvn clean test jacoco:report

# Run integration tests
mvn verify
```

### Test Structure
- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test component interactions
- **Repository Tests**: Test data access layer with Testcontainers

## 📊 Monitoring & Health Checks

### Actuator Endpoints
- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/env` - Environment variables
- `/actuator/loggers` - Logger configuration

### Health Indicators
- Database connectivity
- Disk space availability
- Application status

## 🔒 Security Considerations

### Current Implementation
- Basic authentication for development
- File upload validation
- Input sanitization
- SQL injection prevention (JPA)

### Production Recommendations
- Implement proper authentication (OAuth2, JWT)
- Add rate limiting
- Enable HTTPS
- Implement audit logging
- Add input validation
- Configure CORS properly

## 🚀 Deployment

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d

# Or build and run manually
docker build -f Dockerfile.prod -t rag-application .
docker run -p 8080:8080 rag-application
```

### Kubernetes Deployment
```yaml
# Example Kubernetes deployment (create as needed)
apiVersion: apps/v1
kind: Deployment
metadata:
  name: rag-application
spec:
  replicas: 3
  selector:
    matchLabels:
      app: rag-application
  template:
    metadata:
      labels:
        app: rag-application
    spec:
      containers:
      - name: rag-application
        image: rag-application:latest
        ports:
        - containerPort: 8080
```

## 🤝 Contributing

### Development Workflow
1. Create a feature branch from `main`
2. Implement changes following clean architecture principles
3. Add comprehensive tests
4. Update documentation
5. Submit a pull request

### Code Quality
- Follow SOLID principles
- Maintain clean architecture layers
- Write comprehensive tests
- Use meaningful commit messages
- Follow Java coding conventions

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team at dev@mystudypartner.com
- Check the [Wiki](wiki-url) for additional documentation

## 🔄 Version History

- **v1.0.0** - Initial release with core document management functionality
  - Document upload and processing
  - Hierarchical document structure
  - REST API with OpenAPI documentation
  - Database migration with Flyway
  - Comprehensive exception handling
