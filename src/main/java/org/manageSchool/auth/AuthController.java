package org.manageSchool.auth;

import java.util.Scanner;

import org.manageSchool.shared.AppException;

public class AuthController {

    private final AuthService service = new AuthService(new AuthRepository());

    // Muestra el formulario de login. Repite hasta credenciales válidas o que el usuario escriba "0" para volver.
    public User mostrarLogin(Scanner scanner) {
        while (true) {
            System.out.println("\n  ===== INICIAR SESIÓN =====");
            System.out.println("  (Escriba 0 para volver al menú principal)\n");

            System.out.print("  Correo: ");
            String correo = scanner.nextLine().trim();
            if (correo.equals("0")) return null;

            System.out.print("  Contraseña: ");
            String contrasena = scanner.nextLine().trim();

            try {
                return service.login(correo, contrasena);
            } catch (AppException e) {
                System.out.println("  Error: " + e.getMessage());
            }
        }
    }

    // Permite al Admin crear una cuenta nueva desde consola.
    public void crearCuenta(Scanner scanner, String rol) {
        System.out.println("\n  ===== CREAR CUENTA =====");

        System.out.print("  Nombre: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("  Correo (@colegio.edu.co): ");
        String correo = scanner.nextLine().trim();

        System.out.print("  Contraseña: ");
        String contrasena = scanner.nextLine().trim();

        try {
            CreateUserRequest request = new CreateUserRequest(nombre, correo, contrasena, rol);
            service.createAccount(request);
            System.out.println("  Cuenta creada exitosamente para: " + nombre + " (" + rol + ")");
        } catch (AppException e) {
            System.out.println("  Error al crear cuenta: " + e.getMessage());
        }
    }
}