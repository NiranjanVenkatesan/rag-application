.PHONY: help dev dev-build dev-logs dev-shell prod prod-build stop clean test models

help: ## Show available commands
	@echo "Available commands:"
	@echo "  dev         - Start development environment"
	@echo "  dev-build   - Build and start development environment"
	@echo "  dev-logs    - Show development logs"
	@echo "  dev-shell   - Open shell in development container"
	@echo "  prod        - Start production environment"
	@echo "  prod-build  - Build and start production environment"
	@echo "  stop        - Stop all services"
	@echo "  clean       - Clean all containers and volumes"
	@echo "  test        - Run tests"
	@echo "  models      - Download ML models"

dev: ## Start development environment
	@echo "🚀 Starting RAG development environment..."
	@if not exist .env copy .env.example .env
	docker-compose -f docker-compose.dev.yml up -d
	@echo "✅ Development environment started!"
	@echo "📝 Application: http://localhost:8080"
	@echo "🗄️  Database Admin: http://localhost:8082"
	@echo "🔍 Weaviate: http://localhost:8081"
	@echo "🔧 Debug port: 5005"

dev-build: ## Build and start development environment
	docker-compose -f docker-compose.dev.yml up -d --build

dev-logs: ## Show development logs
	docker-compose -f docker-compose.dev.yml logs -f rag-app

dev-shell: ## Open shell in development container
	docker-compose -f docker-compose.dev.yml exec rag-app bash

prod: ## Start production environment
	@echo "🚀 Starting RAG production environment..."
	@if not exist .env copy .env.example .env
	docker-compose up -d
	@echo "✅ Production environment started!"

prod-build: ## Build and start production environment
	docker-compose up -d --build

stop: ## Stop all services
	docker-compose down
	docker-compose -f docker-compose.dev.yml down

clean: ## Clean all containers and volumes
	docker-compose down -v --remove-orphans
	docker-compose -f docker-compose.dev.yml down -v --remove-orphans
	docker system prune -f

test: ## Run tests
	docker-compose -f docker-compose.dev.yml exec rag-app mvn test

models: ## Download ML models
	@echo "📥 Downloading ML models..."
	@if not exist models mkdir models
	powershell -Command "& { Write-Host 'Models directory created. Add model download scripts here.' }"

status: ## Show status of all services
	docker-compose ps
	docker-compose -f docker-compose.dev.yml ps