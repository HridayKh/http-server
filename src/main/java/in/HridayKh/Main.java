package in.HridayKh;

import in.HridayKh.DI.ClassScanner;
import in.HridayKh.DI.Registry;
import in.HridayKh.DI.RegistryValidator;
import in.HridayKh.config.ConfigLoader;
import in.HridayKh.http.Http;

public class Main {

	public static void main(String[] args) {
		System.out.println("Loading configuration...");
		long startTime = System.currentTimeMillis();

		ConfigLoader config = new ConfigLoader("application.properties");
		config.load();

		long preScanTime = System.currentTimeMillis();
		System.out.println("Configuration loaded in " + (preScanTime - startTime) + " ms");
		System.out.println("Starting classpath scan and registry population...");

		Registry registry = new Registry();
		ClassScanner classScanner = new ClassScanner(config, registry);
		classScanner.scan();

		long preValidationTime = System.currentTimeMillis();
		System.out.println("Classpath scan completed in " + (preValidationTime - preScanTime) + " ms");
		System.out.println("Starting registry validation...");

		RegistryValidator registryValidator = new RegistryValidator(config, registry);
		registryValidator.validate();

		long endTime = System.currentTimeMillis();
		System.out.println("Registry validation completed in " + (endTime - preValidationTime) + " ms");
		System.out.println("Setup completed in " + (endTime - startTime) + " ms");

		Http http = new Http(config);
		http.createServer();
	}

}
