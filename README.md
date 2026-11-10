# java-library

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white)
![Build](https://img.shields.io/badge/build-Maven-blue?logo=apachemaven&logoColor=white)
![Tests](https://img.shields.io/badge/tests-JUnit%205-brightgreen?logo=junit5&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

A console application written in **Java** to manage a small library: documents (books and magazines), readers, borrowing and returning, searching, sorting, statistics, and persistent storage in a plain text file.

The project is built as a clean, layered, well-tested Java application. It focuses on **object-oriented design, collections, modern Java (lambdas and Streams), validation, error handling, file persistence, automated tests and Maven**, without any external framework.

## Features

- **Documents**: add, remove, display books and magazines (with number of copies).
- **Readers**: register and remove readers identified by a unique id.
- **Borrowing**: borrow and return documents, with availability tracking per copy.
- **Search**: by title, publication year, author, or availability.
- **Sorting**: by title, or by publication year (ascending or descending).
- **Statistics**: number of documents, total copies, available documents, oldest and newest publication year.
- **Persistence**: data is saved to a text file after every change and restored at startup.
- **Safe input handling**: invalid numbers are rejected and asked again, invalid values are refused with a clear message.
- **Strict loading**: an invalid save file is reported with the exact line number, and is never overwritten.

## Quick start

### Prerequisites

- JDK **17 or higher**
- Maven **3.9 or higher**

### Build and test

```bash
git clone https://github.com/Lekeb25/java-library.git
cd java-library
mvn clean package
```

`mvn clean package` compiles the code, runs all the tests, and builds the executable JAR in `target/`. If a test fails, no JAR is produced.

### Run

```bash
java -jar target/java-library-1.0.0.jar
```

The save file is created in the **directory you run the command from**, so running the program from another directory starts a new library. On the first launch, the library is initialised with sample data.

## Usage

```text
================ MENU =====================
1. Show library
2. Add a reader
3. Remove a reader
4. Add a document
5. Remove a document
6. Borrow a document
7. Return a document
8. Search documents
9. Show sorted documents
10. Library statistics
11. Quit
12. Print a menu
```

Example of the library display (option 1):

```text
Readers:
+------+-----------------+-----------------+
| ID   | First name      | Last name       |
+------+-----------------+-----------------+
| 1    | Nabi            | Kenne           |
| 2    | Kebodel         | Bauer           |
+------+-----------------+-----------------+

Books:
+----------------------+--------------------+--------+----------+---------------------------+
| Title                | Author             | Year   | Copies   | Borrowers                 |
+----------------------+--------------------+--------+----------+---------------------------+
| Dune                 | Frank Herbert      | 1965   | 3        | 1                         |
| Steppenwolf          | Herman Hesse       | 1927   | 4        | -                         |
+----------------------+--------------------+--------+----------+---------------------------+

Magazines:
+----------------------+--------+----------+--------+-----------------+---------------------------+
| Title                | Year   | Copies   | Issue  | Publisher       | Borrowers                 |
+----------------------+--------+----------+--------+-----------------+---------------------------+
| National Geographic  | 2024   | 2        | 245    | NatGeo Society  | 1, 2                      |
+----------------------+--------+----------+--------+-----------------+---------------------------+
```

The **Borrowers** column shows the ids of the readers who currently borrow the document.

## Project structure

```text
java-library/
├── pom.xml
├── README.md
├── LICENSE
├── docs/
│   └── ARCHITECTURE.md
└── src/
    ├── main/java/
    │   ├── app/          Main: console interface and orchestration
    │   ├── library/      Library, DocumentCriteria: business logic, search and sorting
    │   ├── model/        Document, Book, Magazine, Reader, Borrowable, Validation
    │   ├── exception/    Domain exceptions and InvalidSaveFileException
    │   └── persistence/  LibraryFileStorage: save and load
    └── test/java/        Unit tests (library, model, persistence)
```

The `app` package depends on the others, and nothing depends on `app`. The business logic knows nothing about the console or the file. Details, diagrams and design decisions are in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Design highlights

| Topic | Choice |
|---|---|
| Collections | `List<Document>` for ordered iteration, `Map<Integer, Reader>` for direct lookup by id. Fields are declared with interface types |
| Search | A single `searchDocuments(Predicate<Document>)`, with reusable and combinable criteria in `DocumentCriteria` (`withTitle`, `publishedInYear`, `byAuthor`...) |
| Sorting | `Comparable` defines the natural order of documents (title, then year). `Comparator` arguments provide any other order without touching `Document` |
| Equality | `equals` and `hashCode` are consistent. A reader is identified by its id, a document by its type, title and year (plus its own attributes) |
| Validation | Domain objects refuse invalid values in constructors and setters, through a single package-private `Validation` class |
| Errors | Domain rules throw unchecked exceptions. Invalid save files throw a checked `InvalidSaveFileException` (an `IOException`), with the line number |
| Encapsulation | Getters return defensive copies, so callers cannot modify internal collections |
| Persistence | The whole state is rewritten on each save, in a text format that is loaded in two passes |

## Save file format

The save file is a plain text file (named `library-data.txt` by default, see `Main`). One line per element, fields separated by `;`:

```text
READER;firstName;lastName;id
BOOK;title;author;publicationYear;numberOfCopies
MAGAZINE;title;publicationYear;numberOfCopies;issueNumber;publisher
BORROW;title;publicationYear;readerId
```

Blank lines are ignored. Any invalid line stops the loading, and the file is left untouched. The message has this form:

```text
Error: could not load saved data from <save file name>: Invalid save file, line <line number>: <reason>
```

The reason can be, for example, an unknown line type, a wrong number of fields, an unreadable number, a value refused by the model, or a borrow that refers to an unknown reader or document.

## Testing

```bash
mvn test
```

The test suite contains close to 100 JUnit 5 tests covering:

- business rules and exceptions of `Library` (add, remove, borrow, return, search, sort, aggregates, empty library cases);
- `Document`, `Book`, `Magazine` and `Reader` (equality, ordering, validation, file line format);
- `LibraryFileStorage`: save and load round trip, written file format, and every kind of invalid file (using JUnit temporary directories, so the real data file is never touched).

## Known limitations

- **Text fields cannot contain `;` or line breaks.** The save file uses `;` between fields and a line break between elements, with no escaping. A title containing `;` would be cut in two when the file is read back, so the application refuses such values.
- **A badly timed interruption can damage the save file.** Each save rewrites the whole file. If the program is killed exactly during the write (for example by a power cut), the file can be left incomplete. The next start reports the problem with a line number instead of loading wrong data, and does not overwrite the file.
- **Single user, single process.** Two instances running on the same save file would overwrite each other's changes, because there is no locking.
- **Documents have no technical id.** A document is identified by its title and publication year. Two different editions sharing both cannot be told apart in a search.
- **Text menu only.** The console layer has no automated tests; the logic it calls is fully tested.

## Possible improvements

- Expose the library through a **REST API** (Spring Boot), reusing the existing business layer.
- Replace the text file with a **database**.
- Use **JSON** (or escaping) for the save format.
- Write the save file atomically (temporary file, then rename).
- Introduce a common parent exception for all domain exceptions.
- Add technical identifiers for documents.

## Author

**Lekeb25** — [github.com/Lekeb25](https://github.com/Lekeb25)

## License

This project is licensed under the MIT License: you may use, modify and distribute the code, including commercially, **as long as you keep the copyright notice** that credits the author. See the [LICENSE](LICENSE) file.
