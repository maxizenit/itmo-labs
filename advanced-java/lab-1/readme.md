# Лабораторная работа 1
Разработать standalone приложение, которое имеет следующие возможности:

Принимает на вход проект в виде .jar файла С помощью библиотеки для манипуляции байткодом (ASM) посчитать и вывести следующие метрики:
* Максимальная глубина наследования
* Средняя глубина наследования
* Метрика ABC (assignment-ом является факт записи в локальную переменную istore, astore и т.д.)
* Среднее количество переопределенных методов
* Среднее количество полей в классе

Метрики должны выводиться как в консоль, так и в заданный пользователем файл в машинно-читаемом формате JSON

Гайд по использованию ASM: https://asm.ow2.io/asm4-guide.pdf