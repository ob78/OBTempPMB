package com.paymybuddy.configuration;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class RepositoryDataSource {

	public static DriverManagerDataSource getDataSource(String driverClassName, String url, String username,
			String password) {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		return dataSource;
	}

}
