package me.sharpdev.pluginutils.database;

import org.bson.Document;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DocumentSerializer {
    private static final HashMap<Class<?>, Serializer<?>> SERIALIZERS = new HashMap<>();

    public static <T> void addSerializer(Class<T> clazz, Serializer<T> serializer) {
        SERIALIZERS.put(clazz, serializer);
    }

    public static void removeSerializer(Class<?> clazz) {
        SERIALIZERS.remove(clazz);
    }

    // Serializers for Spigot types
    static {
        addSerializer(NamespacedKey.class, new Serializer<>() {
            @Override
            public Document serialize(NamespacedKey object) {
                return new Document("key", object.getNamespace()).append("value", object.getKey());
            }

            @Override
            public NamespacedKey deserialize(Document document) {
                return new NamespacedKey(document.getString("key"), document.getString("value"));
            }
        });
        addSerializer(ItemStack.class, new Serializer<>() {
            @Override
            public Document serialize(ItemStack object) {
                Document document = new Document();

                document.append("bytes", object.serializeAsBytes());

                return document;
            }

            @Override
            public ItemStack deserialize(Document document) {
                if (document == null) return null;
                if (!document.containsKey("bytes")) return null;
                List<Byte> byteList = document.getList("bytes", byte.class);
                byte[] bytes = new byte[byteList.size()];
                for (int i = 0; i < byteList.size(); i++) {
                    bytes[i] = byteList.get(i);
                }
                return ItemStack.deserializeBytes(bytes);
            }
        });
        addSerializer(UUID.class, new Serializer<>() {
            @Override
            public Document serialize(UUID object) {
                return new Document().append("lsb", object.getLeastSignificantBits()).append("msb", object.getMostSignificantBits());
            }

            @Override
            public UUID deserialize(Document document) {
                return new UUID(document.getLong("msb"), document.getLong("lsb"));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> Document serializeObject(T object) {
        if(object == null) return null;
        if(object instanceof UpdateSerializable) {
            Document document = new Document();

            for(Field declaredField : object.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                int modifiers = declaredField.getModifiers();
                if(Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers)) continue;

                if(declaredField.isAnnotationPresent(UpdateSerializable.CustomMethod.class)) {
                    UpdateSerializable.CustomMethod customMethod = declaredField.getAnnotation(UpdateSerializable.CustomMethod.class);

                    try {
                        Method method = object.getClass().getDeclaredMethod(customMethod.serialize(), Document.class);

                        method.invoke(object, document);
                    } catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                        throw new SerializationException(object.getClass(), e);
                    }
                } else {
                    Class<?> type = declaredField.getType();
                    try {
                        if (type == int.class || type == Integer.class || type == float.class || type == Float.class || type == double.class || type == Double.class || type == long.class || type == Long.class || type == boolean.class || type == Boolean.class || type == byte.class || type == Byte.class || type == char.class || type == Character.class || type == short.class || type == Short.class || type == String.class) {
                            document.append(declaredField.getName(), declaredField.get(object));
                        } else {
                            document.put(declaredField.getName(), serializeObject(declaredField.get(object)));
                        }
                    } catch(IllegalAccessException e) {
                        throw new SerializationException(type, e);
                    }
                }
            }

            return document;
        }
        Class<?> clazz = object.getClass();
        if(SERIALIZERS.containsKey(clazz)) {
            return ((Serializer<T>) SERIALIZERS.get(clazz)).serialize(object);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(Class<T> clazz, Document document) {
        if(UpdateSerializable.class.isAssignableFrom(clazz)) {
            try {
                Constructor<T> constructor = clazz.getConstructor();
                constructor.setAccessible(true);

                T defaultObject = constructor.newInstance();

                for (Field declaredField : clazz.getDeclaredFields()) {
                    int modifiers = declaredField.getModifiers();
                    if(Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers)) continue;
                    if(declaredField.isAnnotationPresent(UpdateSerializable.CustomMethod.class)) {
                        UpdateSerializable.CustomMethod customMethod = declaredField.getAnnotation(UpdateSerializable.CustomMethod.class);

                        Method method = clazz.getDeclaredMethod(customMethod.deserialize(), Document.class);

                        method.invoke(defaultObject, document);
                    } else {
                        Class<?> type = declaredField.getType();
                        if (type == int.class || type == Integer.class || type == float.class || type == Float.class || type == double.class || type == Double.class || type == long.class || type == Long.class || type == boolean.class || type == Boolean.class || type == byte.class || type == Byte.class || type == char.class || type == Character.class || type == short.class || type == Short.class || type == String.class) {
                            declaredField.set(defaultObject, document.get(declaredField.getName(), type));
                        } else {
                            declaredField.set(defaultObject, deserializeObject(type, document.get(declaredField.getName(), Document.class)));
                        }
                    }
                }

                return defaultObject;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new SerializationException(clazz, e);
            }
        }
        if(SERIALIZERS.containsKey(clazz)) {
            return ((Serializer<T>) SERIALIZERS.get(clazz)).deserialize(document);
        }
        return null;
    }

    public interface Serializer<T> {
        Document serialize(T object);
        T deserialize(Document document);
    }
}
