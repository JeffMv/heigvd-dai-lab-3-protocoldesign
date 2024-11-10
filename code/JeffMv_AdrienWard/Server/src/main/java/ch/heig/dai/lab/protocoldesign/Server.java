package ch.heig.dai.lab.protocoldesign;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

// supported protocol versions : 1 -> 2

//public class Server implements Runnable {
public class Server {
    final int SERVER_PORT = 2277;

    // this server's supported protocol versions range
    final int PROTOCOL_VERSION_MIN = 1;
    final int PROTOCOL_VERSION_MAX = 2;

    public static void main(String[] args) {
        // Create a new server and run it
        Server server = new Server();
        server.run();
    }

    private void run() {

        try (ServerSocket serverSocket = new ServerSocket(this.SERVER_PORT)) {
            System.out.println("Server started on port " + this.SERVER_PORT);

            while (true) {
                // Wait for a client to connect
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                     BufferedWriter clientOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {

                    System.out.println("A client is connected");

                    clientOut.write(String.format("WELCOME:PROTOCOL:" + PROTOCOL_VERSION_MIN + ":" + PROTOCOL_VERSION_MAX + ":ADD,SUB,MUL,DIV\n"));
                    clientOut.write(String.format("3\n")); // announce to client the nbr of lines to read
                    clientOut.write(String.format("Opérations supportées: <operation> <operand1> <operand2>\nAvec <operation> prenant une valeur parmi : ADD,SUB,MUL,DIV,EXT\nValeurs acceptées pour <operandX>: nombre à virgule" + "\n"));
                    clientOut.flush();

                    String line;

                    while ((line = clientIn.readLine()) != null) {
                        System.out.println("Client message: " + line);
                        // TODO : handle client message
                        ComputationRequest request = ComputationRequest.fromString(line);
                        String response = "";
                        if (request.errorCode != 0 || !request.canCompute()) {
                            response = request.errorCode + " " + request.errorDetails + "\n";
                        } else {
                            response = request.errorCode + " " + request.compute() + "\n";
                        }
                        clientOut.write(response);
                        clientOut.flush();
                    }

                } catch (Exception e) {
                    System.out.println("Communication Error. Error: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

        } catch(Exception e){
            System.out.println("Server or socket Error. Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

class ComputationRequest {
    Integer protocolVersion;
    Integer numberBase;
    String operation;
    Double operand1;
    Double operand2;

    int errorCode = 0;
    int errorDetails = 0;

    public static final int ERR_MALFORMED_INPUT = 1;
    public static final int ERR_OPERATION_ERROR = 2;
    public static final int ERR_OPERAND_ERROR = 4;
    public static final int ERR_MATHS_ERROR = 8;
    public static final int ERR_PROTOCOL_ERROR = 16;
    public static final int ERR_OTHER_ERROR = 32;

    public static final int ERRD_LEFT_OPERAND_ERROR = 1;
    public static final int ERRD_RIGHT_OPERAND_ERROR = 2;
    public static final int ERRD_DIV_BY_ZERO_ERROR = 2;
    public static final int ERRD_SQRT_NEGATIVE_ERROR = 1;


    public static ComputationRequest fromString(String message) {
        String[] parts = message.split(" ");
        int errorCode = (parts.length != 5) ? 1 : 0;
        int errorDetails = 0;

        Integer protocolVersion = null;
        Integer numberBase = null;
        String operation = null;
        Double operand1 = null;
        Double operand2 = null;

        try {
            protocolVersion = Integer.parseInt(parts[0]);

            if (parts.length < 2) {
                throw new RuntimeException("Missing operation");
            }
            numberBase = Integer.parseInt(parts[1]);
            if (protocolVersion < 1 || protocolVersion > 2) {
                errorCode = errorCode | ComputationRequest.ERR_PROTOCOL_ERROR;
            }
            if (numberBase != 10) {
                errorCode = errorCode | ComputationRequest.ERR_PROTOCOL_ERROR;
            }

            operation = parts[2].trim().toUpperCase();
            if (!operation.equals("ADD") && !operation.equals("SUB") && !operation.equals("MUL") && !operation.equals("DIV")) {
                errorCode = errorCode | ComputationRequest.ERR_OPERATION_ERROR;
            }

            operand1 = tryParse(parts[3]);
            operand2 = tryParse(parts[4]);
            if (operand1 == null || operand1.isNaN() ||
                    operand2 == null || operand2.isNaN()) {
                errorCode = errorCode | ComputationRequest.ERR_OPERAND_ERROR;
                errorDetails = errorDetails | ((operand1 == null || operand1.isNaN()) ? ComputationRequest.ERRD_LEFT_OPERAND_ERROR : 0);
                errorDetails = errorDetails | ((operand2 == null || operand2.isNaN()) ? ComputationRequest.ERRD_RIGHT_OPERAND_ERROR : 0);
                // throw new NumberFormatException();
            }
            return new ComputationRequest(protocolVersion, numberBase, operation, operand1, operand2, errorCode, errorDetails);
        } catch (NumberFormatException e) {
            errorCode = errorCode | (parts.length < 5 ? ComputationRequest.ERR_MALFORMED_INPUT : ComputationRequest.ERR_OPERAND_ERROR);
            return new ComputationRequest(protocolVersion, numberBase, operation, operand1, operand2, errorCode, errorDetails);
        } catch (RuntimeException e) {
            errorCode = errorCode | ComputationRequest.ERR_MALFORMED_INPUT;
            return new ComputationRequest(protocolVersion, numberBase, operation, operand1, operand2, errorCode, errorDetails);
        }
    }

    private static Double tryParse(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private ComputationRequest(Integer protocolVersion, Integer numberBase, String operation, Double operand1, Double operand2, int errorCode, int errorDetails) {
        this.protocolVersion = protocolVersion;
        this.numberBase = numberBase;
        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }

    boolean canCompute() {
        if (this.operation == null || this.operand1 == null || this.operand2 == null) {
            return false;
        }
        if (this.operation.equals("DIV") && this.operand2 == 0) {
            errorCode = errorCode | ComputationRequest.ERR_MATHS_ERROR;
            errorDetails = errorDetails | ERRD_RIGHT_OPERAND_ERROR;
            return false;
        }
        return this.errorCode == 0;
    }

    Double compute() {
        Double result = null;
        if (!canCompute()) {
            return result;
        }
        switch (this.operation.trim().toUpperCase()) {
            case "ADD":
                return this.operand1 + this.operand2;
            case "SUB":
                return this.operand1 - this.operand2;
            case "MUL":
                return this.operand1 * this.operand2;
            case "DIV":
                return this.operand1 / this.operand2;
            case "EXT":
                return Double.valueOf(0.0);
            default:
                return result;
        }
    }
}