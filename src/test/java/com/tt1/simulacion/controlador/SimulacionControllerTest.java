package com.tt1.simulacion.controlador;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SimulacionControllerTest {

    @Autowired
    private MockMvc mvc;

    // ── POST /Email ────────────────────────────────────────────────────────

    @Test
    void emailDevuelve201() throws Exception {
        mvc.perform(post("/Email")
                .param("emailAddress", "test@test.com")
                .param("message", "hola"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.done").value(true));
    }

    // ── POST /Solicitud/Solicitar ──────────────────────────────────────────

    @Test
    void solicitarDevuelve201ConToken() throws Exception {
        mvc.perform(post("/Solicitud/Solicitar")
                .param("nombreUsuario", "juan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombreCriaturas\":[\"Alpha\"],\"cantidadesIniciales\":[2]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.done").value(true))
                .andExpect(jsonPath("$.tokenSolicitud").isNumber());
    }

    // ── GET /Solicitud/GetSolicitudesUsuario ───────────────────────────────

    @Test
    void getSolicitudesUsuarioDevuelve201() throws Exception {
        mvc.perform(get("/Solicitud/GetSolicitudesUsuario")
                .param("nombreUsuario", "juan"))
                .andExpect(status().isCreated());
    }

    @Test
    void getSolicitudesUsuarioNuevoDevuelveListaVacia() throws Exception {
        mvc.perform(get("/Solicitud/GetSolicitudesUsuario")
                .param("nombreUsuario", "usuarioNuevo"))
                .andExpect(status().isCreated())
                .andExpect(content().string("[]"));
    }

    // ── GET /Solicitud/ComprobarSolicitud ──────────────────────────────────

    @Test
    void comprobarSolicitudTokenInexistenteDevuelveVacia() throws Exception {
        mvc.perform(get("/Solicitud/ComprobarSolicitud")
                .param("nombreUsuario", "juan")
                .param("tok", "9999"))
                .andExpect(status().isCreated())
                .andExpect(content().string("[]"));
    }

    // ── POST /Resultados ───────────────────────────────────────────────────

    @Test
    void resultadosTokenInexistenteDevuelveNullEnData() throws Exception {
        mvc.perform(post("/Resultados")
                .param("nombreUsuario", "juan")
                .param("tok", "9999"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.done").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
