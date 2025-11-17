package com.yzlaboratory.api_configs;

import com.yzlaboratory.api_configs.controller.ConfigController;
import com.yzlaboratory.api_configs.entity.Config;
import com.yzlaboratory.api_configs.service.DynamoDbService;
import com.yzlaboratory.api_configs.service.UUIDService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit-Tests für den ConfigController.
 * @WebMvcTest lädt nur die Web-Schicht (den Controller).
 */
@WebMvcTest(ConfigController.class)
public class ConfigControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DynamoDbService mockDynamoDbService;

	@Test
	void testStatusEndpoint() throws Exception {
		//Arrange
		String expectedResult = "<h1>Hello World, its me the Status Controller of your friend api-configs</h1>";
		//Act and Assert
		mockMvc.perform(get("/configs/status"))
				.andExpect(status().isOk())
				.andExpect(content().string(expectedResult));
	}

	/**
	 * Testet den GET /configs/{configId} Endpunkt.
	 */
	@Test
	void testGetConfigById() throws Exception {
		// Arrange
		String testId = "3ddc92d7-b498-460b-b223-71166501fe1f";
		String testModelId = "Test Model_1234";
		Config mockConfig = new Config();
		String[] configArray = new String[] {"E-1", "C-1"};
		BigDecimal price = BigDecimal.valueOf(123.0);
		mockConfig.setConfig(List.of(configArray));
		mockConfig.setConfigId(testId);
		mockConfig.setModelId(testModelId);
		mockConfig.setPrice(price);

		when(mockDynamoDbService.getConfigById(testId)).thenReturn(mockConfig);

		// Act & Assert
		mockMvc.perform(get("/configs/" + testId))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.configId", is(testId)))
				.andExpect(jsonPath("$.modelId", is(testModelId)))
				.andExpect(jsonPath("$.config").isArray())
				.andExpect(jsonPath("$.config.length()", is(2)))
				.andExpect(jsonPath("$.config[0]", is("E-1")))
				.andExpect(jsonPath("$.config[1]", is("C-1")))
				.andExpect(jsonPath("$.price").value(price));
	}

	/**
	 * Testet den POST /configs Endpunkt.
	 * Dieser Test mockt auch den statischen Aufruf an UUIDService.
	 */
	@Test
	void testPostConfig() throws Exception {
		// Arrange
		String testUUID = "new-uuid-12345";
		String modelId = "Astral X_2025";
		String[] configArray = new String[] {"E-1", "C-1"};
		String price = "213124.22";
		String inputJson = "{\"configId\": \"\",\"modelId\": \"Astral X_2025\",\"config\": [\"E-1\",\"C-1\"],\"price\": 213124.22}";
		
		try (MockedStatic<UUIDService> mockedUuidService = Mockito.mockStatic(UUIDService.class)) {

			mockedUuidService.when(UUIDService::getUUID).thenReturn(testUUID);

			// Mocken der saveConfig-Methode (sie gibt void zurück)
			doNothing().when(mockDynamoDbService).saveConfig(any(Config.class));

			// Ein ArgumentCaptor, um zu prüfen, was an den Service gesendet wurde
			ArgumentCaptor<Config> configCaptor = ArgumentCaptor.forClass(Config.class);

			// Act & Assert
			mockMvc.perform(post("/configs")
							.contentType(MediaType.APPLICATION_JSON)
							.content(inputJson))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.modelId", is(modelId)))
					.andExpect(jsonPath("$.configId", is(testUUID)))
					.andExpect(jsonPath("$.price").value(price))
					.andExpect(jsonPath("$.config.length()", is(configArray.length)));

			// Stellen Sie sicher, dass mockDynamoDbService.saveConfig aufgerufen wurde
			verify(mockDynamoDbService).saveConfig(configCaptor.capture());
			// Überprüfen Sie, ob das an den Service übergebene Objekt
			// die UUID enthält, die wir gemockt haben.
			Config capturedConfig = configCaptor.getValue();
			assertThat(capturedConfig.getConfigId()).isEqualTo(testUUID);
			assertThat(capturedConfig.getModelId()).isEqualTo(modelId);
		}
	}
}