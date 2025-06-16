package com.kosta.saladMan;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Arrays;

import com.kosta.saladMan.controller.hq.inventroy.HqInventoryController;
import com.kosta.saladMan.dto.store.UserDto;
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
    private HqInventoryController inventoryController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() {
        // given
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("홍길동");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("김철수");

        when(userService.getUsers()).thenReturn(Arrays.asList(user1, user2));

        // when
        List<UserDto> users = inventoryController.getUsers();

        // then
        assertThat(users).hasSize(2);
        assertThat(users).extracting("name").containsExactly("홍길동", "김철수");

        verify(userService, times(1)).getUsers();
    }

    @Test
    public void testCreateUser() {
        // given
        UserDto inputDto = new UserDto();
        inputDto.setName("박영희");

        UserDto returnedDto = new UserDto();
        returnedDto.setId(3L);
        returnedDto.setName("박영희");

        when(userService.createUser(inputDto)).thenReturn(returnedDto);

        // when
        UserDto result = inventoryController.createUser(inputDto);

        // then
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("박영희");

        verify(userService, times(1)).createUser(inputDto);
    }
}
