IMAGE_NAME = rb-graphql-template
DOCKER_ORG = docker-org-here
BUILDKITE_BRANCH ?= $(shell git rev-parse --abbrev-ref HEAD) # current branch name
BUILDKITE_BUILD_NUMBER ?= 0

BRANCH_TAG != echo ${BUILDKITE_BRANCH} | sed 's:/:-:g'

.PHONY: help build tag push test run all clean

help: ## Display this help page
	@echo "Welcome to the GraphQL Template API!"
	@printf "\n\033[32mEnvironment Variables\033[0m\n"
	@cat $(MAKEFILE_LIST) | egrep -o "\\$$\{[a-zA-Z0-9_]*\}" | sort | uniq | \
		sed -E 's/^[\$$\{]*|\}$$//g' | xargs -I % echo % % | \
		xargs printf "echo \"\033[93m%-30s\033[0m \$$(printenv %s)\n\"" | bash | sed "s/echo //"
	@printf "\n\033[32mMake Targets\033[0m\n"
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
	@echo ""

build: ## Build the ${DOCKER_ORG}/rb-graphql-template Docker image
	@echo "--- Pulling build image (contains dependency cache)"
	docker pull ${DOCKER_ORG}/${IMAGE_NAME}-build:latest
	@echo "--- Compiling the application"
	docker run --name=${IMAGE_NAME}-compile -v `pwd`/app:/app ${DOCKER_ORG}/${IMAGE_NAME}-build:latest sbt stage
	@echo "--- Building the runtime image"
	docker build --pull -t ${DOCKER_ORG}/${IMAGE_NAME}:latest .
	@echo "--- Saving the compile container for future caching benefits"
	# These steps are just an optimisation, so we don't really care if they fail.
	-docker commit ${IMAGE_NAME}-compile ${DOCKER_ORG}/${IMAGE_NAME}-build:latest
	-docker push ${DOCKER_ORG}/${IMAGE_NAME}-build:latest
	-docker rm ${IMAGE_NAME}-compile

tag: build ## Tag the ${DOCKER_ORG}/rb-graphql-template Docker image
	@echo "--- Tagging image"
	docker tag ${DOCKER_ORG}/${IMAGE_NAME}:latest ${DOCKER_ORG}/${IMAGE_NAME}:${BRANCH_TAG}
	docker tag ${DOCKER_ORG}/${IMAGE_NAME}:latest ${DOCKER_ORG}/${IMAGE_NAME}:${BUILDKITE_BUILD_NUMBER}

push: tag ## Push the ${DOCKER_ORG}/rb-graphql-template tags to Docker Hub
	@echo "--- Pushing to Docker registry"
	docker push ${DOCKER_ORG}/${IMAGE_NAME}:${BUILDKITE_BUILD_NUMBER}
	docker push ${DOCKER_ORG}/${IMAGE_NAME}:${BRANCH_TAG}
	docker push ${DOCKER_ORG}/${IMAGE_NAME}:latest

test: ## Run the test suite
	@echo "--- Pulling build image (contains dependency cache)"
	docker pull ${DOCKER_ORG}/${IMAGE_NAME}-build:latest
	@echo "--- Running unit tests"
	docker run --rm --env-file=app/etc/test.env -v `pwd`/app:/app ${DOCKER_ORG}/${IMAGE_NAME}-build:latest sbt ci coverageAggregate

run: ## Run the current branch locally
	@echo "Pulling build image (contains dependency cache)"
	docker pull ${DOCKER_ORG}/${IMAGE_NAME}-build:latest
	@echo "Starting the API on port 8080"
	docker run -p 8080:80 --env-file=app/etc/development.env -v `pwd`/app:/app ${DOCKER_ORG}/${IMAGE_NAME}:master sbt run

clean: ## Clean the artifacts, Docker images and containers created by the build and test commands
	@echo "--- Cleaning up the file system"
	-docker run --rm -v `pwd`/app:/app ${DOCKER_ORG}/rb-graphql-template:latest chmod -R a+w /app
	git clean -dfq
	@echo "--- Cleaning up Docker images"
	-docker rm $(docker ps -aq) 2> /dev/null
	-docker rmi ${DOCKER_ORG}/${IMAGE_NAME}:latest 2> /dev/null
	-docker rmi ${DOCKER_ORG}/${IMAGE_NAME}:${BRANCH_TAG} 2> /dev/null
	-docker rmi ${DOCKER_ORG}/${IMAGE_NAME}:${BUILDKITE_BUILD_NUMBER} 2> /dev/null
	-docker rmi $(docker images -f "dangling=true" -q) 2> /dev/null

all: build tag push
