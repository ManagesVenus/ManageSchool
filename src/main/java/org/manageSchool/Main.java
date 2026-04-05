package org.manageSchool;

import org.manageSchool.auth.AuthController;
import org.manageSchool.auth.AuthService;
import org.manageSchool.subject.SubjectService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // ========== ISSUE-012: Carga de materias predeterminadas ==========
        SubjectService subjectService = new SubjectService();
        subjectService.seedDefaultSubjects();

        System.out.println("Sistema inicializado. Materias predeterminadas cargadas.");

        // ISSUE-003: admin por defecto + login
        AuthService authService = new AuthService();
        authService.seedDefaultAdmin();

        Scanner scanner = new Scanner(System.in);
        AuthController authController = new AuthController();
        authController.mostrarLogin(scanner);
    }
}
