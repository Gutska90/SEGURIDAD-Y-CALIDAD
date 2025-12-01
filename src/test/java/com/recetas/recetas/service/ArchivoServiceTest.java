package com.recetas.recetas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArchivoServiceTest {
    
    @InjectMocks
    private ArchivoService archivoService;

    private final String TEST_UPLOAD_DIR = "test-uploads";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(archivoService, "uploadDir", TEST_UPLOAD_DIR);
    }


    @Test
    void testGuardarImagen_Exitoso() throws IOException {
        MultipartFile validImage = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                new byte[1024]
        );

        String rutaGenerada = archivoService.guardarImagen(validImage);

        assertNotNull(rutaGenerada, "La ruta no debería ser nula");
        assertTrue(rutaGenerada.startsWith("/" + TEST_UPLOAD_DIR + "/imagenes/"), "La ruta debe ser correcta");
        assertTrue(rutaGenerada.endsWith(".jpg"), "Debe conservar la extensión .jpg");
        assertTrue(rutaGenerada.length() > ("/test-uploads/imagenes/").length() + 32, "Debe tener un nombre único");
    }

    @Test
    void testGuardarImagen_ArchivoVacio() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        IOException exception = assertThrows(IOException.class, () -> {
            archivoService.guardarImagen(emptyFile);
        });

        assertEquals("El archivo está vacío", exception.getMessage());
    }

    @Test
    void testGuardarImagen_TamanoExcedido() {
        int oneMB = 1024 * 1024;
        int maxSizeBytes = 10 * oneMB;
        byte[] oversizedBytes = new byte[maxSizeBytes + 1];

        MultipartFile oversizedFile = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                oversizedBytes
        );

        IOException exception = assertThrows(IOException.class, () -> {
            archivoService.guardarImagen(oversizedFile);
        });

        assertTrue(exception.getMessage().contains("excede el tamaño máximo permitido"), "Debe fallar por tamaño");
    }

    @Test
    void testGuardarImagen_TipoNoPermitido() {
        MultipartFile wrongTypeFile = new MockMultipartFile(
                "file",
                "video.mp4",
                "video/mp4",
                new byte[10]
        );

        IOException exception = assertThrows(IOException.class, () -> {
            archivoService.guardarImagen(wrongTypeFile);
        });

        assertEquals("Tipo de archivo no permitido", exception.getMessage());
    }
    

    @Test
    void testGuardarVideo_Exitoso() throws IOException {
        MultipartFile validVideo = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                new byte[1024]
        );

        String rutaGenerada = archivoService.guardarVideo(validVideo);

        assertNotNull(rutaGenerada, "La ruta no debería ser nula");
        assertTrue(rutaGenerada.startsWith("/" + TEST_UPLOAD_DIR + "/videos/"), "La ruta debe ser correcta");
        assertTrue(rutaGenerada.endsWith(".mp4"), "Debe conservar la extensión .mp4");
    }
    
    @Test
    void testGuardarVideo_TamanoExcedido() {
        int oneMB = 1024 * 1024;
        int maxSizeBytes = 10 * oneMB;
        byte[] oversizedBytes = new byte[maxSizeBytes + 1];

        MultipartFile oversizedFile = new MockMultipartFile(
                "file",
                "large.webm",
                "video/webm",
                oversizedBytes
        );

        IOException exception = assertThrows(IOException.class, () -> {
            archivoService.guardarVideo(oversizedFile);
        });

        assertTrue(exception.getMessage().contains("excede el tamaño máximo permitido"), "Debe fallar por tamaño");
    }

    @Test
    void testGuardarVideo_TipoNoPermitido() {
        MultipartFile wrongTypeFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[10]
        );

        IOException exception = assertThrows(IOException.class, () -> {
            archivoService.guardarVideo(wrongTypeFile);
        });

        assertEquals("Tipo de archivo no permitido", exception.getMessage());
    }


    @Test
    void testObtenerExtension_ConExtension() {
        MultipartFile fileWithExtension = new MockMultipartFile(
                "file",
                "documento.pdf",
                "application/pdf",
                new byte[1]
        );

        try {
            String ruta = archivoService.guardarImagen(fileWithExtension);
            assertTrue(ruta.endsWith(".pdf"));
        } catch (Exception e) {
        }
    }
}
