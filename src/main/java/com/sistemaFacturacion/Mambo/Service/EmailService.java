package com.sistemaFacturacion.Mambo.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sistemaFacturacion.Mambo.mape.dto.CompraDTO;
import com.sistemaFacturacion.Mambo.mape.dto.DetalleCompraDto;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async 
    public void enviarBoleta(String destinatario, CompraDTO compra) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("Tu Boleta de Compra - Mambo Store #" + compra.getId());
            String htmlContent = generarHtmlBoleta(compra);
            
            helper.setText(htmlContent, true); 

            mailSender.send(message);
            System.out.println("📧 Correo enviado a: " + destinatario);

        } catch (MessagingException e) {
            System.out.println("❌ Error al enviar correo: " + e.getMessage());
        }
    }

    private String generarHtmlBoleta(CompraDTO compra) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<div style='font-family: Arial; max-width: 600px; margin: auto; border: 1px solid #ccc; padding: 20px;'>");
        sb.append("<h2 style='color: #28a745; text-align: center;'>¡Gracias por tu compra!</h2>");
        sb.append("<p>Hola <b>").append(compra.getContactoDestinatario()).append("</b>,</p>");
        sb.append("<p>Tu pedido ha sido confirmado. Aquí tienes el resumen:</p>");
        
        sb.append("<hr>");
        sb.append("<p><b>N° Pedido:</b> ").append(compra.getId()).append("</p>");
        sb.append("<p><b>Fecha:</b> ").append(compra.getFechaCreacion()).append("</p>");
        sb.append("<p><b>Tipo Envío:</b> ").append(compra.getTipoEnvio()).append("</p>");
        
        sb.append("<h3>Detalle:</h3>");
        sb.append("<table style='width: 100%; border-collapse: collapse;'>");
        sb.append("<tr style='background: #f8f9fa;'><th>Producto</th><th style='text-align: right;'>Precio</th></tr>");
        
        for (DetalleCompraDto det : compra.getDetalles()) {
            sb.append("<tr>");
            // Nota: Asegúrate de que tu DTO tenga el nombre del producto, si no, tendrás que sacarlo antes
            sb.append("<td style='padding: 8px;'>").append(det.getCantidad()).append(" x Producto: ").append(det.getNombreProducto()).append("</td>");
            sb.append("<td style='text-align: right; padding: 8px;'>S/ ").append(det.getSubtotal()).append("</td>");
            sb.append("</tr>");
        }
        
        sb.append("</table>");
        sb.append("<hr>");
        sb.append("<h3 style='text-align: right;'>Total: S/ ").append(compra.getTotal()).append("</h3>");
        sb.append("<p style='text-align: center; font-size: 12px; color: #777;'>Mambo Store - Lima, Perú</p>");
        sb.append("</div></body></html>");
        
        return sb.toString();
    }
}