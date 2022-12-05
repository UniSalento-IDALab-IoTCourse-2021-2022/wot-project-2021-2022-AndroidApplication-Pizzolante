# SMART GRID - WorkSafe Android Application

## Introduzione

L'app WorkSafe è un applicazione Android nata con lo scopo di monitorare dei 
pericoli in movimento all'interno di un cantiere di lavoro. Per "pericoli in movimento" si 
fa riferimento a delle macchine operatrici che svolgono una particolare mansione 
nel sito e sulle quali è stato installato un beacon BLE che consente loro di essere 
rilevate. Ogni volta che un lavoratore supera la distanza di sicurezza prevista tra 
esso e il macchinario, l'app invia una notifica di allarme. Il funzionamento dell'app 
è basato sul Bluetooth Low Energy e sfrutta il protocollo MQTT per la gestione delle 
notifiche di pericolo.

## Installazione
Per poter utilizzare l'app correttamente è necessario prima andare a configurare
il server REST per l'accesso ai servizi. Per questo fare riferimento al seguente repository:

* https://github.com/UniSalento-IDALab-IoTCourse-2021-2022/wot-project-2021-2022-ServerNodeJS-Pizzolante

Una volta configurato il server, dopo aver installato l'app su un dispositivo Android,
è necessario connettere il dispositivo ad una rete comune.

*NOTA BENE*: Prima di installare l'app su qualsiasi dispositivo, è necessario specificare
l'indirizzo del server al quale ci si vuole connettere. Per far questo, una volta
ottenuto l'indirizzo della macchina server, recarsi nella classe "HttpController" e
cambiare la stringa:
* String BASE_URL = "http://192.168.x.y:3000"

## Funzionamento

Nella sua schermata Home l'app presenta un menu quale:

* Calibrazione
* Worker
* Machinist

Le cui voci devono essere sfruttate sulla base del ruolo che si vuole assumere nello scenario.

### Worker
Se si vuole utilizzare l'app come un worker, allora gli step per poter utilizzare l'app sono:
1. Andare su Calibrazione --> Ottieni Parametri --> Ok.
2. Attivare Bluetooth e GPS.
3. Dalla Home, cliccare su Worker --> Inserisci ID --> Inizia Scansione.
4. Mantenere l'app in Background per la ricezione delle notifiche.

In particolare il punto 1. consente di scaricare la i parametri di configurazione, quali 
la distanza di sicurezza e i valori di RSSI memorizzati nel databse...ecc.

Il punto 3. consente invece di avviare la scansione in real-time dei macchinari in movimento
nel cantiere.

### Machinist
Se si vuole utilizzare l'app come un machinist, allora è necessario eseguire i seguenti 
passaggi:

1. Dalla schermata Home, scegliere Machinist.
2. Inserire il proprio ID e scegliere l'ID del beacon presente sul proprio macchinario.
3. Inizia.

A questo punto mantenendo l'app in background, arriverà una notifica ogni volta che un
worker si avvicinerà troppo al tuo macchinario.
