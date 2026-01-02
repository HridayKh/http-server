package in.HridayKh.http.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Optional;

public class ConfigLoader {

	private static final String DEFAULT_RESOURCE = "application.properties";

	private final Properties props = new Properties();

	private static final ConfigLoader INSTANCE = new ConfigLoader();

	private ConfigLoader() {
		load();
	}

	public static ConfigLoader getInstance() {
		return INSTANCE;
	}

	private void load() {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE)) {
			if (in != null) {
				props.load(in);
			}
		} catch (IOException ignored) {
			// ignore - no properties loaded
		}
	}

	public Optional<String> get(String key) {
		return Optional.ofNullable(props.getProperty(key));
	}

	public String getOrDefault(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	public int getInt(String key, int defaultValue) {
		String v = props.getProperty(key);
		if (v == null)
			return defaultValue;
		try {
			return Integer.parseInt(v.trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		String v = props.getProperty(key);
		return v != null ? Boolean.parseBoolean(v.trim()) : defaultValue;
	}

	public Properties asProperties() {
		Properties copy = new Properties();
		copy.putAll(this.props);
		return copy;
	}

	public void reload() {
		props.clear();
		load();
	}
}
