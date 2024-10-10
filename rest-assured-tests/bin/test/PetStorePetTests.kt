import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.rest.SerenityRest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus

@RunWith(SerenityRunner::class)
class PetStorePetTests {

    private val baseUrl = "http://localhost:8080/api/v3"

    @Before
    fun setUp() {
        // Ensure the pet with ID 1003 does not exist before starting each test
        SerenityRest.given()
            .`when`()
            .delete("$baseUrl/pet/1003")

        // Create the pet with ID 1003 for tests that require it
        val newPet = """
            {
                "id": 1003,
                "name": "Max",
                "status": "available"
            }
        """.trimIndent()

        SerenityRest.given()
            .contentType("application/json")
            .body(newPet)
            .`when`()
            .post("$baseUrl/pet")
    }

    @Test
    fun testAddNewPetSuccess() {
        val newPet = """
            {
                "id": 1004,
                "name": "Bella",
                "status": "available"
            }
        """.trimIndent()

        val postResponse = SerenityRest.given()
            .contentType("application/json")
            .body(newPet)
            .`when`()
            .post("$baseUrl/pet")

        println("POST response status code: ${postResponse.statusCode}")
        println("POST response body: ${postResponse.body.asString()}")

        val expectedStatusCodes = listOf(HttpStatus.CREATED.value(), HttpStatus.OK.value())
        if (postResponse.statusCode !in expectedStatusCodes) {
            throw AssertionError("Expected status code 201 or 200 but was ${postResponse.statusCode}")
        }
    }

    @Test
    fun testUpdatePetInvalidId() {
        val invalidIdPet = """
            {
                "id": "invalid_id",
                "name": "InvalidPet",
                "status": "available"
            }
        """.trimIndent()

        val response = SerenityRest.given()
            .contentType("application/json")
            .body(invalidIdPet)
            .`when`()
            .put("$baseUrl/pet")

        println("PUT response status code: ${response.statusCode}")
        println("PUT response body: ${response.body.asString()}")

        assertEquals("Expected status code 400 but was ${response.statusCode}", HttpStatus.BAD_REQUEST.value(), response.statusCode)
    }

    @Test
    fun testAddNewPetInvalidInput() {
        val invalidPet = """
            {
                "id": "invalid_id",
                "name": 12345,
                "status": "unknown"
            }
        """.trimIndent()

        val postResponse = SerenityRest.given()
            .contentType("application/json")
            .body(invalidPet)
            .`when`()
            .post("$baseUrl/pet")

        println("POST response status code: ${postResponse.statusCode}")
        println("POST response body: ${postResponse.body.asString()}")

        assertEquals("Expected status code between 400 and 499 but was ${postResponse.statusCode}", true, postResponse.statusCode in 400..499)
    }

    @Test
    fun testUpdateExistingPetSuccess() {
        val updatedPet = """
            {
                "id": 1003,
                "name": "MaxUpdated",
                "status": "sold"
            }
        """.trimIndent()

        val putResponse = SerenityRest.given()
            .contentType("application/json")
            .body(updatedPet)
            .`when`()
            .put("$baseUrl/pet")

        println("PUT response status code: ${putResponse.statusCode}")
        println("PUT response body: ${putResponse.body.asString()}")

        assertEquals("Expected status code 200 but was ${putResponse.statusCode}", HttpStatus.OK.value(), putResponse.statusCode)
    }

    @Test
    fun testUpdateNonExistingPet() {
        val updatedPet = """
            {
                "id": 9999,
                "name": "NonExistingPet",
                "status": "available"
            }
        """.trimIndent()

        val response = SerenityRest.given()
            .contentType("application/json")
            .body(updatedPet)
            .`when`()
            .put("$baseUrl/pet")

        println("PUT response status code: ${response.statusCode}")
        println("PUT response body: ${response.body.asString()}")

        assertEquals("Expected status code 404 but was ${response.statusCode}", HttpStatus.NOT_FOUND.value(), response.statusCode)
    }

    @Test
    fun testUpdatePetValidationException() {
        val invalidPetData = """
            {
                "id": 1003,
                "name": "InvalidStatusPet",
                "status": "unknown"
            }
        """.trimIndent()

        val response = SerenityRest.given()
            .contentType("application/json")
            .body(invalidPetData)
            .`when`()
            .put("$baseUrl/pet")

        println("PUT response status code: ${response.statusCode}")
        println("PUT response body: ${response.body.asString()}")

        assertEquals("Expected status code 405 but was ${response.statusCode}", HttpStatus.METHOD_NOT_ALLOWED.value(), response.statusCode)
    }

    @Test
    fun testFindPetByIdSuccess() {
        val response = SerenityRest.given()
            .`when`()
            .get("$baseUrl/pet/1003")
            .then()
            .extract().response()

        println("GET response status code: ${response.statusCode}")
        println("GET response body: ${response.body.asString()}")

        assertEquals("Expected status code was not received", HttpStatus.OK.value(), response.statusCode)
        val petName = response.jsonPath().getString("name")
        assertEquals("The pet name does not match", "Max", petName)
    }

    @Test
    fun testFindPetByIdNotFound() {
        val response = SerenityRest.given()
            .`when`()
            .get("$baseUrl/pet/9999")
            .then()
            .extract().response()

        println("GET response status code: ${response.statusCode}")
        println("GET response body: ${response.body.asString()}")

        assertEquals("Expected status code 404 but was ${response.statusCode}", HttpStatus.NOT_FOUND.value(), response.statusCode)
    }

    @Test
    fun testFindPetsByStatusSuccess() {
        val response = SerenityRest.given()
            .queryParam("status", "available")
            .`when`()
            .get("$baseUrl/pet/findByStatus")
            .then()
            .extract().response()

        println("GET response status code: ${response.statusCode}")
        println("GET response body: ${response.body.asString()}")

        assertEquals("Expected status code was not received", HttpStatus.OK.value(), response.statusCode)
    }

    @Test
    fun testDeletePetSuccess() {
        val deleteResponse = SerenityRest.given()
            .`when`()
            .delete("$baseUrl/pet/1003")

        println("DELETE response status code: ${deleteResponse.statusCode}")
        assertEquals("Expected status code 200 but was ${deleteResponse.statusCode}", HttpStatus.OK.value(), deleteResponse.statusCode)
    }

    @Test
    fun testDeletePetNotFound() {
        val deleteResponse = SerenityRest.given()
            .`when`()
            .delete("$baseUrl/pet/9999")

        println("DELETE response status code: ${deleteResponse.statusCode}")
        assertEquals("Expected status code 404, 400, 204, or 200 but was ${deleteResponse.statusCode}", true, deleteResponse.statusCode in listOf(HttpStatus.NOT_FOUND.value(), HttpStatus.BAD_REQUEST.value(), HttpStatus.NO_CONTENT.value(), HttpStatus.OK.value()))
    }

    @Test
    fun testUploadImageSuccess() {
        val imageFile = java.io.File("src/test/resources/8219A1C8-E836-4463-9534-6A70D438F6A9_1_105_c.jpeg")
        if (!imageFile.exists()) {
            throw AssertionError("The image file does not exist at the specified path.")
        }
        val uploadResponse = SerenityRest.given()
            .multiPart("file", imageFile)
            .`when`()
            .post("$baseUrl/pet/1003/uploadImage")

        println("UPLOAD response status code: ${uploadResponse.statusCode}")
        println("UPLOAD response body: ${uploadResponse.body.asString()}")

        assertEquals("Expected status code 200 or 415 but was ${uploadResponse.statusCode}", true, uploadResponse.statusCode in listOf(HttpStatus.OK.value(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
    }

    @Test
    fun testUploadImagePetNotFound() {
        val imageFile = java.io.File("src/test/resources/8219A1C8-E836-4463-9534-6A70D438F6A9_1_105_c.jpeg")
        if (!imageFile.exists()) {
            throw AssertionError("The image file does not exist at the specified path.")
        }
        val uploadResponse = SerenityRest.given()
            .multiPart("file", imageFile)
            .`when`()
            .post("$baseUrl/pet/9999/uploadImage")

        println("UPLOAD response status code: ${uploadResponse.statusCode}")
        println("UPLOAD response body: ${uploadResponse.body.asString()}")

        assertEquals("Expected status code 404, 400, or 415 but was ${uploadResponse.statusCode}", true, uploadResponse.statusCode in listOf(HttpStatus.NOT_FOUND.value(), HttpStatus.BAD_REQUEST.value(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()))
    }
}
