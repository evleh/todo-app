# Todo-App


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