package com.itm.space.backendresources.service;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTestIT {

    private final UserService userService;

    @Autowired
    public UserServiceTestIT(UserService userService) {
        this.userService = userService;
    }

    @MockBean
    private Keycloak keycloak;

    @MockBean
    private List<RoleRepresentation> roleRepresentations;

    @MockBean
    private List<GroupRepresentation> groupRepresentations;


    @Test
    public void createUserTest() throws Exception {
        UserRequest userRequest = new UserRequest("alexey", "aleksej.demesh.98@inbox.ru",
                "admin", "Alexey", "Demesh");

        Response response = Response.status(Response.Status.CREATED)
                .location(new URI("user_id"))
                .entity(null)
                .build();

        when(keycloak.realm(anyString())).thenReturn(mock(RealmResource.class));
        when(keycloak.realm(anyString()).users()).thenReturn(mock(UsersResource.class));
        when(keycloak.realm(anyString()).users().create(any())).thenReturn(response);

        userService.createUser(userRequest);

       assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void getUserById() {
        UserRepresentation user = new UserRepresentation();
        UUID id = UUID.randomUUID();
        user.setId(String.valueOf(id));
        user.setEmail("aleksej.demesh.98@inbox.ru");

        when(keycloak.realm(anyString())).thenReturn(mock(RealmResource.class));
        when(keycloak.realm(anyString()).users()).thenReturn(mock(UsersResource.class));
        when(keycloak.realm(anyString()).users().get(anyString())).thenReturn(mock(UserResource.class));
        when(keycloak.realm(anyString()).users().get(anyString()).toRepresentation()).thenReturn(user);
        when(keycloak.realm(anyString()).users().get(anyString()).roles()).thenReturn(mock(RoleMappingResource.class));
        when(keycloak.realm(anyString()).users().get(anyString()).roles().getAll()).thenReturn(mock(MappingsRepresentation.class));
        when(keycloak.realm(anyString()).users().get(anyString()).roles().getAll().getRealmMappings()).thenReturn(roleRepresentations);
        when(keycloak.realm(anyString()).users().get(anyString()).groups()).thenReturn(groupRepresentations);

        UserResponse response = userService.getUserById(id);
        assertEquals("aleksej.demesh.98@inbox.ru", response.getEmail());
    }
}
