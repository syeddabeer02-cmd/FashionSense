# Hosted Fashion Sense demonstration

Use a Linux Docker host and a public hostname. Images use Java26 and Node24;
verify availability and ARM support on the chosen server before deployment.
Confirm free-tier eligibility and limits in the provider account. These files
create no cloud resources and do not guarantee free hosting.

From infrastructure/, create .env with POSTGRES_DB, POSTGRES_USER,
POSTGRES_PASSWORD, JWT_SECRET, FASHION_DOMAIN and ACME_EMAIL. Generate at least
32 random bytes, Base64-encode them for JWT_SECRET, and use unique passwords.

```bash
docker compose -f compose.yaml -f compose.hosted.yaml config -q
docker compose -f compose.yaml -f compose.hosted.yaml up --build -d --wait
docker compose -f compose.yaml -f compose.hosted.yaml ps
```

Requires Docker Compose >=2.24.4 for the hosted port override. Point DNS at the
server and allow public TCP80/443 for HTTPS. PostgreSQL, Redis, Kafka, backend and
frontend host bindings remain localhost. The proxy alone serves public requests.
The frontend calls backend:8080 over the container network. The hosted backend
uses localhost:18081 to avoid the retail Airflow port8080. Persistent volumes
retain PostgreSQL, Kafka log and certificate data. Redis is an ephemeral cache.
Back up data and test recovery before relying on this deployment. Never use
`down -v` against data you want to preserve.

After deployment, check public catalog browsing, registration, email verification,
login, cart, a simulated checkout and order history. The current verification
sender logs the link; an operator must retrieve it from backend logs for demo
accounts until email delivery is configured. Do not publish logs or tokens.
Payments are simulated, not connected to a provider or real banking rails.
Do not collect real card details or accept real payments in this demonstration.

## Hosting both projects on one server

Use one HTTPS proxy; two proxies cannot both bind TCP80/443. Choose one as the
server's edge proxy and attach it to both applications' Docker networks. Replace
its Caddyfile with two hostname routes: Fashion Sense -> frontend:3000 and
retail -> dashboard:8501. Give upstream services unique network aliases when
sharing a network. The existing per-project overlays are standalone templates;
a combined server deployment needs the chosen domains/network names to be
configured and validated. Do not run both standalone proxies simultaneously.

The retail application leaves Airflow private. Fashion Sense's backend remains
private as well; browser calls use the Next.js API proxy. Use SSH forwarding for
administrative APIs, and do not expose database/message-broker ports publicly.
