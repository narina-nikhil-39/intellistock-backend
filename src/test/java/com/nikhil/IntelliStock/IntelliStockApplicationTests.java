package com.nikhil.IntelliStock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class IntelliStockApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void corsPreflightIsAllowedForFrontend() throws Exception {
		mockMvc.perform(options("/api/products")
				.header("Origin", "http://localhost:5173")
				.header("Access-Control-Request-Method", "POST")
				.header("Access-Control-Request-Headers", "authorization,content-type"))
			.andExpect(status().isOk())
			.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
	}

	@Test
	void transactionsTableHasNullableUserAttributionColumn() {
		Integer matchingColumns = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
				+ "WHERE TABLE_SCHEMA = DATABASE() "
				+ "AND TABLE_NAME = 'transactions' AND COLUMN_NAME = 'user_id'",
			Integer.class
		);

		assertEquals(1, matchingColumns);
	}

	@Test
	void corsHeadersArePresentOnAuthenticationAndAuthorizationRejections() throws Exception {
		mockMvc.perform(get("/api/products")
				.header("Origin", "http://localhost:5173"))
			.andExpect(status().isUnauthorized())
			.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));

		mockMvc.perform(post("/api/products")
				.with(user("staff").roles("STAFF"))
				.header("Origin", "http://localhost:5173")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"sku\":\"cors-test\",\"name\":\"Cors Test\",\"initialStock\":0,\"safetyStockLevel\":0}"))
			.andExpect(status().isForbidden())
			.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
	}
}
