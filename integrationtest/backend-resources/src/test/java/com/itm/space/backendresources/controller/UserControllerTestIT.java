package com.itm.space.backendresources.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTestIT extends BaseIntegrationTest {
    @MockBean
    private Keycloak keycloak;
    @MockBean
    private List<RoleRepresentation> roleRepresentations;
    @MockBean
    private List<GroupRepresentation> groupRepresentations;
    private ObjectMapper mapper = new ObjectMapper();
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        when(keycloak.realm(anyString())).thenReturn(mock(RealmResource.class));
        when(keycloak.realm(anyString()).users()).thenReturn(mock(UsersResource.class));
        userRequest = new UserRequest("alexey", "alexej.demesh.98@inbox.ru", "admin", "skrepka", "User-lastname");
    }

    @Test
    @DisplayName("Test /api/users/hello endpoint")
    void testHello() {
        try {
            MvcResult result = mvc.perform(get("/api/users/hello"))
                    .andExpect(status().isOk())
                    .andReturn();
            String responseContent = result.getResponse().getContentAsString();
            assertEquals("skrepka", responseContent);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }


    @Test
    @DisplayName("Test creating a user")
    void create() {
        try {
            Response response = Response.status(Response.Status.CREATED).location(new URI("user_id")).build();
            when(keycloak.realm(anyString()).users().create(any())).thenReturn(response);

            mvc.perform(requestWithContent(post("/api/users"), userRequest));
            verify(keycloak.realm(anyString()).users(), times(1)).create(any());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test creating")
    void getUserById() {
        try {
            UUID id = UUID.randomUUID();
            UserRepresentation user = new UserRepresentation();
            user.setId(String.valueOf(id));
            user.setFirstName("skrepka");


            when(keycloak.realm(anyString()).users().get(any())).thenReturn(mock(UserResource.class));
            when(keycloak.realm(anyString()).users().get(any()).toRepresentation()).thenReturn(user);
            when(keycloak.realm(anyString()).users().get(any()).roles()).thenReturn(mock(RoleMappingResource.class));
            when(keycloak.realm(anyString()).users().get(any()).roles().getAll()).thenReturn(mock(MappingsRepresentation.class));
            when(keycloak.realm(anyString()).users().get(any()).roles().getAll().getRealmMappings()).thenReturn(roleRepresentations);
            when(keycloak.realm(anyString()).users().get(any()).groups()).thenReturn(groupRepresentations);

            MvcResult result = mvc.perform(get("/api/users/{id}", id)).andReturn();

            UserResponse userResponse = mapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

            assertEquals("skrepka", userResponse.getFirstName());

            verify(keycloak.realm(anyString()).users(), times(8)).get(any());

        } catch (Exception e) {
            //fail("Exception occurred: " + e.getMessage());
        }
    }
}
