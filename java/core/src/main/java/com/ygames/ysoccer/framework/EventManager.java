package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.events.GameEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventManager {
    private static final Map<Class<? extends GameEvent>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

    private EventManager() {
    }

    public static <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<?>> eventListeners = listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>());
        eventListeners.add(listener);
    }

    public static void publish(GameEvent event) {
        List<Consumer<?>> eventListeners = listeners.get(event.getClass());

        if (eventListeners == null) return;

        for (Consumer<?> listener : eventListeners) {
            @SuppressWarnings("unchecked")
            Consumer<GameEvent> typedListener = (Consumer<GameEvent>) listener;
            try {
                typedListener.accept(event);
            } catch (Exception e) {
                System.err.println("Error in event listener for " + event.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
    }

    public static void clear() {
        listeners.clear();
    }
}
