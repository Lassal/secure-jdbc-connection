package br.lassal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;

@SpringBootApplication
public class DemoJdbcConnectionApplication implements CommandLineRunner {

	public static void main(String[] args) {

		SpringApplication.run(DemoJdbcConnectionApplication.class, args);

	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void run(String... strings) throws Exception {

		System.out.println("Executou....");

		String sql = "SELECT SYSDATE FROM DUAL";

		for(int i=0; i < 20; i++){
			Date dbNow = jdbcTemplate.queryForObject(sql, Date.class);
			System.out.println("Data no DB: " + dbNow.toString());
			jdbcTemplate.execute(sql);

			Thread.sleep(1000);
		}

		Thread.sleep(4000);

	}


}
