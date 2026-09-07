# GitStart

GitStart - CLI-утилита для быстрой инициализации Git и создания GitHub-репозитория для существующего проекта.

Утилита определяет текущий проект, подготавливает Git-репозиторий, создает первый кормит и подключает проект к GitHub. 

## Возможности

GitStart автоматически:

- определяет название текущего проекта;
- определяет тип проекта;
- проверяет наличие Git;
- проверяет наличие GitHub CLI;
- проверяет авторизацию GitHub;
- создает .gitignore для проекта;
- выполняет git init, если репозитория еще не создан;
- создает initial commit;
- устанавливает основную ветку main;
- предлагает выбрать Private или Public для GitHub-репозитория;
- создает новый репозитория на GitHub;
- отправляет проект на GitHub.

Если Git-репозиторий или origin уже существует, GitStart определяет их и не создает повторно.

## Пример

Вместо ручного выполнения:

``` txt
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin ...
git push -u origin main
```

достаточно перейти в папку проекта и выполнить:

``` powershell
gitstart
```
Пример работы:

``` powershell
GitStart

Project: MyProject
Type: Java / Maven

Checking environment...
✓ Git installed
✓ GitHub CLI installed
✓ GitHub authentication detected

Environment is ready.

Preparing project...
✓ .gitignore created

Git repository state:
✗ Local repository detected

Initializing local Git repository...
✓ Repository initialized

Creating initial commit...
✓ Initial commit created
✓ Main branch configured

Repository visibility:

1. Private
2. Public
Choose [1]: 1

Creating GitHub repository...
✓ GitHub repository created
✓ Remote origin configured

Pushing project to GitHub...
✓ Project pushed to GitHub

Done.
```

## Установка на Windows

Откройте PowerShell и выполните:

``` powershell
irm "https://raw.githubusercontent.com/h1llop13/GitStart/main/install.ps1" | iex
```

Установщик проверит необходимые компоненты и при необходимости установит:

- Git;
- GitHub CLI;
- Java 26.

После установки GitStart доступен как обычная команда:

``` powershell
gitstart
```

Для работы с GitHub необходимо один раз авторизоваться:

``` powershell
gh auth login
```

Проверить авторизацию можно командой:

``` powershell
ht auth status
```

## Использование

Перейдите в корневую папку проекта:

``` powershell
cd C:\path\to\project
```

Запустите:

``` powershell
gitstart
```

Название GitHub-репозитория по умолчанию будет соответствовать названию папки проекта.

## Требования

Для работы GitStart необходимы:

- Git;
- GitHub CLI;
- Java 26 или новее;
- подключение к интернету для создания репозитория на GitHub.

При установке через `install.ps1` недостающие компоненты устанавливаются автоматически. 

## Сборка из исходников

Для сборки используется Maven:

``` bash
man clean package
```

Готовый JAR будет находиться в директории:

``` bash
target/
```

Запустить его напрямую можно командой:

``` bash
java -jar target/gitstart-1.0.0-SNAPSHOT.jar
```

## Стек

- Java 26
- Maven
- Picocli
- Git
- GitHub CLI
- PowerShell - установщик Windows

## Статус

Проект находится в разработке. Текущая версия реализует основной сценарий: подготовку локального Git-репозитория и создание связанного репозитория на GitHub.
