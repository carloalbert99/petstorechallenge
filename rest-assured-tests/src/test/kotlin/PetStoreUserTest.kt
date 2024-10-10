import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.rest.SerenityRest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus

@RunWith(SerenityRunner::class)
class PetStoreUserTests {

    private val baseUrl = "http://localhost:8080/api/v3"

    @Test
    fun testCreateUserSuccess() {
        val newUser = """
            {
                "id": 1,
                "username": "testuser",
                "firstName": "John",
                "lastName": "Doe",
                "email": "johndoe@example.com",
                "password": "password123",
                "phone": "123456789",
                "userStatus": 1
            }
        """.trimIndent()

        val postResponse = SerenityRest.given()
            .contentType("application/json")
            .body(newUser)
            .`when`()
            .post("$baseUrl/user")

        SerenityRest.then().log().all() // Log the entire response in Serenity reports
        assertEquals("Expected status code 200 but was ${postResponse.statusCode}", HttpStatus.OK.value(), postResponse.statusCode)
    }

    @Test
    fun testGetUserByUsernameSuccess() {
        // Create the user to ensure it exists
        testCreateUserSuccess()

        val getResponse = SerenityRest.given()
            .`when`()
            .get("$baseUrl/user/testuser")
            .then()
            .extract().response()

        SerenityRest.then().log().all() // Log the entire response in Serenity reports
        assertEquals("Expected status code 200 but was ${getResponse.statusCode}", HttpStatus.OK.value(), getResponse.statusCode)
        val username = getResponse.jsonPath().getString("username")
        assertEquals("The username does not match", "testuser", username)
    }

    @Test
    fun testUpdateUserSuccess() {
        // Create the user to update it
        testCreateUserSuccess()

        val updatedUser = """
            {
                "id": 1,
                "username": "testuser",
                "firstName": "Johnny",
                "lastName": "Doe",
                "email": "johnnydoe@example.com",
                "password": "newpassword123",
                "phone": "987654321",
                "userStatus": 1
            }
        """.trimIndent()

        val putResponse = SerenityRest.given()
            .contentType("application/json")
            .body(updatedUser)
            .`when`()
            .put("$baseUrl/user/testuser")

        SerenityRest.then().log().all() // Log the entire response in Serenity reports
        assertEquals("Expected status code 200 but was ${putResponse.statusCode}", HttpStatus.OK.value(), putResponse.statusCode)
    }

    @Test
    fun testDeleteUserSuccess() {
        // Create the user to delete it
        testCreateUserSuccess()

        val deleteResponse = SerenityRest.given()
            .`when`()
            .delete("$baseUrl/user/testuser")

        SerenityRest.then().log().all() // Log the entire response in Serenity reports
        assertEquals("Expected status code 200 but was ${deleteResponse.statusCode}", HttpStatus.OK.value(), deleteResponse.statusCode)
    }

    @Test
    fun testLoginUserSuccess() {
        // Create the user to test login
        testCreateUserSuccess()

        val loginResponse = SerenityRest.given()
            .queryParam("username", "testuser")
            .queryParam("password", "password123")
            .`when`()
            .get("$baseUrl/user/login")
            .then()
            .extract().response()

        SerenityRest.then().log().all() // Log the entire response in Serenity reports
        assertEquals("Expected status code 200 but was ${loginResponse.statusCode}", HttpStatus.OK.value(), loginResponse.statusCode)
    }

    @Test
    fun testLogoutUserSuccess() {
        val logoutResponse = SerenityRest.given()
            .`when`()
            .get("$baseUrl/user/logout")

        SerenityRest.then().log().all() // Log the entire response in Serenity reports
        assertEquals("Expected status code 200 but was ${logoutResponse.statusCode}", HttpStatus.OK.value(), logoutResponse.statusCode)
    }

    @Test
    fun testLoginUserInvalidCredentials() {
        val loginResponse = SerenityRest.given()
            .queryParam("username", "baduser")
            .queryParam("password", "badpsw123")
            .`when`()
            .get("$baseUrl/user/login")
            .then()
            .extract().response()

        SerenityRest.then().log().all() // Log the entire response in Serenity reports
        assertEquals("Expected status code 400 but was ${loginResponse.statusCode}", HttpStatus.BAD_REQUEST.value(), loginResponse.statusCode)
    }

    @Test
    fun testGetUserInvalidUsername() {
        val getResponse = SerenityRest.given()
            .`when`()
            .get("$baseUrl/user/invalid!username")
            .then()
            .extract().response()

        SerenityRest.then().log().all() // Log the entire response in Serenity reports
        assertEquals("Expected status code 400 but was ${getResponse.statusCode}", HttpStatus.BAD_REQUEST.value(), getResponse.statusCode)
    }

    @Test
    fun testGetUserNotFound() {
        val getResponse = SerenityRest.given()
            .`when`()
            .get("$baseUrl/user/nonexistentuser")
            .then()
            .extract().response()

        SerenityRest.then().log().all() // Log the entire response in Serenity reports
        assertEquals("Expected status code 404 but was ${getResponse.statusCode}", HttpStatus.NOT_FOUND.value(), getResponse.statusCode)
    }
}
