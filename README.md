# Inventory accounting API

Для запуска приложения необходимо:
1. Наличие в PostgreSQL пустой базы данных с названием "storage"
2. Java 11, Maven

Для удобства локального тестирования приложения добавлен spring профиль localtest который можно активировать, указав JVM параметр "-Dspring.profiles.active=localtest". В данном режиме применяется миграция данных, которая добавляет пользователя-администратора.

## Предметная область
Приложение для учета товаров на складах. 

Доступны 2 сущности: "Товар" и "Склад" для которых реализованы CRUD операции. Также имеется возможность проводить документы трех видов: "Поступление", "Продажа" и "Перемещение" товаров, с помощью которых можно реализовать одноименные операции с товарами на складах. На основе проведенных документов имеется возможность построить отчеты, содержащие результаты работы документов. Первый отчет "Общий список товаров" содержит все имеющиеся в системе товары (включая те, что еще ни разу не были поставлены на склад), включая информацию о них: артикул, наименование, цены закупки и продажи (поля с ценами у товара заполняются только после проведения соотвествующих документов для него).  Второй отчет "Остатки товаров на складах" содержит только те товары, которые находятся на складах, включая информацию о них: артикул, наименование, остаток по всем складам (значение остатка товара на складе изменяется в зависимости от любого проведенного с этим товаром документа). Предусмотрена авторизация и аутентификация (на основе токенов) пользователя в системе учета товаров на складах.

API для работы с сущностями оперирует форматом JSON. С документами и отчетами работа может проводится как в формате JSON, так и в формате CSV. Визуализация API через Swagger UI. 

## Используемые технологии
Spring Boot, SpringSecurity, Spring Data JPA, Spring Web, SpringDoc, PostgreSQL, H2, Maven, Flyway, JJWT, Opencsv, Lombok.
