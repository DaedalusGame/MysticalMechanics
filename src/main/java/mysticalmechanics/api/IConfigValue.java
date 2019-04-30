package mysticalmechanics.api;

public interface IConfigValue<T> {
    T getValue();

    void setValue(T value);

    String getKey();

    Class<T> getType();
}
