package org.manageSchool.auth;

import java.util.List;
import java.util.Optional;

import org.manageSchool.shared.util.JsonFileManager;

public class AuthRepository {

    private static final String FILE = "users.json";

    // Devuelve todos los usuarios del archivo
    public List<User> findAll() {
        return JsonFileManager.readAll(FILE, User.class);
    }

    // Busca un usuario por correo (ignora mayúsculas)
    public Optional<User> findByEmail(String correo) {
        return findAll().stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    // Devuelve todos los usuarios con un rol específico
    public List<User> findByRole(String rol) {
        return findAll().stream()
                .filter(u -> u.getRol().equalsIgnoreCase(rol))
                .toList();
    }

    // Guarda un nuevo usuario (agrega al archivo)
    public void save(User user) {
        List<User> users = findAll();
        users.add(user);
        JsonFileManager.writeAll(FILE, users);
    }

    // Actualiza un usuario existente (busca por id y reemplaza)
    public void update(User user) {
        List<User> users = findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                break;
            }
        }
        JsonFileManager.writeAll(FILE, users);
    }

    // Elimina un usuario por id
    public void deleteById(String id) {
        List<User> users = findAll().stream()
                .filter(u -> !u.getId().equals(id))
                .toList();
        JsonFileManager.writeAll(FILE, users);
    }

    // Verifica si existe un usuario con ese correo
    public boolean existsByEmail(String correo) {
        return findByEmail(correo).isPresent();
    }
}