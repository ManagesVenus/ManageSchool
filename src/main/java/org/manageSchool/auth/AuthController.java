package org.manageSchool.auth;

import org.manageSchool.shared.AppException;

import java.util.Scanner;

public class AuthController {

    private final AuthService service = new AuthService();

    // Muestra el formulario de login. Repite hasta que el usuario ingrese credenciales válidas.
    public User mostrarLogin(Scanner scanner) {
        while (true) {
            System.out.println("\n===== INICIAR SESIÓN =====");
            System.out.print("Correo: ");
            String correo = scanner.nextLine().trim();

            System.out.print("Contraseña: ");
            String contrasena = scanner.nextLine().trim();

            try {
                return service.login(correo, contrasena);
            } catch (AppException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Por favor, intente de nuevo.");
            }
        }
    }

    // Permite al Admin crear una cuenta nueva desde consola.
    public void crearCuenta(Scanner scanner, String rol) {
        System.out.println("\n===== CREAR CUENTA =====");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("Correo (@colegio.edu.co): ");
        String correo = scanner.nextLine().trim();

        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine().trim();

        try {
            CreateUserRequest request = new CreateUserRequest(nombre, correo, contrasena, rol);
            service.createAccount(request);
            System.out.println("Cuenta creada exitosamente para: " + nombre + " (" + rol + ")");
        } catch (AppException e) {
            System.out.println("Error al crear cuenta: " + e.getMessage());
        }
    }
}