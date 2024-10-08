# InQoolKurty
REST API aplikace vytvořená za účelem přijímacího pohovoru společnosti InQool a.s..  
  
    
# Použité nástroje
modelování - Visual Paradigm (https://www.visual-paradigm.com/)  
ověření REST API  - Bruno, portable (https://www.usebruno.com/)  
vývoj -  Spring Tools 4 for Eclipse (https://spring.io/tools)  
databáze - h2 - konzole h2 v rámci projektu Spring Tools 4, URL http://localhost:8080/h2-console  
logování - knihovna @Slf4j z lomboku  

# Diagramy
V adresáři [/InQoolKurty/docs/diagramy](https://github.com/pehavelka/InQoolKurty/tree/main/docs/diagramy) jsou uloženy erd, class a use-case diagram.  

# Databáze
Inicializace pomocí knihovny liquibase.  
Vytvoření tabulek:  
[/InQoolKurty/src/main/resources/db/changelog/changelog-schema.sql](https://github.com/pehavelka/InQoolKurty/blob/main/src/main/resources/db/changelog/changelog-schema.sql)  

Inicializace dat:  
[/InQoolKurty/src/main/resources/db/changelog/changelog-data.sql](https://github.com/pehavelka/InQoolKurty/blob/main/src/main/resources/db/changelog/changelog-data.sql)  

# Volání služeb REST API
V adresáři [/InQoolKurty/docs/bruno/InQool](https://github.com/pehavelka/InQoolKurty/tree/main/docs/bruno/InQool) je uložena kolekce volání REST API  
  
# Popis aplikace
Pro logování sql selectů včetně hodnot parametrů jsem použil knihovnu p6spy.  

Aplikace obsahuje metody pro vytváření, změnu a zneplatnění daných objektů.  

Záznamy nejsou fyzicky mazány, ale obsahují příznak platnost.  
platnost = 1 - záznam je platný  
platnost = 0 - záznam je neplatný, smazaný  

V rámci výkonných akcí se provádí kontroly.  
Kontroly mohou mít úroveň ERROR a WARN.  
Po provedení akce se vrátí odpověď 200 a JSON uložených dat  

Při volání jednotlivých výkonných akcí se předává parametr provest.  
Pokud je nastaven:  
false - provádí se kontroly a v případě úrovně ERROR nebo WARN je vrácena odpověď 400, jinak 200 a akce je provedena  
true - provádí se kontroly a v případě úrovně ERROR je vrácena odpověď 400, v případě WARN nebo bez chyb je vrácena odpoěď 200 a akce je provedena  

V controllerech jsou jednotlivé metody logovány na úrovni debug, která je nastavena v application.properties.  

# Ukázka volání - pro zneplatnění:
Ukázka č.1: POST http://localhost:8080/api/kurt/1?provest=false  
Pokud je v rámci kontrol vyhodnoceno varování nebo chyba, tak je vrácena odpověď 400 a seznam hlášení JSON  

Ukázka č.2: POST http://localhost:8080/api/kurt/1?provest=true  
Pokud je v rámci kontrol vyhodnoceno varování nebo nic, tak je vrácena odpověď 200 a záznam se zneplatní.  
Pokud je v rámci kontrol vyhodnocena chyba, tak je vrácena odpověď 400 a seznam hlášení JSON.  

# Třídy obsluhy Kurtů
KurtController  
KurtRepository  
KurtService  
KurtTest  

# Třídy obsluhy Povrchů
PovrchController  
PovrchRepository  
PovrchService  
PovrchTests  

# Třídy obsluhy Rezervací
RezervaceController  
RezervaceRepository  
RezervaceService  
RezervaceTest  

# Třídy obsluhy Zákazníků
ZakaznikController  
ZakaznikRepository  
ZakaznikService  
ZakaznikTest  

# Parametry aplikace [application.properties](https://github.com/pehavelka/InQoolKurty/blob/main/src/main/resources/application.properties), zapnutí/vypnutí inicializace schématu
spring.jpa.hibernate.ddl-auto=none  

# Parametry aplikace [application.properties](https://github.com/pehavelka/InQoolKurty/blob/main/src/main/resources/application.properties), úroveň logování aplikace
logging.level.cz.inqool=debug

# Spuštění testů
mvn test  

# Spuštění aplikace
mvn spring-boot:run  

