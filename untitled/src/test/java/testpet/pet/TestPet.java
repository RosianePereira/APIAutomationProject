package testpet.pet;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestPet {

    private static Pet pet;
    public static RequestSpecification request;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2/pet";

        // Definindo um Pet para os testes
        Category category = new Category(0, "dog");
        Tag tag = new Tag(0, "tag1");
        pet = new Pet(0, category, "doggie", new String[]{"photo1", "photo2"}, new Tag[]{tag}, "available");
    }

    @BeforeEach
    void setRequest() {
        request = given()
                .header("api-key", "special-key")
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(1)
    public void AddPetToTheStore_WithValidData_ReturnOk() {
        request
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .assertThat().statusCode(200)
                .and().body("code", equalTo(200))
                .and().body("type", equalTo("unknown"))
                .and().body("message", isA(String.class));
    }

    @Test
    @Order(2)
    public void UpdatePet_WithValidData_ReturnOk() {
        // Atualizando o Pet
        pet.setName("updatedDoggie");

        request
                .body(pet)
                .when()
                .put("/pet")
                .then()
                .assertThat().statusCode(200)
                .and().body("name", equalTo("updatedDoggie"))
                .and().body("status", equalTo("available"));
    }

    @Test
    @Order(3)
    public void DeletePet_WithValidPetId_ReturnOk() {
        // Deletando o Pet
        int petId = pet.getId();

        request
                .when()
                .delete("/pet/" + petId)
                .then()
                .assertThat().statusCode(200);
    }

    @Test
    public void AddPetToTheStore_WithInvalidBody_ReturnBadRequest() {
        Response response = request
                .body("invalid data")
                .when()
                .post("/pet")
                .then()
                .extract().response();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertEquals(true, response.getBody().asPrettyString().contains("unknown"));
    }
}
