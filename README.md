# How to start
* [Базовый синтаксис Котлина](https://kotlinlang.org/docs/reference/basic-syntax.html)
* [Infix функции](https://kotlinlang.org/docs/reference/functions.html#infix-notation)
* [Extension функции](https://kotlinlang.org/docs/reference/extensions.html)
* [Результат через равно](https://kotlinlang.org/docs/reference/functions.html#single-expression-functions)

Запускать нужно в IDE: IntelliJ Idea.

## Как запустить?
1. Проверьте `File -> Project Structure -> Project`. В `Project SDK` должен быть выбран JDK. Желательно 13.0.2. 
Его можно скачать [тут](https://www.oracle.com/java/technologies/javase-jdk13-downloads.html).
2. Там же удостоверьтесь, что в `Project compiler output` указан путь до проекта и в конце `/out`. Пример:
`/home/kirill/IdeaProjects/parallel/out`.
3. Откройте нужую лабу, около функции `main` есть зелёная кнопка, нажмите на неё.

## Лабы
### Порядок
1. Вектор на число.
2. Вектор на вектор.
3. Матрица на вектор.
4. Матрица на матрицу.

### Структура
1. Создаём данные (ThreadPool, вектор, матрицу и т.п.).
2. Запускаем параллельную и обычную функцию которые делают одно и то же.
3. Если результаты одинаковые, то параллельная работает так же как и обычная, 
а значит мы не ошиблись в реализации.

## Посмотреть перед просмотром лаб
* Сущности из каталога `data`
* Файл `Extensions.kt`
* Файл `TestData.kt`
