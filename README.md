# distroless-jvm-examples

## Getting Started

### Prerequisites

- Docker
- mise
  - Not required if you install Java manually.
- Java
  - 25
  - You can install by mise.

### Installation

#### Install Java

```bash
mise install
```

If you don't use mise, you need to install Java manually.

#### Build application

```bash
gradle clean bootBuildImage
```

If you don't use mise, you need to use gradlew.

#### Run application

```bash
docker compose up -d
```

#### Verify

Each container exposes a health check endpoint.

| Service                         | URL                                   |
|---------------------------------|---------------------------------------|
| corretto                        | http://localhost:8081/actuator/health |
| distroless                      | http://localhost:8082/actuator/health |
| distroless-custom-jre           | http://localhost:8083/actuator/health |
| distroless-custom-jre-buildpack | http://localhost:8084/actuator/health |
