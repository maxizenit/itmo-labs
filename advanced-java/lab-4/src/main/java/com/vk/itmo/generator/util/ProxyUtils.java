package com.vk.itmo.generator.util;

import com.vk.itmo.generator.Generator;
import java.lang.reflect.Proxy;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Утилиты для работы с прокси. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProxyUtils {

  /**
   * Генерирует прокси-объект заданного интерфейса. Методы {@code toString}, {@code equals} и {@code
   * hashCode} заранее определелены, остальные методы возвращают сгенерированные {@link Generator}
   * значения.
   *
   * @param interfaceClass класс интерфейса
   * @param random рандомизатор
   * @return прокси-объект заданного интерфейса
   * @throws IllegalArgumentException если переданный класс не является интерфейсом
   */
  public static @NonNull Object generateProxy(
      @NonNull Class<?> interfaceClass, @NonNull Random random) {
    if (!interfaceClass.isInterface()) {
      throw new IllegalArgumentException(interfaceClass.getSimpleName() + " is not an interface");
    }
    return Proxy.newProxyInstance(
        interfaceClass.getClassLoader(),
        new Class<?>[] {interfaceClass},
        (proxy, method, args) ->
            switch (method.getName()) {
              case "toString" -> "%s(proxy)".formatted(interfaceClass.getSimpleName());
              case "equals" -> proxy == args[0];
              case "hashCode" -> System.identityHashCode(proxy);
              default -> Generator.generateValueOfType(method.getReturnType(), random, 1);
            });
  }
}
