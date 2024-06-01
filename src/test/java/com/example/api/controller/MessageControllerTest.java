package com.example.api.controller;

import com.example.api.exception.MessageNotFoundException;
import com.example.api.model.Message;
import com.example.api.service.MessageService;
import com.example.api.utils.MessageHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MessageControllerTest {

    private MockMvc mockMVC;

    @Mock
    private MessageService messageService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        MessageController messageController = new MessageController(messageService);
        mockMVC = MockMvcBuilders.standaloneSetup(messageController)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    class RegisterMessageTest {
        @Test
        void shouldAllowRegisterMessage() throws Exception {
            // Arrange
            var message = MessageHelper.createMessage();
            when(messageService.registerMessage(any(Message.class)))
                    .thenAnswer(index -> index.getArgument(0));

            // Act & Assert
            mockMVC.perform(post("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(message))
            ).andExpect(status().isCreated());
            verify(messageService, times(1)).registerMessage(any(Message.class));
        }

        @Test
        void shouldThrowExceptionWhenMessagePayloadIsTypeXML() throws Exception {
            // Arrange
            String xmlPayload = """
            <message><username>Name</username><content>Hello!</content></message>
            """;

            // Act & Assert
            mockMVC.perform(post("/messages")
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(messageService, never())
                    .registerMessage(any(Message.class));
        }
    }

    @Nested
    class GetMessageTest {
        @Test
        void shouldAllowGetMessage() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            var newMessage = MessageHelper.createMessage();
            when(messageService.getMessage(any(UUID.class)))
                    .thenReturn(newMessage);

            // Act & Assert
            mockMVC.perform(get("/messages/{id}", id))
                    .andExpect(status().isOk());
            verify(messageService, times(1)).getMessage(any(UUID.class));
        }
        @Test
        void shouldThrowExceptionWhenGetIfMessageIdNotFound() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            when(messageService.getMessage(id))
                    .thenThrow(MessageNotFoundException.class);
            // Act
            mockMVC.perform(get("/messages/{id}", id))
                    .andExpect(status().isBadRequest());
            verify(messageService, times(1)).getMessage(id);

        }
    }

    @Nested
    class UpdateMessageTest {
        @Test
        void shouldAllowUpdateMessage() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            var newMessage = MessageHelper.createMessage();
            newMessage.setId(id);
            when(messageService.updateMessage(any(UUID.class), any(Message.class)))
                    .thenAnswer(index -> index.getArgument(1));

            // Act & Assert
            mockMVC.perform(put("/messages/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(newMessage))
                    ).andExpect(status().isOk());
            verify(messageService, times(1))
                    .updateMessage(id, newMessage);
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdNotFound() {
            fail("NotImplementedError");
        }

        @Test
        void shouldThrowExceptionWhenUpdateIfMessageIdIsNotEqual() {
            fail("NotImplementedError");
        }

        @Test
        void shouldThrowExceptionWhenMessagePayloadIsTypeXML() throws Exception {
            // Arrange
            var id = UUID.randomUUID();
            String xmlPayload = "<message><id>" + id + "</id><username>Name</username><content>Hello!</content></message> ";

            // Act & Assert
            mockMVC.perform(put("/messages/{id}", id)
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(messageService, never())
                    .updateMessage(any(UUID.class),any(Message.class));
        }

    }

    @Nested
    class DeleteMessageTest {
        @Test
        void shouldAllowDeleteMessage() {
            fail("NotImplementedError");
        }
        @Test
        void shouldThrowExceptionWhenDeleteIfMessageIdNotFound() {
            fail("NotImplementedError");
        }
    }

    @Nested
    class ListMessagesTest {
        @Test
        void shouldAllowListMessages() {
            fail("NotImplementedError");
        }
    }
}
