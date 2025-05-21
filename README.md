# Todo-App

## Fragen: 
- Ich verstehe nicht in welche Layer Entity-Objekte gehören. Sie sind die Daten mit denen ich arbeite und die
  in der Data Layer gespeichert werden aber sie sind in der Domain sprache was für Business Layer sprechen würde.


## Was haben wir wann gemacht: 
- UE1 (30.4.25): Client-Server Architektur, HTTP-Request + get, post, put, delete Methoden
- UE2 (7.5.25): 3-Layer Backend Architektur, Anfang mit Spring Boot, REST, JSON, Controller Klasse einrichten 
  (Annotationen, Service-Klasse noch nicht eingerichtet), CRUD
- UE3 (20.5.25): Alles ab Implementierung der CRUD-Methoden

### HTTP (Erklärung von ChatGPT )
- __HTTP__ steht für __HyperText Transfer Protocol__. Protokoll das zur Datenübertragung zwischen Client-Server verwendet wird. 
- Der Client sendet eine Anfrage (Request), der Server gibt eine Antwort (Response) zurück.

| Begriff          | Erklärung                                                             |
| ---------------- | --------------------------------------------------------------------- |
| **Request**      | Eine Anfrage vom Client an den Server                                 |
| **Response**     | Eine Antwort vom Server auf die Anfrage                               |
| **HTTP-Methode** | Gibt an, **was** der Client tun will (z. B. Daten holen, senden etc.) |
| **Statuscode**   | Antwort-Code vom Server (z. B. 200 = OK, 404 = nicht gefunden)        |
| **Header**       | Metadaten (z. B. Inhaltstyp, Sprache, Authentifizierung etc.)         |
| **Body**         | Der Inhalt (z. B. JSON-Daten), der übertragen wird                    |

| Methode  | Bedeutung                  | 
| -------- | -------------------------- | 
| `GET`    | Daten vom Server **holen** |
| `POST`   | Neue Daten **erstellen**   | 
| `PUT`    | Daten **aktualisieren**    | 
| `DELETE` | Daten **löschen**          |


## 3-Layer Backend Architektur
1. Presentation Layer: mit Controller der Verantwortlich dafür ist, dass Http-Request an korrekte Funktion in der Logik weiter geleitet wird
2. Business Logic Layer: Hier befindet sich Anwendungskern, inkludiert Service Klassen, 
    Hier spricht man Domain Sprache e.g. wenn ich eine Software zur LV-Verwaltung mache, sind hier Schüler:innen objekte 
    und alle zugehörigen Services. 
3. Data Access layer: Für Interaktion mit DB, abfragen und speichern von daten 

ACHTUNG: Ich verstehe nicht in welche Layer Entity-Objekte gehören. Sie sind die Daten mit denen ich arbeite und die 
in der Data Layer gespeichert werden aber sie sind in der Domain sprache was für Business Layer sprechen würde. 

## Projekt Erstellen und Setup 
- Projekt erstellen: New Project mit Maven, Java und JDK 21, Dependencies Spring Web (Dependencies kann man später noch rein geben)
- Wichtigste Dateien nach dem Setup
  - pom.xml
  - ToDoAppApplication: hier kann ich Applikation starten, auf der Console sehe ich auf welchem Port die Applikation läuft
  - Testen ob alles funktioniert hat: Applikation starten und wenn ich 404 (?) bekomme, ist alles okay
- Controller und Entity Package 


## Rest und Controller Implementation 
### Rest Allgemein 
REST (Representational State Transfer) ist Software Architektur Style der Richlinien fürs
Implementieren von Web Services definiert (u.a. wie man paths benennt). 

1. Uniform Interface: Benennung von Routen und Endpunkten
2. gedacht für Client-Server Architektur
3. Layered System
4. Stateless: Jedes mal wenn ich einen Http-Request sende müssen alle Infos in diesem inkludiert sein. 
Bsp. für Statefull wäre wenn wir z.b. speichern das wir einen Login gemacht haben. __Wieso:__ verbessert die Perfomance da 
alle Anfragen von jedem Server beantwortet werden können. 
5. Cashable: Anfragen können in den Zwischenspeicher gespeichert werden. Das heißt, dass wenn ich in einem bestimmten Zeitraum die gleiche Anfrage stelle, 
bekomme ich immer dieselbe Antwort. Das verbessert die Performance aber bedeutet auch, dass ich nicht immer die aktuellste Antwort habe (Cash weiß nicht ob sich am Server was geändert hat). 
6. Aktuell egal, nicht in VO besprochen 

https://www.visual-paradigm.com/guide/development/what-is-rest-api/

### CRUD (Antwort von ChatGPT)
CRUD ist ein Konzept aus der Softwareentwicklung, das die vier Grundoperationen für Daten beschreibt:

| Operation | Bedeutung          | Beispiel                      |
| --------- | ------------------ | ----------------------------- |
| **C**     | Create (Erstellen) | Einen neuen Datensatz anlegen |
| **R**     | Read (Lesen)       | Daten abfragen/anzeigen       |
| **U**     | Update (Ändern)    | Daten aktualisieren           |
| **D**     | Delete (Löschen)   | Daten entfernen               |


Zusammenhang CRUD und REST: REST verwendet HTTP-Methoden, um CRUD-Operationen umzusetzen.

### Rest hier: Benennung der Paths die über Mapping in Controller-Klasse definiert werden 
Für uns ist die Benennung der Endpunkte am wichtigsten. Die Endpunkte werden in der Controller-Klasse definiert. 
Jede Entity hat eine eigene Controller-Klasse. 
Bsp.: Benennung der Endpunkte anhand der Entity Klasse `Todo`


| Methode  | Bsp Path für Entity-Klasse `Todo` | Bedeutung                       | 
| -------- |-----------------------------------|---------------------------------| 
| `GET`    | /todos                            | ALLE Daten vom Server **holen** |
| `GET`    | /todos/{id}                       | ALLE Daten vom Server **holen** |
| `POST`   | /todos                            | Neue Daten **erstellen**        | 
| `PUT`    | /todos/{id}                       | Daten **aktualisieren**         | 
| `DELETE` | /todos/{id}                       | Daten **löschen**               |





## Implementierung der CRUD-Methoden (todo:überarbeiten )
Einbinden: SpringDoc OpenAPI Starter WebMVC UI (in pom.xml)
swagger: kann json doc aus meinem code für documentationmachen und mit swagger visualisieren (ist dann in resources/application.properties)
und so kann ich routen aufrufen um das alles mal auszuprobieren

## Datenvalidierung (todo:überarbeiten )
über Annotationen in der Entity Klasse und @Valid Annotaton in Post methode
Validierung auch im Backend i.d. Business Logik, Frontend optional über js aber js kann man ausschalten
wieso nicht auf DB verlagern: will DB austauschen können, je mehr ich in DB konfiguriere desto schwerer ist es sie später auszutauschen
ResponseStatus Annotation


## Service Klasse in Controller einbinden 
Service-Klasse wird als Attribut in der Controller KLasse in diese eingebunden. Wir haben 2 Versionen des Konzepts 
Dependency Injection (FieldInjection und ConstructorInjection) kennengelernt. 

Allgemein Dependency Injection: Man versucht Objekte von außen zu bekommen anstatt sie in der eigenen Klasse zu erzeugen 
(i.e. nicht selber new Object() aufrufen). Dadurch kann ich verantwortung über diese Klasse abgeben. 

Vorteile ConstructorInjection: 
- Attribut kann final sein 
- Testen ist leichter weil ich im Konstruktor ServiceKlasse als Testklasse übergeben kann und nicht Blackbox Code 
von Autowired habe. 
- Allgemein ist Autowired schwerer zu kontrollieren weil Black Box 


Bsp Code: 
```
// FieldInjection
@RestController
public class Controller {
   @Autowired
   private ServiceClass serviceClass; 
} 
```

```
// ConstructorInjection
@RestController
public class Controller {
   private ServiceClass serviceClass; 
   public Controller(ServiceClass serviceClass){ ... }; 
} 
```


## Architektur Input zu Schichten Trennung (todo:überarbeiten)

to-do Klasse ist in allen Schichten: cleane architektur hätte response-objekte in der Presentaion layer (Create-to-do: Validation object oder DTOs)
Bsp.: wie ResponseObject i.d. Präsentation (so würde es Marvin machen): Erzeugt aus Post-Request (hat keine id) fertige Business-Logik Objekte und übergibt diese an Business-Schicht

## Exceptions
Benötigen Mapping zwischen Business-Layer und Präsentation Layer da Exceptions in Präsentation Layer Http-ErrorCode entsprechen.
- Annotation in Exception Klasse: `@ResponseStatus(HttpStatus.CONFLICT)`macht mapping zwischen Java-Klasse und HttpStatus 

## Docker
### Wieso: 
Wir verwenden Docker um unsere postgres Datenbank einzubinden. Dazu verwenden wir ein vorkonfiguriertes 
Docker image auf dem die DB gut läuft. 

Andere verwendung von Docker wäre darin zu Entwickeln. Dadurch kann ich die Applikation von dem eigenen System unabhängig machen. 
Diese Unabhängigkeit erleichtert gemeinsames Entwickeln und garantiert mir, dass die Anwendung auf jedem System läuft. 

### Docker: Wie verwenden 
1. Installation: https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository -> Install using the apt repository
2. Dependency in pom.xml einfügen 
3. Starten von Docker (Docker muss immer laufen, wenn cih die Applikation verwende)
   - sudo docker run -p "5432:5432" -e POSTGRES_USER=eww -e POSTGRES_PASSWORD=eww -e POSTGRES_DB=todoapp postgres
   - docker-compose.yml in cmd starten mit docker compose up (yml ist wie json nur ohne klammern (?); scope wird nicht durch Klammern sondern durch Formatierung definiert (wie bei python))
   - Erklärungen:`-p "5432:5432"`: Port-forwarding vom local host port 5432 auf docker port 5432.
4. Troubleshooting:
   - Postgres Stoppen: sudo systemctl stop postgresql
   - Prüfen ob port frei ist: sudo lsof -i :5432
   - `docker ps -a`: Anzeigen welche Container laufen  
   - `docker compose up`: Start der yml Datei (Achtung: Windows Docker Manuell starten)
   - `docker compose down`: Docker stoppen 


### Andere Möglichkeiten Datenbanken einzubinden: 
1. In-Memory: DB ist nicht persistent, nur zu Test zwecken 
2. DB in eigenständiger Applikation laufen lassen 
3. XAMP 

## Repository Schicht: 
Um die Repository Schicht zu implimentieren verwenden wir JPA (Java Persistence API) und Hibernate. 
Dependency muss in pom.yml inkludiert werden 
JPA ist ein Interface. Hibernate implementiert dieses Interface. Es ist ein Object Relational Mapper (ORM). 
ORMs Mappen zwischen OOP und Relationalen Datenbanken i.e. es __macht aus Java Objekten Einträge in relationalen Datenbanken 
und aus DB-Einträgen Java Objekte.__ 

__Bsp.: unterschiede zwischen DB und OOP__
In relationalen Datenbanken wäre Zeile 1 und 2 gleich (hier kein Primary Key) aber die Variablen `a` und `b` sind in Java 
nicht gleich da sie unterschiedliche Referenzen besitzen.    


| Name | Alter |     
|------|-------|
| "A"  | 30    | 
| "A"  | 30    |
| "B"  | 30    |

`Person a = new Person("A", 30); Person b = new Person("A", 30); `

### JPA How to 
- Dependency in pom.xml hinzufügen 
- `public interface MyRepository extends JpaRepository<Entity, id-type>`: Ich muss Entity Datentyp und Datentyp der ID angeben 
- `Optional` als return Type: Ist eine Möglichkeit mit Rückgabetyp zu signalisieren, dass es auch nichts zurück gegeben 
werden könnte. Es ist eine Alternative zu null bei der weniger NullPointerExceptions auftreten 
können. Wenn ich `Optional` zurück bekomme muss ich immer checken ob was in meinem Objekt ist. 
- Interface hat CRUD-Methoden. Ich muss diese Methoden nicht implementieren da sie automatisiert vom Hibernate-Framework 
implementiert werden i.e. CRUD-Methoden werden in SQL-Statements übersetzt. 