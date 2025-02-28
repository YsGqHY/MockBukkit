package org.mockbukkit.mockbukkit.services;

import org.mockbukkit.mockbukkit.plugin.EmptyPlugin;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicesManagerTest
{

	private ServerMock server;
	private EmptyPlugin plugin;

	@BeforeEach
	void setUp()
	{
		server = MockBukkit.mock();
		plugin = MockBukkit.loadWith(EmptyPlugin.class, "empty_plugin.yml");
	}

	@AfterEach
	void tearDown()
	{
		MockBukkit.unmock();
	}

	@Test
	void test_register_should_register()
	{
		// We use this class as the service class to avoid creating a useless class
		server.getServicesManager().register(ServicesManagerTest.class, this, plugin, ServicePriority.Normal);
		assertEquals(this, server.getServicesManager().load(ServicesManagerTest.class));
	}

	@Test
	void test_register_multiple_service()
	{
		Object obj = new Object();
		Object obj2 = new Object();
		Object obj3 = new Object();
		server.getServicesManager().register(Object.class, obj, plugin, ServicePriority.Normal);
		server.getServicesManager().register(Object.class, obj2, plugin, ServicePriority.High);
		server.getServicesManager().register(Object.class, obj3, plugin, ServicePriority.Low);
		// Should be the first object
		assertEquals(obj2, server.getServicesManager().load(Object.class));
		RegisteredServiceProvider<Object> registeredServiceProvider = server.getServicesManager()
				.getRegistration(Object.class);
		assertNotNull(registeredServiceProvider);
		assertEquals(ServicePriority.High, registeredServiceProvider.getPriority());
		assertEquals(3, server.getServicesManager().getRegistrations(Object.class).size());
	}

	@Test
	void test_load_not_registered_object()
	{
		assertNull(server.getServicesManager().load(Object.class));
	}

	@Test
	void test_load_registered_object()
	{
		Object object = new Object();
		server.getServicesManager().register(Object.class, object, plugin, ServicePriority.Normal);
		assertNotNull(server.getServicesManager().load(Object.class));
		assertEquals(object, server.getServicesManager().load(Object.class));
	}

	@Test
	void test_get_registration_not_registered_object()
	{
		assertNull(server.getServicesManager().getRegistration(Object.class));
	}

	@Test
	void test_get_registration_registered_object()
	{
		Object object = new Object();
		server.getServicesManager().register(Object.class, object, plugin, ServicePriority.Normal);

		RegisteredServiceProvider<Object> registeredServiceProvider = server.getServicesManager()
				.getRegistration(Object.class);
		assertNotNull(registeredServiceProvider);
		assertEquals(object, registeredServiceProvider.getProvider());
	}

	@Test
	void test_unregister_service_provider()
	{
		Object obj = new Object();
		Object obj2 = new Object();
		Object obj3 = new Object();
		server.getServicesManager().register(Object.class, obj, plugin, ServicePriority.Normal);
		server.getServicesManager().register(Object.class, obj2, plugin, ServicePriority.High);
		server.getServicesManager().register(Object.class, obj3, plugin, ServicePriority.Low);
		// Unregister existing object
		server.getServicesManager().unregister(Object.class, obj);
		assertEquals(2, server.getServicesManager().getRegistrations(Object.class).size());
		// Unregister unexisting object
		server.getServicesManager().unregister(Object.class, obj);
		assertEquals(2, server.getServicesManager().getRegistrations(Object.class).size());
	}

	@Test
	void test_unregister()
	{
		Object obj = new Object();
		Object obj2 = new Object();
		Object obj3 = new Object();
		server.getServicesManager().register(Object.class, obj, plugin, ServicePriority.Normal);
		server.getServicesManager().register(Object.class, obj2, plugin, ServicePriority.High);
		server.getServicesManager().register(Object.class, obj3, plugin, ServicePriority.Low);
		server.getServicesManager().register(ServicesManagerTest.class, this, plugin, ServicePriority.Normal);
		assertEquals(3, server.getServicesManager().getRegistrations(Object.class).size());
		assertEquals(1, server.getServicesManager().getRegistrations(ServicesManagerTest.class).size());
		// unregister
		server.getServicesManager().unregister(obj);
		assertEquals(2, server.getServicesManager().getRegistrations(Object.class).size());
		assertEquals(1, server.getServicesManager().getRegistrations(ServicesManagerTest.class).size());
		server.getServicesManager().unregister(obj);
		assertEquals(2, server.getServicesManager().getRegistrations(Object.class).size());
		assertEquals(1, server.getServicesManager().getRegistrations(ServicesManagerTest.class).size());
		server.getServicesManager().unregisterAll(plugin);
		assertEquals(0, server.getServicesManager().getRegistrations(Object.class).size());
		assertEquals(0, server.getServicesManager().getRegistrations(ServicesManagerTest.class).size());
	}

	@Test
	void test_get_registrations()
	{
		Object obj = new Object();
		Object obj2 = new Object();
		server.getServicesManager().register(Object.class, obj, plugin, ServicePriority.Normal);
		server.getServicesManager().register(Object.class, obj2, plugin, ServicePriority.High);
		server.getServicesManager().register(ServicesManagerTest.class, this, plugin, ServicePriority.Normal);
		assertEquals(3, server.getServicesManager().getRegistrations(plugin).size());
		server.getServicesManager().unregister(obj);
		assertEquals(2, server.getServicesManager().getRegistrations(plugin).size());
	}

	@Test
	void test_get_known_services()
	{
		Object obj = new Object();
		Object obj2 = new Object();
		server.getServicesManager().register(Object.class, obj, plugin, ServicePriority.Normal);
		server.getServicesManager().register(Object.class, obj2, plugin, ServicePriority.High);
		server.getServicesManager().register(ServicesManagerTest.class, this, plugin, ServicePriority.Normal);
		Set<Class<?>> knownServices = server.getServicesManager().getKnownServices();
		assertEquals(2, knownServices.size());
		assertTrue(knownServices.contains(Object.class));
		assertTrue(knownServices.contains(ServicesManagerTest.class));
	}

	@Test
	void test_is_provided_for()
	{
		Object obj = new Object();
		Object obj2 = new Object();
		server.getServicesManager().register(Object.class, obj, plugin, ServicePriority.Normal);
		server.getServicesManager().register(Object.class, obj2, plugin, ServicePriority.High);
		server.getServicesManager().register(ServicesManagerTest.class, this, plugin, ServicePriority.Normal);
		assertTrue(server.getServicesManager().isProvidedFor(Object.class));
		assertTrue(server.getServicesManager().isProvidedFor(ServicesManagerTest.class));
	}

}
