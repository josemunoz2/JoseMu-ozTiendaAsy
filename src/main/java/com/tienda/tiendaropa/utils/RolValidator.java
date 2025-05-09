package com.tienda.tiendaropa.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RolValidator {

    public static void validarAdmin(String rol) {
        if (rol == null || !rol.equalsIgnoreCase("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: solo administradores");
        }
    }

    public static void validarCliente(String rol) {
        if (rol == null || !rol.equalsIgnoreCase("cliente")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: solo clientes");
        }
    }
}
