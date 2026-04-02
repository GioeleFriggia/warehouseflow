Questa cartella contiene file NUOVI o MODIFICATI per aggiungere:

- export Excel/PDF
- audit log forte
- filtri per data/fornitore/categoria
- avvisi automatici sotto scorta
- storico inventari

COME USARLA
1) copia i file nelle stesse cartelle del progetto warehouseflow
2) sovrascrivi i file esistenti quando richiesto
3) nel backend esegui:
   mvn clean spring-boot:run
4) nel frontend esegui:
   npm install
   npm run dev

NOTE
- Il backend usa nuove dipendenze Maven (Apache POI + OpenPDF)
- Ho lasciato il progetto in Java/Spring Boot + JavaScript/React
- Questa patch è stata preparata sulla struttura della repo che hai condiviso.
