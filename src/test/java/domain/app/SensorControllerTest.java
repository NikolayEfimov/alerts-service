package domain.app;

import app.SensorController;
import app.SensorService;
import app.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import app.config.LocalDateTimeDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorController.class)
public class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorService sensorService;

    @Test
    public void testCollectSensorMeasurement() throws Exception {
        var uuid = UUID.randomUUID();
        var measurementRequest = new MeasurementRequest(2000, LocalDateTime.now());
        var om = new ObjectMapper();
        om.findAndRegisterModules();

        mockMvc.perform(post("/api/v1/sensors/{uuid}/measurements", uuid)
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(measurementRequest)))
                .andExpect(status().isOk());

        verify(sensorService, times(1)).addMeasurement(eq(uuid), any(MeasurementRequest.class));
    }

    @Test
    public void testGetSensorStatus() throws Exception {
        var uuid = UUID.randomUUID();
        var expectedResponse = new SensorStatusResponse(SensorStatus.OK);

        when(sensorService.getSensorStatus(uuid)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/sensors/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    public void testGetSensorMetrics() throws Exception {
        var uuid = UUID.randomUUID();
        var expectedResponse = new SensorMetricsResponse(1000, 2000);

        when(sensorService.getSensorMetrics(uuid)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/sensors/{uuid}/metrics", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxLast30Days", is(1000)))
                .andExpect(jsonPath("$.avgLast30Days", is(2000.0)));
    }

    @Test
    public void testGetSensorAlerts() throws Exception {
        var uuid = UUID.randomUUID();
        var expectedResponse = Arrays.asList(
                new AlertResponse(LocalDateTime.now(), LocalDateTime.now(), 2100, 2200, 2300),
                new AlertResponse(LocalDateTime.now(), LocalDateTime.now(), 2200, 2300, 2400)
        );

        when(sensorService.getSensorAlerts(uuid)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/sensors/{uuid}/alerts", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].measurement1", is(2100)))
                .andExpect(jsonPath("$[0].measurement2", is(2200)))
                .andExpect(jsonPath("$[0].measurement3", is(2300)))
                .andExpect(jsonPath("$[1].measurement1", is(2200)))
                .andExpect(jsonPath("$[1].measurement2", is(2300)))
                .andExpect(jsonPath("$[1].measurement3", is(2400)));
    }

    @Test
    public void testLocalDateTimeSerializationWithObjectMapper() throws Exception {
        String dateTimeString = "\"2019-02-01T18:55:47+00:00\"";
        var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(WRITE_DATES_AS_TIMESTAMPS);

        var module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        objectMapper.registerModule(module);

        var dateTime = objectMapper.readValue(dateTimeString, LocalDateTime.class);

        assertThat(dateTime).isNotNull();
        assertThat(dateTime.getYear()).isEqualTo(2019);
        assertThat(dateTime.getMonthValue()).isEqualTo(2);
        assertThat(dateTime.getDayOfMonth()).isEqualTo(1);
        assertThat(dateTime.getHour()).isEqualTo(18);
        assertThat(dateTime.getMinute()).isEqualTo(55);
        assertThat(dateTime.getSecond()).isEqualTo(47);
    }
}

