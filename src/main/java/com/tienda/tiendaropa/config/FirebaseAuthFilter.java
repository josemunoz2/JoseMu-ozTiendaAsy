package com.tienda.tiendaropa.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FirebaseAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        String path = http.getRequestURI();
        String metodo = http.getMethod();

        // ✅ Permitir acceso sin token a estos endpoints públicos
        if (esRutaPublica(path, metodo)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = http.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.replace("Bearer ", "").trim();
            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
                request.setAttribute("firebaseUser", decoded);
                chain.doFilter(request, response);
                return;
            } catch (Exception e) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }
        }

        // 🔒 Si no trae token y no es pública
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no proporcionado");
    }

    private boolean esRutaPublica(String path, String metodo) {
        return (
            // Productos públicos
            (metodo.equals("GET") && (
                path.equals("/productos") ||
                path.matches("/productos/[^/]+") ||
                path.matches("/productos/categoria/[^/]+")
            )) ||
    
            // Clientes sin token
            (path.equals("/clientes") && metodo.equals("POST")) ||
            (path.matches("/clientes/[^/]+") && (metodo.equals("PUT") || metodo.equals("DELETE"))) ||
    
            // Carrito sin token
            (path.equals("/carrito/agregar") && metodo.equals("POST")) ||
            (path.equals("/carrito") && (metodo.equals("GET") || metodo.equals("DELETE"))) ||
    
            // Boletas sin token
            (path.equals("/boletas") && (metodo.equals("POST") || metodo.equals("GET"))) ||
            (path.matches("/boletas/[^/]+") && (metodo.equals("GET") || metodo.equals("DELETE")))
        );
    }
    
}
