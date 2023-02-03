package subway;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 노선 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class LineAcceptanceTest {

    @BeforeEach
    void setUp() {
        Map<String, String> firstParam = new HashMap<>();
        firstParam.put("name", "강남역");
        Map<String, String> secondParam = new HashMap<>();
        secondParam.put("name", "덕소역");

        StationAcceptanceTest.지하철역_생성(firstParam);
        StationAcceptanceTest.지하철역_생성(secondParam);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다")
    @Test
    void 지하철_노선을_생성하면_목록_조회_시_생성한_노선을_찾을_수_있다() {
        // When
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 10);

        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();

        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();

        // Then
        List<String> lineNames = response.jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void 지하철_노선을_2개_생성하고_목록을_조회하면_2개의_노선을_찾을_수_있다() {
        // Given
        Map<String, Object> firstParams = new HashMap<>();
        firstParams.put("name", "신분당선");
        firstParams.put("color", "bg-red-600");
        firstParams.put("upStationId", 1);
        firstParams.put("downStationId", 2);
        firstParams.put("distance", 10);

        Map<String, Object> secondParams = new HashMap<>();
        secondParams.put("name", "경의중앙선");
        secondParams.put("color", "bg-teal-300");
        secondParams.put("upStationId", 1);
        secondParams.put("downStationId", 2);
        secondParams.put("distance", 5);

        RestAssured.given().log().all()
            .body(firstParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();

        RestAssured.given().log().all()
            .body(secondParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();

        // When
        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();

        // Then
        List<String> lines = response.jsonPath()
            .getList("$");
        assertThat(lines).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선을_생성하고_생성한_노선을_조회하면_생성한_노선_정보를_응답받는다() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 10);

        Long lineId = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract().jsonPath().getLong("id");

        // When
        ExtractableResponse<Response> response =
            RestAssured.given()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();

        // Then
        String lineName = response.jsonPath().getString("name");
        assertThat(lineName).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 지하철_노선을_생성하고_생성한_노선을_수정하면_해당_노선_정보는_수정된다() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 10);

        Long lineId = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract().jsonPath().getLong("id");

        // When
        Map<String, String> changedLineNameParam = new HashMap<>();
        changedLineNameParam.put("name", "경의중앙선");

        ExtractableResponse<Response> response =
            RestAssured.given()
                .body(changedLineNameParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .when().put("/lines/" + lineId)
                .then().log().all()
                .extract();

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선을_생성하고_생성한_노선을_삭제하면_해당_노선_정보는_삭제된다() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 10);

        Long lineId = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract().jsonPath().getLong("id");

        // When
        ExtractableResponse<Response> response =
            RestAssured.given()
                .queryParam("id", lineId)
                .log().all()
                .when().delete("/lines")
                .then().log().all()
                .extract();

        // Then
        List<Long> ids = response.jsonPath().getList("id", Long.class);
        assertThat(ids).doesNotContain(lineId);
    }
}
