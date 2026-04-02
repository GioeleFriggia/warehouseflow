# WarehouseFlow

Web app completa per la gestione di un grande magazzino con:
- carico merce
- scarico merce
- giacenze aggiornate
- ordini fornitori
- ruoli utente
- dashboard manageriale

## Stack
- Backend: Java 17 + Spring Boot 3.3.x + PostgreSQL + Spring Security + JWT
- Frontend: JavaScript + React + Vite + Axios

Spring Boot 3.3 richiede almeno Java 17. citeturn600576search0
Vite usa `npm create vite@latest` e nelle versioni attuali richiede Node.js 20.19+ o 22.12+. citeturn600576search2

## Credenziali iniziali
- Email: `admin@warehouseflow.local`
- Password: `Admin123!`

## Struttura progetto
- `backend` = API Spring Boot
- `frontend` = app React + Vite
- `sql/init.sql` = script base DB

## 1. Database PostgreSQL
Crea il database:

```sql
CREATE DATABASE warehouseflow;
```

Poi controlla `backend/src/main/resources/application.yml` e cambia username/password PostgreSQL se necessario.

## 2. Avvio backend in VS Code
Apri il terminale nella cartella `backend` e lancia:

```bash
mvn clean spring-boot:run
```

Backend su:

```text
http://localhost:8080
```

Health check:

```text
http://localhost:8080/api/health
```

## 3. Avvio frontend in VS Code
Apri un secondo terminale nella cartella `frontend`.

Copia `.env.example` in `.env` e lascia:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Poi lancia:

```bash
npm install
npm run dev
```

Frontend su:

```text
http://localhost:5173
```

## Ruoli
- `ADMIN`
- `MANAGER`
- `WAREHOUSE`
- `STORE_OPERATOR`

## Funzioni incluse
### Backend
- login JWT
- utenti
- prodotti
- movimenti di magazzino
- ordini fornitore
- dashboard
- autorizzazioni per ruolo

### Frontend
- login
- dashboard
- gestione prodotti
- carico/scarico merce
- ordini
- utenti

## Endpoints principali
- `POST /api/auth/login`
- `GET /api/users/me`
- `GET /api/products`
- `POST /api/products`
- `GET /api/stock/movements`
- `POST /api/stock/movements`
- `GET /api/orders`
- `POST /api/orders`
- `PATCH /api/orders/{id}/status`
- `GET /api/dashboard`

## Nota importante
Il progetto è impostato per essere aperto direttamente in VS Code. Non ho potuto testare qui una connessione reale al tuo PostgreSQL locale, quindi se sul tuo PC username/password o porta del DB sono diversi devi solo aggiornare `application.yml`.
