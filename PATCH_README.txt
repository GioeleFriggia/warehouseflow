WAREHOUSEFLOW PATCH 2
=====================

Questa patch aggiunge:
- inventario guidato
- ricevimento ordine automatico
- allegati documenti sugli ordini
- nuova pagina Inventory nel frontend
- aggiornamento pagina Orders

COME INSTALLARE
1. Copia il contenuto di questa cartella dentro la root del progetto warehouseflow.
2. Sovrascrivi i file esistenti.
3. Avvia backend:
   cd backend
   mvn clean spring-boot:run
4. Avvia frontend:
   cd frontend
   npm install
   npm run dev

NOTE
- Gli allegati ordine vengono salvati nel database.
- Il download allegati viene fatto dal frontend tramite axios + blob.
- La chiusura inventario può creare automaticamente movimenti ADJUSTMENT.
- Il ricevimento ordine crea automaticamente movimenti INBOUND per le quantità ricevute.
