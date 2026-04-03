package org.manageSchool;

import org.manageSchool.subject.SubjectService;

public class Main {
    public static void main(String[] args) {
        // ========== ISSUE-012: Carga de materias predeterminadas ==========
        SubjectService subjectService = new SubjectService();
        subjectService.seedDefaultSubjects();

        System.out.println("Sistema inicializado. Materias predeterminadas cargadas.");

    }
}
