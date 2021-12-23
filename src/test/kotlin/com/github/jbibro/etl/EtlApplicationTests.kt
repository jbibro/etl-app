package com.github.jbibro.etl

import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForObject
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EtlApplicationTests {

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Test
	fun `returns selected metrics`() {
		assertEquals(
			"""
				[
					{
						"max clicks": 8894,
						"sum impressions": 51047032,
						"avg ctr": 0.08786307201137492
					}
				]
			""",
			restTemplate.getForObject<String>(
				"/analyze/?avg=ctr&sum=impressions&max=clicks"
			),
			false
		)
	}

	@Test
	fun `returns ctr per datasource`() {
		assertEquals(
			"""
				[
					{
						"avg ctr": 0.009306593619697533,
						"datasource": "Google Ads"
					},
					{
						"avg ctr": 0.11593188703961742,
						"datasource": "Twitter Ads"
					},
					{
						"avg ctr": 0.06220906933955774,
						"datasource": "Facebook Ads"
					}
				]
			""",
			restTemplate.getForObject<String>(
				"/analyze/?avg=ctr&groupBy=datasource"
			),
			false
		)
	}

	@Test
	fun `returns total impressions for Google grouped by days`() {
		assertEquals(
			"""
				[
					{
						"sum impressions": 72729.0,
						"Daily": "2019-12-01"
					},
					{
						"sum impressions": 92440.0,
						"Daily": "2019-12-02"
					},
					{
						"sum impressions": 85156.0,
						"Daily": "2019-12-03"
					}
				]
			""",
			restTemplate.getForObject<String>(
				UriComponentsBuilder
					.fromPath("/analyze/")
					.queryParam("sum", "impressions")
					.queryParam("groupBy", "Daily")
					.queryParam("filters", "Datasource.eq.Google Ads")
					.queryParam("filters", "Daily.ge.2019-12-01")
					.queryParam("filters", "Daily.le.2019-12-03")
					.build()
					.toUriString()
			),
			false
		)
	}

}
