package com.batuhaniskr.product.controller;

import com.batuhaniskr.product.model.User;
import com.batuhaniskr.product.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder; // Requerido ya que el controlador lo inyecta

    @Test
    @WithMockUser
    public void testSignUp_Success() throws Exception {
        // 1. ARRANGE (Preparar datos y simular comportamiento del encriptador)
        User user = new User();
        user.setEmail("samir@final.com");
        user.setPassword("password123");

        Mockito.when(bCryptPasswordEncoder.encode("password123")).thenReturn("encryptedPasswordXYZ");
        // No necesitamos "when" para userService.saveUser porque devuelve void

        // JSON que enviaremos en el cuerpo de la petición
        String userJson = "{\"email\":\"samir@final.com\",\"password\":\"password123\"}";

        // 2. ACT & 3. ASSERT (Ejecutar POST con CSRF habilitado y verificar código 200 OK)
        mockMvc.perform(post("/api/users")
                        .with(csrf()) // Evita bloqueos de seguridad por falta de token CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        // Verificación adicional: asegurar que el servicio realmente intentó guardar el usuario
        Mockito.verify(userService, Mockito.times(1)).saveUser(Mockito.any(User.class));
    }
}