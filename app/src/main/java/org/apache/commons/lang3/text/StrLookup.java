package org.apache.commons.lang3.text;

import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public abstract class StrLookup<V> {
    private static final StrLookup<String> NONE_LOOKUP = new MapStrLookup(null);
    private static final StrLookup<String> SYSTEM_PROPERTIES_LOOKUP = new SystemPropertiesStrLookup();

    public abstract String lookup(String str);

    public static StrLookup<?> noneLookup() {
        return NONE_LOOKUP;
    }

    public static StrLookup<String> systemPropertiesLookup() {
        return SYSTEM_PROPERTIES_LOOKUP;
    }

    public static <V> StrLookup<V> mapLookup(Map<String, V> map) {
        return new MapStrLookup(map);
    }

    protected StrLookup() {
    }

    static class MapStrLookup<V> extends StrLookup<V> {
        private final Map<String, V> map;

        MapStrLookup(Map<String, V> map) {
            this.map = map;
        }

        @Override // org.apache.commons.lang3.text.StrLookup
        public String lookup(String key) {
            Object obj;
            Map<String, V> map = this.map;
            if (map == null || (obj = map.get(key)) == null) {
                return null;
            }
            return obj.toString();
        }
    }

    private static class SystemPropertiesStrLookup extends StrLookup<String> {
        private SystemPropertiesStrLookup() {
        }

        @Override // org.apache.commons.lang3.text.StrLookup
        public String lookup(String key) {
            if (!key.isEmpty()) {
                try {
                    return System.getProperty(key);
                } catch (SecurityException e) {
                    return null;
                }
            }
            return null;
        }
    }
}
