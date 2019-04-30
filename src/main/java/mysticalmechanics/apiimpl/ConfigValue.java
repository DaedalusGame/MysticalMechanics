package mysticalmechanics.apiimpl;

import mysticalmechanics.api.IConfigValue;

public abstract class ConfigValue<T> implements IConfigValue<T> {
    Class<T> type;
    String key;

    public ConfigValue(Class<T> type, String key) {
        this.type = type;
        this.key = key;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}
