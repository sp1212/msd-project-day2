package com.webage.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

public class CustomerAPIJUnitIntegrationTest {

	@Test
	@DisplayName("Ensures that the api returns OK status code 200")
	public void ensureThatUserAPICallReturnStatusCode200() throws Exception {
		String scopes = "com.webage.data.apis";
		String token_string = createToken(scopes);

		HttpClient client = HttpClient.newBuilder().build();
		// Note the extra / at the end of customers is required if your API test fails
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/customers/"))
				.header("Authorization", "Bearer " + token_string).build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		assertThat(response.statusCode()).isEqualTo(200);

	}

	@Test
	@DisplayName("Ensures that the content type starts with application/json")
	void ensureThatJsonIsReturnedAsContentType() throws Exception {
		HttpClient client = HttpClient.newBuilder().build();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/customers/1")).build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		Optional<String> firstValue = response.headers().firstValue("Content-Type");
		String string = firstValue.get();
		assertThat(string).startsWith("application/json");
	}

	@Test
	@DisplayName("Ensure that the JSON for the user id 1 contains a reference to steve")
	void ensureJsonContainsCustomerName() throws Exception {
		String scopes = "com.webage.data.apis";
		String token_string = createToken(scopes);
		HttpClient client = HttpClient.newBuilder().build();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/customers/1"))
				.header("Authorization", "Bearer " + token_string).build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		String body = response.body();
		// For easy to see the output
		System.out.println(body);
		// Change steve to the name of the first person in your database
		assertTrue(body.contains("name\":\"Bruce\""));

	}

	public static String createToken(String scopes) {

		try {
			Algorithm algorithm = Algorithm.HMAC256("secret");
			long fiveHoursInMillis = 1000 * 60 * 60 * 5;
			Date expireDate = new Date(System.currentTimeMillis() + fiveHoursInMillis);
			String token = JWT.create().withSubject("apiuser").withIssuer("me@me.com").withClaim("scopes", scopes)
					.withExpiresAt(expireDate).sign(algorithm);
			return token;
		} catch (JWTCreationException exception) {
			return null;
		}
	}

}