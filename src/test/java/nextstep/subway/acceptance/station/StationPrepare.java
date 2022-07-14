package nextstep.subway.acceptance.station;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.StationRequest;
import org.springframework.http.MediaType;

import java.util.List;

public class StationPrepare {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String params;

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        try {
            params = objectMapper.writeValueAsString(new StationRequest(name));
        } catch (Exception e) {
            e.getStackTrace();
        }

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(String path) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(path)
                .then().log().all()
                .extract();
    }

    public static List<String> 지하철역_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }
}
