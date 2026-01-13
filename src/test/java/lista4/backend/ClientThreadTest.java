package lista4.backend;

import lista4.gameInterface.GameInputAdapter;
import lista4.gameInterface.GameOutputAdapter;
import lista4.gameLogic.GameManager;
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

/**
 * Test class for ClientThread.
 * Verifies the correctness of the server-client communication logic,
 * including the handshake process, selection of appropriate adapters,
 * and forwarding of commands to the game logic.
 */
class ClientThreadTest {

    /** Mocked client socket. */
    private Socket mockSocket;

    /** List of mocked input adapters (index 0: GUI, index 1: Console). */
    private ArrayList<GameInputAdapter> inAdapters;

    /** List of mocked output adapters (index 0: GUI, index 1: Console). */
    private ArrayList<GameOutputAdapter> outAdapters;

    /** Specific GUI output adapter mock used for interaction verification. */
    private GameOutputAdapter mockGuiOutAdapter;

    /**
     * Initializes the test environment before each test case.
     * Creates mocks for the socket, streams, and adapter lists.
     * Configures a fake IP address for the test connection.
     *
     * @throws IOException if an error occurs during input/output stream simulation.
     */
    @BeforeEach
    void setUp() throws IOException {
        mockSocket = mock(Socket.class);

        java.net.InetAddress fakeAddress = mock(java.net.InetAddress.class);
        when(fakeAddress.toString()).thenReturn("127.0.0.1-TEST");
        when(mockSocket.getInetAddress()).thenReturn(fakeAddress);

        inAdapters = new ArrayList<>();
        inAdapters.add(mock(GameInputAdapter.class)); // Index 0: GUI
        inAdapters.add(mock(GameInputAdapter.class)); // Index 1: Console

        outAdapters = new ArrayList<>();
        mockGuiOutAdapter = mock(GameOutputAdapter.class);
        outAdapters.add(mockGuiOutAdapter); // Index 0: GUI
        outAdapters.add(mock(GameOutputAdapter.class)); // Index 1: Console
    }

    /**
     * Tests the scenario where a client selects the GUI interface and requests the
     * board state.
     * This test verifies:
     * 1. Whether ClientThread correctly recognizes the GUI handshake command.
     * 2. Whether the player is registered in the correct output adapter.
     * 3. Whether the GETBOARD command is forwarded to the appropriate input
     * adapter.
     * 4. Whether the process terminates correctly upon receiving the quit command.
     *
     * @throws IOException if an error occurs during operations on the mocked
     *                     socket.
     */
    @Test
    void shouldSelectGUIAdapterAndRegisterPlayer() throws IOException {
        // Mocking GameManager instead of using the singleton instance
        GameManager mockGameManager = mock(GameManager.class);

        // Simulating data: first GUI selection, then board request, then exit
        String inputData = "GUI\nGETBOARD\nquit\n";
        InputStream inputStream = new ByteArrayInputStream(inputData.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();

        when(mockSocket.getInputStream()).thenReturn(inputStream);
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        // Player list with one registered color
        ArrayList<PlayerColor> gamers = new ArrayList<>();
        gamers.add(PlayerColor.BLACK);

        ClientThread clientThread = new ClientThread(
                mockSocket,
                inAdapters,
                outAdapters,
                PlayerColor.BLACK,
                gamers,
                mockGameManager);

        // Execution of the thread logic
        clientThread.run();

        // Verification of player registration in the GUI adapter
        verify(mockGuiOutAdapter).registerPlayer(eq(PlayerColor.BLACK), any(PrintWriter.class));

        // Verification of the board request call on the GUI input adapter
        verify(inAdapters.get(0)).sendBoardRequest(PlayerColor.BLACK);
    }
}