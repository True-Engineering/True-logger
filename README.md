# Библиотек со spring аспектом для логирования запроса и ответа в контроллерах

### Использование: Maven
Добавьте в свой pom файл проекта зависимость и репозиторий:
```
<repositories>
    [...]
    <repository>
      <id>github</id>
      <url>https://maven.pkg.github.com/True-Engineering/True-logger</url>
      <releases>
        <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
    </repository>
    [...]
</repositories>
...
<dependencies>
    [...]
    <dependency>
        <groupId>ru.trueengineering</groupId>
        <artifactId>true-logger</artifactId>
        <version>1.15</version>
    </dependency>
    [...]
</dependencies>
```

### Использование: Gradle
```
compile(group: 'ru.trueengineering', name: 'true-logger', version: '1.15')

maven { url 'https://maven.pkg.github.com/True-Engineering/True-logger' }
```

### Конфигурация
Для конфигурации необходимо указать в своем application.properties или application.yml следующие настройки
```
# true если хотим использовать аспект для логирования
trueengineering.logging.enabled=true
# в этой проперте указывается список regexp шаблонов, по которым будут определяться классы для которых нужно применять аспект
trueengineering.logging.packages=ru.trueengineering.lib.logger.aspect.logging.controller
# в этой проперте указывается базовый пакет, начиная с которого в проекте лежат классы с дто
trueengineering.logging.base.package.for.models=ru.trueengineering.lib.logger.aspect.logging
```

### MDC
Библиотека добавляет в контекст MDC аргументы:
* requestMillis - время выполнение запроса в миллисекундах 
* METHOD - название вызыываемого метода 
* LOG_TEMPLATE - формат шаблона строчки лога в поле message 
