package com.sistemaFacturacion.Mambo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemaFacturacion.Mambo.auth.jwt.JwtService;
import com.sistemaFacturacion.Mambo.entity.Repository.ClienteRepository;
import com.sistemaFacturacion.Mambo.entity.Repository.CompraRepository;
import com.sistemaFacturacion.Mambo.entity.Repository.DestinatarioRepository;
import com.sistemaFacturacion.Mambo.entity.Repository.DetalleCompraRepository;
import com.sistemaFacturacion.Mambo.entity.Repository.EnvioRepository;
import com.sistemaFacturacion.Mambo.entity.Repository.PagoRepository;
import com.sistemaFacturacion.Mambo.entity.Repository.ProductoRepository;
import com.sistemaFacturacion.Mambo.entity.model.Compra;
import com.sistemaFacturacion.Mambo.entity.model.Destinatario;
import com.sistemaFacturacion.Mambo.entity.model.DetalleCompra;
import com.sistemaFacturacion.Mambo.entity.model.Envio;
import com.sistemaFacturacion.Mambo.entity.model.Producto;
import com.sistemaFacturacion.Mambo.entity.model.TipoComprobante;
import com.sistemaFacturacion.Mambo.entity.model.TipoEnvio;
import com.sistemaFacturacion.Mambo.entity.model.TipoEstado;
import com.sistemaFacturacion.Mambo.entity.model.cliente;
import com.sistemaFacturacion.Mambo.entity.model.pago;
import com.sistemaFacturacion.Mambo.mape.dto.CompraDTO;
import com.sistemaFacturacion.Mambo.mape.dto.CompraRequestDTO;
import com.sistemaFacturacion.Mambo.mape.dto.DetalleCompraDto;
import com.sistemaFacturacion.Mambo.mape.mapeo.CompraMape;
import com.sistemaFacturacion.Mambo.mape.mapeo.DetalleCompraMape;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private DetalleCompraRepository detalleCompraRepository;

    @Autowired
    private EnvioRepository envioRepository;
    @Autowired
    private DestinatarioRepository destinatarioRepository;
    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private CompraMape compraMape;
    @Autowired
    private DetalleCompraMape detalleCompraMape;
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public CompraDTO guardarCarrito(CompraRequestDTO dto, String dni) {
        
        cliente cliente = clienteRepository.findByNumeroDocumento(dni)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 2. Guardar ENVÍO
        Envio envio = compraMape.toEnvioEntity(dto.getEnvio());
        // Lógica de precio de envío
        envio.setPrecio(envio.getTipoEnvio() == TipoEnvio.DELIVERY ? 20.0 : 0.0);
        envio = envioRepository.save(envio);

        // 3. Guardar DESTINATARIO
        Destinatario destinatario = compraMape.toDestinatarioEntity(dto.getDestinatario());
        destinatario.setCliente(cliente);
        destinatario = destinatarioRepository.save(destinatario);

        // 4. Guardar PAGO
        pago pago = pagoRepository.save(compraMape.toPagoEntity(dto.getPago()));

        // 5. Preparar la COMPRA (Aún no la guardamos, necesitamos el total real)
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setDestinatario(destinatario);
        compra.setEnvio(envio);
        compra.setPago(pago);
        compra.setEstadoPago(TipoEstado.PENDIENTE);
        compra.setTipoComprobante(TipoComprobante.valueOf(dto.getTipoComprobante()));
        compra.setFechaCreacion(LocalDateTime.now());

        // 6. PROCESAR DETALLES, VALIDAR STOCK Y CALCULAR TOTAL SEGURO
        double totalProductos = 0.0;
        List<DetalleCompra> detallesParaGuardar = new ArrayList<>();

        for (DetalleCompraDto detDto : dto.getDetalles()) {
            // A. Buscamos el producto REAL en la BD
            Producto productoReal = productoRepository.findById(detDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + detDto.getProductoId()));

            // B. Validamos Stock
            if (productoReal.getStock() < detDto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + productoReal.getNombre());
            }

            // C. Descontamos Stock y actualizamos producto
            productoReal.setStock(productoReal.getStock() - detDto.getCantidad());
            productoRepository.save(productoReal);

            // D. Creamos el detalle usando el PRECIO REAL de la BD (Seguridad)
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProducto(productoReal);
            detalle.setCantidad(detDto.getCantidad());
            detalle.setPrecioUnitario(productoReal.getPrecio()); // Usamos precio de BD
            detalle.setSubTotal(productoReal.getPrecio() * detDto.getCantidad());
            
            // Agregamos al acumulador
            totalProductos += detalle.getSubTotal();
            detallesParaGuardar.add(detalle);
        }

        // 7. Establecer Total y Guardar Compra
        compra.setTotal(totalProductos + envio.getPrecio());
        Compra compraGuardada = compraRepository.save(compra);

        // 8. Guardar Detalles vinculados a la Compra
        for (DetalleCompra det : detallesParaGuardar) {
            det.setCompra(compraGuardada); // Vinculamos la compra ya guardada
            detalleCompraRepository.save(det);
        }
        
        // Asignamos para devolver en el DTO
        compraGuardada.setDetalles(detallesParaGuardar);

        return compraMape.toDto(compraGuardada);
    }// 📋 Listar todos los carritos
    public List<CompraDTO> listarCarritos() {
        return compraRepository.findAll()
                .stream()
                .map(compraMape::toDto)
                .collect(Collectors.toList());
    }

    // 🔎 Buscar carritos por cliente
    public List<CompraDTO> buscarPorCliente(Long clienteId) {
        return compraRepository.findByClienteId(clienteId).stream()
                .map(compraMape::toDto)
                .collect(Collectors.toList());
    }

}
