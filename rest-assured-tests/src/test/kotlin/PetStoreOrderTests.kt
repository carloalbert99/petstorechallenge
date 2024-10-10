import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.rest.SerenityRest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus

@RunWith(SerenityRunner::class)
class PetStoreOrderTests {

    private val baseUrl = "http://localhost:8080/api/v3"

    @Test
    fun testGetInventorySuccess() {
        val response = SerenityRest.given()
            .header("accept", "application/json")
            .`when`()
            .get("$baseUrl/store/inventory")
            .then()
            .extract().response()

        println("GET inventory response status code: ${response.statusCode}")
        println("GET inventory response body: ${response.body.asString()}")

        assertEquals("Expected status code 200 but was ${response.statusCode}", HttpStatus.OK.value(), response.statusCode)
        assert(response.jsonPath().getMap<Any, Any>(".").isNotEmpty()) { "Inventory response should not be empty. Response body: ${response.body.asString()}" }
    }

    @Test
    fun testPlaceOrderSuccess() {
        val newOrder = """
            {
                "id": 10,
                "petId": 198772,
                "quantity": 7,
                "shipDate": "2024-10-10T12:00:00.000Z",
                "status": "placed",
                "complete": true
            }
        """.trimIndent()

        val postResponse = SerenityRest.given()
            .contentType("application/json")
            .body(newOrder)
            .`when`()
            .post("$baseUrl/store/order")
            .then()
            .extract().response()

        println("POST order response status code: ${postResponse.statusCode}")
        println("POST order response body: ${postResponse.body.asString()}")

        assert(listOf(HttpStatus.OK.value(), HttpStatus.CREATED.value()).contains(postResponse.statusCode)) {
            "Expected status code 200 or 201 but was ${postResponse.statusCode}"
        }
    }

@Test
    fun testPlaceOrderInvalidFormat() {
        val invalidOrder = """
            {
                "id": "invalid",
                "petId": 198772,
                "quantity": "seven",
                "shipDate": "invalid-date",
                "status": "unknown",
                "complete": "maybe"
            }
        """.trimIndent()

        val postResponse = SerenityRest.given()
            .contentType("application/json")
            .body(invalidOrder)
            .`when`()
            .post("$baseUrl/store/order")
            .then()
            .extract().response()

        println("POST order with invalid format response status code: ${postResponse.statusCode}")
        println("POST order with invalid format response body: ${postResponse.body.asString()}")

        assertEquals("Expected status code 400 but was ${postResponse.statusCode}", HttpStatus.BAD_REQUEST.value(), postResponse.statusCode)
    }

@Test
    fun testGetOrderByIdSuccess() {
        val orderId = 10

        val response = SerenityRest.given()
            .header("accept", "application/json")
            .`when`()
            .get("$baseUrl/store/order/$orderId")
            .then()
            .extract().response()

        println("GET order by ID response status code: ${response.statusCode}")
        println("GET order by ID response body: ${response.body.asString()}")

        assertEquals("Expected status code 200 but was ${response.statusCode}", HttpStatus.OK.value(), response.statusCode)
        assertEquals("Expected order ID $orderId but was ${response.jsonPath().getLong("id")}", orderId.toLong(), response.jsonPath().getLong("id"))
    }

   @Test
    fun testGetOrderByIdNotFound() {
        val orderId = 9999

        val response = SerenityRest.given()
            .header("accept", "application/json")
            .`when`()
            .get("$baseUrl/store/order/$orderId")
            .then()
            .extract().response()

        println("GET order by invalid ID response status code: ${response.statusCode}")
        println("GET order by invalid ID response body: ${response.body.asString()}")

        assertEquals("Expected status code 404 but was ${response.statusCode}", HttpStatus.NOT_FOUND.value(), response.statusCode)
    }

 @Test
    fun testGetOrderByIdInvalidRange() {
        for (orderId in 6..9) {
            val response = SerenityRest.given()
                .header("accept", "application/json")
                .`when`()
                .get("$baseUrl/store/order/$orderId")
                .then()
                .extract().response()

            println("GET order by ID ($orderId) response status code: ${response.statusCode}")
            println("GET order by ID ($orderId) response body: ${response.body.asString()}")

            if (response.statusCode == HttpStatus.BAD_REQUEST.value()) {
                assertEquals("Expected status code 400 for invalid ID but was ${response.statusCode}", HttpStatus.BAD_REQUEST.value(), response.statusCode)
            } else {
                assertEquals("Expected status code 404 for not found but was ${response.statusCode}", HttpStatus.NOT_FOUND.value(), response.statusCode)
            }
        }
    }

@Test
    fun testDeleteOrderByIdSuccess() {
        val orderId = 11

        val response = SerenityRest.given()
            .`when`()
            .delete("$baseUrl/store/order/$orderId")
            .then()
            .extract().response()

        println("DELETE order by ID response status code: ${response.statusCode}")

        assertEquals("Expected status code 200 but was ${response.statusCode}", HttpStatus.OK.value(), response.statusCode)
    }

      @Test
    fun testDeleteOrderByIdInvalidId() {
        val orderId = 1001

        val response = SerenityRest.given()
            .`when`()
            .delete("$baseUrl/store/order/$orderId")
            .then()
            .extract().response()

        println("DELETE order by invalid ID response status code: ${response.statusCode}")

        assertEquals("Expected status code 400 but was ${response.statusCode}", HttpStatus.BAD_REQUEST.value(), response.statusCode)
    }

    @Test
    fun testDeleteOrderByIdNotFound() {
        val orderId = 999

        val response = SerenityRest.given()
            .`when`()
            .delete("$baseUrl/store/order/$orderId")
            .then()
            .extract().response()

        println("DELETE order by non-existing ID response status code: ${response.statusCode}")

        assertEquals("Expected status code 404 but was ${response.statusCode}", HttpStatus.NOT_FOUND.value(), response.statusCode)
    }

}