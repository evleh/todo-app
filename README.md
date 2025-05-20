# Todo-App


- Notizen von 20.5. vervollständigen 

## Docker
### Wieso: 
Wir verwenden Docker um unsere postgres Datenbank einzubinden. Dazu verwenden wir ein vorkonfiguriertes 
Docker image auf dem die DB gut läuft. 

Andere verwendung von Docker wäre darin zu Entwickeln. Dadurch kann ich die Applikation von dem eigenen System unabhängig machen. 
Diese Unabhängigkeit erleichtert gemeinsames Entwickeln und garantiert mir, dass die Anwendung auf jedem System läuft. 

### Docker: Wie verwenden 
1. Installation: https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository -> Install using the apt repository
2. Starten von Docker (Docker muss immer laufen, wenn cih die Applikation verwende)
   - sudo docker run -p "5432:5432" -e POSTGRES_USER=eww -e POSTGRES_PASSWORD=eww -e POSTGRES_DB=todoapp postgres
   - docker-compose.yml in cmd starten mit docker compose up (yml ist wie json nur ohne klammern (?); scope wird nicht durch Klammern sondern durch Formatierung definiert (wie bei python))
   - Erklärungen:`-p "5432:5432"`: Port-forwarding vom local host port 5432 auf docker port 5432. 
3. Troubleshooting:
   - Postgres Stoppen: sudo systemctl stop postgresql
   - Prüfen ob port frei ist: sudo lsof -i :5432


### Andere Möglichkeiten Datenbanken einzubinden: 
1. In-Memory: DB ist nicht persistent, nur zu Test zwecken 
2. DB in eigenständiger Applikation laufen lassen 
3. XAMP 

sudo lsof -i :5432
