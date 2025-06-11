package com.kosta.saladMan;


import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Arrays;

import com.kosta.saladMan.controller.hq.Inventroy.InventoryController;
import com.kosta.saladMan.dto.UserDto;
import com.kosta.saladMan.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InventoryControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() {
        // given
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("È«±æµ¿");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("±èÃ¶¼ö");

        when(userService.getUsers()).thenReturn(Arrays.asList(user1, user2));

        // when
        List<UserDto> users = inventoryController.getUsers();

        // then
        assertThat(users).hasSize(2);
        assertThat(users).extracting("name").containsExactly("È«±æµ¿", "±èÃ¶¼ö");

        verify(userService, times(1)).getUsers();
    }

    @Test
    public void testCreateUser() {
        // given
        UserDto inputDto = new UserDto();
        inputDto.setName("¹Ú¿µÈñ");

        UserDto returnedDto = new UserDto();
        returnedDto.setId(3L);
        returnedDto.setName("¹Ú¿µÈñ");

        when(userService.createUser(inputDto)).thenReturn(returnedDto);

        // when
        UserDto result = inventoryController.createUser(inputDto);

        // then
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("¹Ú¿µÈñ");

        verify(userService, times(1)).createUser(inputDto);
    }
}
