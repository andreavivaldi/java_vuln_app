# VulnApp — Sysdig Pipeline Scanning Demo

> ⚠️ **This application is intentionally vulnerable. Never deploy to production.**

A deliberately insecure Spring Boot application designed to demonstrate how **Sysdig Secure pipeline scanning** catches vulnerabilities before images reach production.

## Vulnerabilities included

| Vulnerability | CVE | Location |
|---|---|---|
| Log4Shell | CVE-2021-44228 | log4j-core 2.14.1 dep + `/search`, `/greet`, `/ping` endpoints |
| Text4Shell | CVE-2022-42889 | commons-text 1.9 dep + `/greet` endpoint |
| Spring4Shell | CVE-2022-22965 | Spring Boot 2.6.0 / Spring Framework 5.3.x |
| Jackson deserialization | CVE-2022-42003/4 | jackson-databind 2.13.0 |
| SnakeYAML RCE | CVE-2022-1471 | snakeyaml 1.30 |
| Netty HTTP smuggling | CVE-2021-43797 | netty-all 4.1.70 |
| SQL Injection | CWE-89 | `/search` endpoint |
| Command Injection | CWE-78 | `/ping` endpoint |
| XSS | CWE-79 | `/search` response |
| SSRF | CWE-918 | `/fetch` endpoint |
| H2 Console exposed | — | `/h2-console` no auth |
| Runs as root | — | Dockerfile |

## Running locally

```bash
mvn package -DskipTests
docker build -t vulnapp .
docker run -p 8080:8080 vulnapp
# Open http://localhost:8080
```

## GitHub Actions — Sysdig Pipeline Scan

The workflow in [`.github/workflows/sysdig-scan.yml`](.github/workflows/sysdig-scan.yml):

1. Builds the Maven artifact
2. Builds the Docker image
3. Runs **Sysdig `scan-action`** — fails the pipeline if the policy is violated
4. Uploads SARIF results to the GitHub Security tab
5. Pushes the image to GHCR **only if the scan passes**

### Required secrets

| Secret | Value |
|---|---|
| `SYSDIG_SECURE_TOKEN` | Sysdig Secure API token (Settings → API Tokens) |
| `SYSDIG_SECURE_URL` | *(optional)* Region endpoint, e.g. `https://eu1.app.sysdig.com` |

Set these under **Repository → Settings → Secrets and variables → Actions**.
