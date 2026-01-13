package lista4.backend;

import lista4.gameInterface.GameInputAdapter;
import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.PlayerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClientThreadTest {
    private Socket mockSocket;
    private ArrayList<GameInputAdapter> inAdapters;
    private ArrayList<GameOutputAdapter> outAdapters;
    private GameOutputAdapter mockGuiOutAdapter;

    @BeforeEach
    void setUp() throws IOException {
        mockSocket = mock(Socket.class);

        java.net.InetAddress fakeAddress = mock(java.net.InetAddress.class);
        when(fakeAddress.toString()).thenReturn("127.0.0.1-TEST");
        when(mockSocket.getInetAddress()).thenReturn(fakeAddress);

        // Przygotowanie mocków adapterów
        inAdapters = new ArrayList<>();
        inAdapters.add(mock(GameInputAdapter.class)); // Index 0: GUI
        inAdapters.add(mock(GameInputAdapter.class)); // Index 1: Konsola

        outAdapters = new ArrayList<>();
        mockGuiOutAdapter = mock(GameOutputAdapter.class);
        outAdapters.add(mockGuiOutAdapter); // Index 0: GUI
        outAdapters.add(mock(GameOutputAdapter.class)); // Index 1: Konsola
    }

    @Test
    void shouldSelectGUIAdapterAndRegisterPlayer() throws IOException {
        // Symulujemy dane od klienta: najpierw "GUI", potem "quit"
        String inputData = "GUI\nquit\n";
        InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();

        when(mockSocket.getInputStream()).thenReturn(inputStream);
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        ArrayList<PlayerColor> gamers = new ArrayList<>();
        ClientThread clientThread = new ClientThread(mockSocket, inAdapters,
                outAdapters, PlayerColor.BLACK, gamers);

        // Uruchamiamy metodę run() wątku
        clientThread.run();

        // Sprawdzamy, czy registerPlayer został wywołany na adapterze GUI (index 0)
        verify(mockGuiOutAdapter).registerPlayer(eq(PlayerColor.BLACK),
                any(PrintWriter.class));
    }
}