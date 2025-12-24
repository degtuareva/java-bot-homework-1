package org.homework.di;

import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DIContainer {
    private final Map<Class<?>, Object> createdServices = new HashMap<>();

    private final Map<Class<?>, Class<?>> registeredImplementations = new HashMap<>();

    public DIContainer() {
        autoRegister();
    }

    private void autoRegister() {
        Reflections reflections = new Reflections("org.homework",
                new SubTypesScanner(false),
                new TypeAnnotationsScanner());

        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Register.class);

        for (Class<?> clazz : annotated) {
            if (clazz.isInterface()) {
                continue; // Если это интерфейс, пропускаем, так как ищем только реализации
            }

            if (!verifyNoArgConstructor(clazz)) {
                continue; // Если нет конструктора без аргументов, пропускаем класс
            }

            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> intf : interfaces) {
                // Регистрируем реализацию если она еще не зарегистрирована или
                // если класс-реализация не является интерфейсом (предпочитаем реальные реализации)
                if (!registeredImplementations.containsKey(intf) || registeredImplementations.get(intf).isInterface()) {
                    registeredImplementations.put(intf, clazz);
                }
            }
            if (interfaces.length == 0) {
                // Если у класса нет интерфейсов, регистрируем его сам в себя
                registeredImplementations.put(clazz, clazz);
            }
        }
    }

    public <T> T resolve(Class<T> serviceClass) {
        @SuppressWarnings("unchecked")
        T service = (T) createdServices.get(serviceClass);
        if (service == null) {
            service = createService(serviceClass);
        }
        return service;
    }

    private <T> T createService(Class<T> serviceClass) {
        // Рефакторинг произведен для упрощения понимания
        if (serviceClass.isInterface()) {
            return createServiceFromInterface(serviceClass);
        } else {
            return createServiceFromClass(serviceClass);
        }
    }

    private <T> T createServiceFromInterface(Class<T> serviceClass) {
        Class<?> implementationClass = registeredImplementations.get(serviceClass);
        if (implementationClass == null) {
            throw new IllegalStateException("No implementation registered for interface: " + serviceClass.getName());
        }
        return createServiceFromClass(implementationClass.asSubclass(serviceClass));
    }

    // Специальный метод для создания сервисов для класса (не интерфейса)
    private <T> T createServiceFromClass(Class<T> concreteClass) {
        try {
            T instance = concreteClass.getDeclaredConstructor().newInstance();
            createdServices.put(concreteClass, instance); // Сохраняем перед инъекцией зависимостей
            injectDependencies(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate service: " + concreteClass.getName(), e);
        }
    }

    private void injectDependencies(Object object) {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Resolve.class)) {
                field.setAccessible(true);

                Class<?> dependencyType = field.getType();
                Object dependency = resolve(dependencyType);

                try {
                    field.set(object, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(String.format("Could not set field '%s' on class '%s'.", field.getName(), clazz.getName()), e);
                }
            }
        }
    }

    private boolean verifyNoArgConstructor(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            // Логировать предупреждение о том, что класс не имеет конструктора без аргументов
            return false;
        }
    }
}