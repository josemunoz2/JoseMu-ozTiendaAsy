package com.tienda.tiendaropa.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.tienda.tiendaropa.model.*;
import com.tienda.tiendaropa.model.Carrito.ItemCarrito;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    private final Firestore db = FirestoreClient.getFirestore();
    private static final String COLLECTION_CLIENTES = "clientes";
    private static final String COLLECTION_PRODUCTOS = "productos";
    private static final String COLLECTION_BOLETAS = "boletas";

    // ============================ CLIENTES ============================

    public String saveCliente(Cliente cliente) throws ExecutionException, InterruptedException {
        String baseId = cliente.getId();
        String newId = baseId;
        int counter = 1;
        while (true) {
            DocumentSnapshot document = db.collection(COLLECTION_CLIENTES).document(newId).get().get();
            if (!document.exists()) break;
            counter++;
            newId = baseId.replaceAll("\\d+$", "") + String.format("%03d", counter);
        }
        cliente.setId(newId);
        db.collection(COLLECTION_CLIENTES).document(newId).set(cliente);
        return "Cliente guardado con ID: " + newId;
    }

    public Cliente getClienteById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_CLIENTES).document(id).get().get();
        return document.exists() ? document.toObject(Cliente.class) : null;
    }

    public List<QueryDocumentSnapshot> getAllClientes() throws ExecutionException, InterruptedException {
        return db.collection(COLLECTION_CLIENTES).get().get().getDocuments();
    }

    public String deleteClienteById(String id) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION_CLIENTES).document(id).delete();
        return "Cliente eliminado";
    }

    // ============================ PRODUCTOS ============================

    public String saveProducto(Producto producto) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        String baseId = producto.getId();
        String newId = baseId;
        int counter = 1;
    
        while (true) {
            DocumentSnapshot document = db.collection("productos").document(newId).get().get();
            if (!document.exists()) break;
            counter++;
            newId = baseId.replaceAll("\\d+$", "") + String.format("%03d", counter);
        }
    
        producto.setId(newId);
        db.collection("productos").document(newId).set(producto);
        return "Producto guardado con ID: " + newId;
    }
    

    public String deleteProducto(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("productos").document(id).delete();
        return "Producto eliminado con ID: " + id;
    }
    

    public Producto getProductoById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_PRODUCTOS).document(id).get().get();
        return document.exists() ? document.toObject(Producto.class) : null;
    }

    public List<QueryDocumentSnapshot> getAllProductos() throws ExecutionException, InterruptedException {
        return db.collection(COLLECTION_PRODUCTOS).get().get().getDocuments();
    }

    public List<Producto> filtrarProductos(String nombre, String proveedor, Double min, Double max) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documentos = db.collection("productos").get().get().getDocuments();
        List<Producto> resultados = new ArrayList<>();
        for (DocumentSnapshot doc : documentos) {
            Producto p = doc.toObject(Producto.class);
            boolean coincide = true;
            if (nombre != null && !p.getNombre().toLowerCase().contains(nombre.toLowerCase())) coincide = false;
            if (proveedor != null && (p.getProveedor() == null || !p.getProveedor().equalsIgnoreCase(proveedor))) coincide = false;
            if (min != null && p.getPrecio() < min) coincide = false;
            if (max != null && p.getPrecio() > max) coincide = false;
            if (coincide) resultados.add(p);
        }
        return resultados;
    }
    public List<Producto> getProductosPorCategoria(String categoria) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference productosRef = db.collection("productos");
    
        Query query = productosRef.whereEqualTo("categoria", categoria);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
    
        List<Producto> productos = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            Producto producto = doc.toObject(Producto.class);
            productos.add(producto);
        }
    
        return productos;
    }
    public String aumentarStockProveedor(String productoId, String proveedorId, int cantidad) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
    
        // Buscar la relación proveedor-producto exacta
        CollectionReference relaciones = db.collection("proveedor-producto");
        Query query = relaciones
            .whereEqualTo("productoId", productoId)
            .whereEqualTo("proveedorId", proveedorId);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documentos = future.get().getDocuments();
    
        if (documentos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No existe relación proveedor-producto");
        }
    
        DocumentSnapshot doc = documentos.get(0);
        ProveedorProducto pp = doc.toObject(ProveedorProducto.class);
        pp.setStock(pp.getStock() + cantidad);
    
        // Guardar el nuevo stock
        db.collection("proveedor-producto").document(pp.getId()).set(pp);
        return "Stock actualizado correctamente para proveedor: " + proveedorId;
    }
    
    

    // ============================ BOLETAS ============================

   
   

    public String saveBoleta(String rol, Boleta boleta) throws ExecutionException, InterruptedException {
        if (!rol.equalsIgnoreCase("cliente")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Solo los clientes pueden generar boletas.");
        }
    
        Firestore db = FirestoreClient.getFirestore();
        double totalCalculado = 0.0;
        Set<String> productosProcesados = new HashSet<>();
    
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            String productoId = detalle.getProductoId();
            int cantidad = detalle.getCantidad();
    
            if (productosProcesados.contains(productoId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto duplicado en la boleta: " + productoId);
            }
            productosProcesados.add(productoId);
    
            // Buscar relación proveedor-producto
            CollectionReference relaciones = db.collection("proveedor-producto");
            Query query = relaciones.whereEqualTo("productoId", productoId);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documentos = future.get().getDocuments();
    
            if (documentos.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontró proveedor para el producto: " + productoId);
            }
    
            QueryDocumentSnapshot doc = documentos.get(0);
            String docId = doc.getId();
            DocumentSnapshot snapshot = db.collection("proveedor-producto").document(docId).get().get();
            ProveedorProducto proveedorProducto = snapshot.toObject(ProveedorProducto.class);
    
            if (proveedorProducto == null || proveedorProducto.getStock() < cantidad) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente o relación inválida para producto: " + productoId);
            }
    
            // Mostrar info para depuración
            System.out.println("Producto: " + productoId + ", cantidad pedida: " + cantidad + ", stock actual: " + proveedorProducto.getStock());
    
            // Setear precio unitario y calcular total
            double precioUnitario = proveedorProducto.getPrecioCompra();
            detalle.setPrecioUnitario(precioUnitario);
    
            double subtotal = precioUnitario * cantidad;
            totalCalculado += subtotal;
    
            // Restar stock y actualizar
            proveedorProducto.setStock(proveedorProducto.getStock() - cantidad);
            db.collection("proveedor-producto").document(docId).set(proveedorProducto);
        }
    
        boleta.setTotal(totalCalculado);
    
        // Guardar boleta
        DocumentReference boletaRef = db.collection("boletas").document();
        boleta.setId(boletaRef.getId());
        boletaRef.set(boleta);
    
        return "Boleta creada con ID: " + boleta.getId();
    }
    

    

    
    public String saveBoleta(Boleta boleta) throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("boletas").document(boleta.getId());
    ApiFuture<WriteResult> future = docRef.set(boleta);
    return "Boleta guardada en: " + future.get().getUpdateTime();
}

    

    public Boleta getBoletaById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_BOLETAS).document(id).get().get();
        return document.exists() ? document.toObject(Boleta.class) : null;
    }

    public List<QueryDocumentSnapshot> getAllBoletas() throws ExecutionException, InterruptedException {
        return db.collection(COLLECTION_BOLETAS).get().get().getDocuments();
    }

    public List<QueryDocumentSnapshot> getBoletasByClienteId(String clienteId) throws ExecutionException, InterruptedException {
        return db.collection(COLLECTION_BOLETAS).whereEqualTo("clienteId", clienteId).get().get().getDocuments();
    }

    public String deleteBoletaById(String id) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION_BOLETAS).document(id).delete();
        return "Boleta eliminada con ID: " + id;
    }
    public void descontarStockProveedor(String productoId, int cantidadComprada) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
    
        // Buscar la relación producto-proveedor con stock disponible
        CollectionReference relaciones = db.collection("proveedor-producto");
        Query query = relaciones.whereEqualTo("productoId", productoId).whereGreaterThanOrEqualTo("stock", cantidadComprada);
        ApiFuture<QuerySnapshot> future = query.get();
    
        List<QueryDocumentSnapshot> documentos = future.get().getDocuments();
        if (documentos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para el producto ID: " + productoId);
        }
    
        // Usamos el primer proveedor que tenga stock suficiente
        DocumentSnapshot doc = documentos.get(0);
        ProveedorProducto pp = doc.toObject(ProveedorProducto.class);
    
        // Restar stock
        pp.setStock(pp.getStock() - cantidadComprada);
    
        // Guardar cambios
        db.collection("proveedor-producto").document(pp.getId()).set(pp);
    }
    

    // ============================ PROVEEDORES ============================

    public String saveProveedor(Proveedor proveedor) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference ref = db.collection("proveedores");
    
        // Generar nuevo ID numérico como String
        int nuevoId = ref.get().get().size() + 1;
        String id = String.valueOf(nuevoId);
    
        proveedor.setId(id);
        db.collection("proveedores").document(id).set(proveedor);
        return "Proveedor guardado con ID: " + id;
    }
    

    public List<Proveedor> getAllProveedores() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> docs = db.collection("proveedores").get().get().getDocuments();
        List<Proveedor> list = new ArrayList<>();
        for (DocumentSnapshot doc : docs) list.add(doc.toObject(Proveedor.class));
        return list;
    }

    public Proveedor getProveedorById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection("proveedores").document(id).get().get();
        return document.exists() ? document.toObject(Proveedor.class) : null;
    }

    public String deleteProveedor(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("proveedores").document(id).delete();
        return "Proveedor eliminado con ID: " + id;
    }
    

    // ============================ CARRITO ============================

    public String agregarProductoAlCarrito(String clienteId, ItemCarrito nuevoItem) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference carritoRef = db.collection("carritos").document(clienteId);
    
        Carrito carrito;
        DocumentSnapshot snapshot = carritoRef.get().get();
    
        if (snapshot.exists()) {
            carrito = snapshot.toObject(Carrito.class);
        } else {
            carrito = new Carrito();
            carrito.setClienteId(clienteId);
            carrito.setProductos(new ArrayList<>());
        }
    
        boolean encontrado = false;
        for (ItemCarrito item : carrito.getProductos()) {
            if (item.getProductoId().equals(nuevoItem.getProductoId())) {
                item.setCantidad(item.getCantidad() + nuevoItem.getCantidad());
                encontrado = true;
                break;
            }
        }
    
        if (!encontrado) carrito.getProductos().add(nuevoItem);
    
        carritoRef.set(carrito, SetOptions.merge());
        return "Producto agregado al carrito de cliente: " + clienteId;
    }

    public Carrito getCarritoByClienteId(String clienteId) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = db.collection("carritos").document(clienteId).get().get();
        return snapshot.exists() ? snapshot.toObject(Carrito.class) : null;
    }

    public String eliminarCarrito(String clienteId) throws ExecutionException, InterruptedException {
        db.collection("carritos").document(clienteId).delete();
        return "Carrito eliminado para cliente: " + clienteId;
    }

    // ============================ provedor prodcutos ============================
// Crear un nuevo registro ProveedorProducto
public String saveProveedorProducto(ProveedorProducto proveedorProducto) throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("proveedor-producto").document();
    proveedorProducto.setId(docRef.getId());
    ApiFuture<WriteResult> result = docRef.set(proveedorProducto);
    return "ProveedorProducto guardado con ID: " + docRef.getId();
}

// Obtener todos los registros
public List<ProveedorProducto> getAllProveedorProductos() throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference proveedorProductos = db.collection("proveedor-producto");
    ApiFuture<QuerySnapshot> querySnapshot = proveedorProductos.get();
    List<ProveedorProducto> lista = new ArrayList<>();
    for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
        ProveedorProducto pp = doc.toObject(ProveedorProducto.class);
        lista.add(pp);
    }
    return lista;
}

// Obtener un registro por ID
public ProveedorProducto getProveedorProductoById(String id) throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("proveedor-producto").document(id);
    DocumentSnapshot document = docRef.get().get();
    if (document.exists()) {
        return document.toObject(ProveedorProducto.class);
    }
    return null;
}

// Actualizar un registro
public String updateProveedorProducto(ProveedorProducto proveedorProducto) throws ExecutionException, InterruptedException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("proveedor-producto").document(proveedorProducto.getId());
    ApiFuture<WriteResult> result = docRef.set(proveedorProducto);
    return "ProveedorProducto actualizado: " + proveedorProducto.getId();
}

// Eliminar un registro
public String deleteProveedorProducto(String id) {
    Firestore db = FirestoreClient.getFirestore();
    db.collection("proveedor-producto").document(id).delete();
    return "ProveedorProducto eliminado: " + id;
}



    // ============================ VALIDADORES ============================

    private void validarAdmin(String rol) {
        if (rol == null || !rol.equalsIgnoreCase("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: solo administradores");
        }
    }

    private void validarCliente(String rol) {
        if (rol == null || !rol.equalsIgnoreCase("cliente")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: solo clientes");
        }
    }
}
