package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    final String SERVER_ADDRESS = "localhost";
    final int SERVER_PORT = 2277;

    public static void main(String[] args) {
        // Create a new client and run it
        Client client = new Client();
        client.run();
    }

    private void run() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            // Lecture et affichage du message de bienvenue du serveur
            String srvGreeting = in.readLine();
            String nbrLinesToReadMsg = in.readLine();
            int nbrLinesToRead = 1;
            try {
                nbrLinesToRead = Integer.parseInt(nbrLinesToReadMsg);
            } catch (NumberFormatException e) {
                System.out.println("Erreur : le serveur a envoyé un nombre de lignes à lire invalide. Veuillez vérifier la version du serveur. Déconnexion.");
                throw new RuntimeException("le serveur a envoyé un nombre de lignes à lire invalide. Veuillez vérifier la version du serveur.");
            }

            String welcomeMessage = "";
            for (int i = 0; i < nbrLinesToRead; ++i) {
                welcomeMessage += in.readLine() + "\n";
            }
            System.out.println("Liste des opérations supportées par le serveur :" + welcomeMessage);

            // Initialisation de l'interface de commande
            Scanner scanner = new Scanner(System.in);

            while (true) {
                // System.out.println("\nEntrez une opération au format : <version> <base> <operation> <nombre1> <nombre2>");
                // System.out.println("Par exemple : 1 10 ADD -3.5 4.0 ou tapez EXT pour quitter.");
                System.out.println("\nEntrez une opération au format : <operation> <nombre1> <nombre2>");
                System.out.println("Par exemple : ADD -3.5 4.0 ou tapez EXT pour quitter.");

                // Lecture de l'opération saisie par l'utilisateur
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("EXT")) {
                    // Envoi du message pour terminer la connexion
                    out.write("1 10 EXT 0 0\n");
                    out.flush();
                    System.out.println("Déconnexion demandée. Fermeture du client.");
                    break;
                }

                // Envoi de la requête au serveur
                out.write("1 10 " + input.trim() + "\n");
                out.flush();

                // Lecture et affichage de la réponse du serveur
                String response = in.readLine();
                if (response != null) {
                    String[] parts = response.split(" ");
                    int status = Integer.parseInt(parts[0]);
                    String result = parts[1];

                    if (status == 0) {
                        System.out.println("Résultat de l'opération : " + result);
                    } else {
                        System.out.println("Erreur lors du calcul. Code d'erreur : " + status);
                        afficherErreur(status);
                    }
                } else {
                    System.out.println("Erreur : le serveur a fermé la connexion.");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client : exception lors de la connexion au serveur : " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Client : Erreur dans les échanges avec le serveur : " + e.getMessage());
        }
    }

    /**
    * Affiche un message d'erreur en fonction du code d'état reçu.
    */
    private void afficherErreur(int codeErreur) {
        switch (codeErreur) {
            case 1 -> System.out.println("Erreur : Entrée malformée.");
            case 2 -> System.out.println("Erreur : Opération non valide.");
            case 4 -> System.out.println("Erreur : Erreur d'opérande.");
            case 8 -> System.out.println("Erreur : Erreur mathématique (racine d'un nombre négatif ou division par zéro).");
            case 16 -> System.out.println("Erreur : Erreur de protocole.");
            case 32 -> System.out.println("Erreur : Autre erreur.");
            default -> System.out.println("Erreur inconnue.");
        }
    }
}