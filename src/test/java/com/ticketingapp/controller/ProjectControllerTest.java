package com.ticketingapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ticketingapp.dto.ProjectDTO;
import com.ticketingapp.dto.RoleDTO;
import com.ticketingapp.dto.UserDTO;
import com.ticketingapp.enums.Gender;
import com.ticketingapp.enums.Status;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String token = "eyJhbGciOiJIUzI1NiJ9.eyJmaXJzdE5hbWUiOiJhZG1pbiIsImxhc3ROYW1lIjoiYWRtaW4iLCJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJpZCI6MSwiZXhwIjoxNjE1MzcxNDQzLCJpYXQiOjE2MTUzMzU0NDMsInVzZXJuYW1lIjoiYWRtaW5AYWRtaW4uY29tIn0.Ps8_JJK6q3l1uUbQgoyUPb2IflGV5HQ_LtT4CqF_KTE";

    private static UserDTO userDTO;
    private static ProjectDTO projectDTO;

    @BeforeAll
    static void setUp() {
        userDTO = UserDTO.builder()
                .id(2L)
                .firstName("Mike")
                .lastName("Smith")
                .username("mike@gmail.com")
                .password("abc123")
                .confirmPassword("abc123")
                .role(new RoleDTO(2L, "Manager"))
                .gender(Gender.MALE)
                .build();

        projectDTO = ProjectDTO.builder()
                .projectCode("API1")
                .projectName("Api")
                .assignedManager(userDTO)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .projectDetail("Api Test")
                .projectStatus(Status.OPEN)
                .completeTaskCounts(0)
                .unfinishedTaskCounts(0)
                .build();
    }

    @Test
    public void givenNoToken_whenGetSecureRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/project/Api1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenToken_getAllProjects() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/project")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].projectCode").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].assignedManager.username").isNotEmpty());
    }

    @Test
    public void givenToken_createProject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/project")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .content(toJsonString(projectDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.projectCode").isNotEmpty());
    }

    @Test
    public void givenToken_updateProject() throws Exception {
        projectDTO.setId(2L);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/project")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .content(toJsonString(projectDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Project has been updated successfully"));
    }

    @Test
    public void givenToken_deleteProject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/project/" + projectDTO.getProjectCode())
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON)
                .content(toJsonString(projectDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    protected static String toJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}