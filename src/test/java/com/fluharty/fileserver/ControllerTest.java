package com.fluharty.fileserver;

import com.fluharty.fileserver.service.UserFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ControllerTest {

    @Mock
    private UserFileService userFileService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
            Controller controller = new Controller(userFileService);
            mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        }
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFiles() throws Exception {
        List<String> files = Arrays.asList("file1.txt", "file2.txt");

        when(userFileService.getFiles()).thenReturn(files);

        mockMvc.perform(get("/files")
                        .header("user", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("file1.txt"))
                .andExpect(jsonPath("$[1]").value("file2.txt"));

        verify(userFileService, times(1)).getFiles();
    }

    @Test
    void testDownloadFileSuccess() throws Exception {
        String filename = "file1.txt";
        byte[] fileData = "File content".getBytes();

        when(userFileService.getFiles()).thenReturn(Arrays.asList("file1.txt", "file2.txt"));
        when(userFileService.download(filename)).thenReturn(fileData);

        mockMvc.perform(get("/download")
                        .param("filename", filename)
                        .header("user", "testUser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(fileData));

        verify(userFileService, times(1)).getFiles();
        verify(userFileService, times(1)).download(filename);
    }

    @Test
    void testUploadFileSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "file3.txt", "text/plain", "File content".getBytes());

        when(userFileService.getFiles()).thenReturn(List.of("file1.txt"));
        doNothing().when(userFileService).upload(any(), any());

        mockMvc.perform(multipart("/upload")
                        .file(file)
                        .header("user", "testUser"))
                .andExpect(status().isCreated());

        verify(userFileService, times(1)).getFiles();
        verify(userFileService, times(1)).upload(any(), any());
    }

    @Test
    void testDeleteFileSuccess() throws Exception {
        String filename = "file1.txt";

        when(userFileService.getFiles()).thenReturn(Arrays.asList("file1.txt", "file2.txt"));
        doNothing().when(userFileService).delete(filename);

        mockMvc.perform(delete("/delete")
                        .param("filename", filename)
                        .header("user", "testUser"))
                .andExpect(status().isOk());

        verify(userFileService, times(1)).getFiles();
        verify(userFileService, times(1)).delete(filename);
    }
}
