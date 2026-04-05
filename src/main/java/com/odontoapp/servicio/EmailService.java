package com.odontoapp.servicio;

import com.odontoapp.entidad.Cita;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // ─── ICONOS SVG ───
    private static final String SVG_MAIL = "<svg fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\" width=\"100%\" height=\"100%\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z\"></path></svg>";
    private static final String SVG_USER_GEAR = "<svg fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\" width=\"100%\" height=\"100%\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z\"></path></svg>";
    private static final String SVG_USER_PLUS = "<svg fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\" width=\"100%\" height=\"100%\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z\"></path></svg>";
    private static final String SVG_KEY = "<svg fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\" width=\"100%\" height=\"100%\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z\"></path></svg>";
    private static final String SVG_CHECK = "<svg fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\" width=\"100%\" height=\"100%\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z\"></path></svg>";
    private static final String SVG_X = "<svg fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\" width=\"100%\" height=\"100%\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z\"></path></svg>";
    private static final String SVG_BELL = "<svg fill=\"none\" stroke=\"currentColor\" viewBox=\"0 0 24 24\" width=\"100%\" height=\"100%\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9\"></path></svg>";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MÉTODOS DE ENVÍO PÚBLICOS
    // ══════════════════════════════════════════════════════════════════════════

    /** Activación de cuenta para personal (admin, odontólogo, etc.) */
    public void enviarEmailActivacionAdmin(String para, String nombre, String token) {
        String urlActivacion = baseUrl + "/activar-cuenta?token=" + token;
        String subject = "Activa tu cuenta — OdontoApp";
        
        String html = buildIcon("#CCFBF1", "#0D9488", SVG_USER_GEAR)
            + buildTitle("¡Tu cuenta está lista!", 
                         "Hola " + nombre + ", tu cuenta de personal en OdontoApp ha sido creada exitosamente.", 
                         "Para acceder al sistema deberás activar tu cuenta configurando una contraseña personal.")
            + buildInfoBox("#F0FDFA", "#99F6E4", "#115E59", "#0F766E", "💡 Información Importante:", 
                         "<ul style=\"margin:0;padding-left:24px;\"><li style=\"margin-bottom:4px;\">Este enlace es único y expirará si no lo usas.</li><li>Si no solicitaste una cuenta, comunícate con el administrador.</li></ul>")
            + buildButton("#0D9488", "Activar mi cuenta", urlActivacion);

        enviarSeguro(para, subject, buildWrapper(html), "activación admin");
    }

    /** Completar registro para paciente (self-service) */
    public void enviarEmailActivacion(String para, String nombre, String token) {
        String urlActivacion = baseUrl + "/registro/completar?token=" + token;
        String subject = "Completa tu registro — OdontoApp";

        String html = buildIcon("#DBEAFE", "#2563EB", SVG_USER_PLUS)
            + buildTitle("¡Casi terminamos!", 
                         "Hola " + nombre + ", gracias por iniciar tu registro en OdontoApp.", 
                         "Solo falta un paso para poder gestionar tus citas por internet.")
            + buildInfoBox("#EFF6FF", "#BFDBFE", "#1E40AF", "#1D4ED8", "📋 Siguientes pasos:", 
                         "<ul style=\"margin:0;padding-left:24px;\"><li style=\"margin-bottom:4px;\">Completa tu información personal en el link.</li><li>Crea tu contraseña segura para acceder a tu historial.</li></ul>")
            + buildButton("#2563EB", "Completar mi registro", urlActivacion);

        enviarSeguro(para, subject, buildWrapper(html), "activación paciente");
    }

    /** Contraseña temporal para usuario creado por el admin */
    public void enviarPasswordTemporal(String para, String nombre, String passwordTemporal) {
        String subject = "Bienvenido a OdontoApp — Tus credenciales de acceso";

        String html = buildIcon("#FEF3C7", "#D97706", SVG_KEY)
            + buildTitle("Bienvenido a OdontoApp", 
                         "Hola " + nombre + ", se ha configurado tu acceso al sistema.", 
                         "A continuación encontrarás tus credenciales de acceso temporal.")
            + "<div style=\"background-color:#F8FAFC; border:1px solid #E2E8F0; border-radius:8px; padding:16px; text-align:left; margin-bottom:16px;\">"
            + "<p style=\"font-size:14px; color:#475569; margin:0 0 8px;\"><strong>Email:</strong> " + para + "</p>"
            + "<p style=\"font-size:14px; color:#475569; margin:0;\"><strong>Contraseña Temporal:</strong> <code style=\"background-color:#E2E8F0; padding:2px 6px; border-radius:4px;\">" + passwordTemporal + "</code></p>"
            + "</div>"
            + buildInfoBox("#FEF3C7", "#FDE68A", "#92400E", "#B45309", "⚠️ Importante:", 
                         "<p style=\"margin:0;\">Por tu seguridad, el sistema te pedirá cambiar esta contraseña en tu primer inicio de sesión.</p>")
            + buildButton("#D97706", "Ir al inicio de sesión", baseUrl + "/login");

        try {
            enviarEmail(para, subject, buildWrapper(html));
        } catch (MessagingException e) {
            System.err.println("Error al enviar email con contraseña temporal: " + e.getMessage());
            throw new RuntimeException("Error al enviar email con contraseña temporal: " + e.getMessage());
        }
    }

    /** Recuperación de contraseña */
    public void enviarEmailRecuperacionPassword(String para, String nombre, String token) {
        String urlRestablecer = baseUrl + "/recuperar-password/restablecer?token=" + token;
        String subject = "Restablece tu contraseña — OdontoApp";

        String html = buildIcon("#DCFCE7", "#16A34A", SVG_MAIL)
            + buildTitle("¡Email Enviado!", 
                         "Hemos recibido una solicitud para restablecer tu contraseña.", 
                         "Por favor, sigue las instrucciones para crear una nueva.")
            + buildInfoBox("#EFF6FF", "#BFDBFE", "#1E40AF", "#1D4ED8", "💡 Consejos:", 
                         "<ul style=\"margin:0;padding-left:24px;\"><li style=\"margin-bottom:4px;\">Este link expirará en 24 horas.</li><li>Si no solicitaste el cambio, puedes ignorar este correo.</li></ul>")
            + buildButton("#2563EB", "Restablecer contraseña", urlRestablecer)
            + "<div style=\"text-align:center; font-size:14px;\"><a href=\"" + baseUrl + "/login\" style=\"font-weight:500; color:#475569;\">Volver al inicio de sesión</a></div>";

        try {
            enviarEmail(para, subject, buildWrapper(html));
        } catch (MessagingException e) {
            System.err.println("Error al enviar email de recuperación: " + e.getMessage());
            throw new RuntimeException("Error al enviar email de recuperación: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  NOTIFICACIONES DE CITAS
    // ──────────────────────────────────────────────────────────────────────────

    /** Cita confirmada */
    public void enviarConfirmacionCita(Cita cita) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale.forLanguageTag("es-PE"));
        DateTimeFormatter fmtH = DateTimeFormatter.ofPattern("HH:mm");

        String subject = "✅ Cita confirmada — OdontoApp";
        
        String detalles = "<p style=\"font-size:14px; color:#334155; margin:0 0 12px;\"><strong style=\"color:#64748B;\">📅 Fecha:</strong> " + cita.getFechaHoraInicio().format(fmt) + "</p>"
                        + "<p style=\"font-size:14px; color:#334155; margin:0 0 12px;\"><strong style=\"color:#64748B;\">🕐 Hora:</strong> " + cita.getFechaHoraInicio().format(fmtH) + " hrs</p>"
                        + "<p style=\"font-size:14px; color:#334155; margin:0 0 12px;\"><strong style=\"color:#64748B;\">👨‍⚕️ Dentista:</strong> " + cita.getOdontologo().getNombreCompleto() + "</p>"
                        + "<p style=\"font-size:14px; color:#334155; margin:0;\"><strong style=\"color:#64748B;\">🦷 Tipo:</strong> " + (cita.getProcedimiento() != null ? cita.getProcedimiento().getNombre() : "Por definir") + "</p>";

        String html = buildIcon("#DCFCE7", "#16A34A", SVG_CHECK)
            + buildTitle("¡Cita Confirmada!", "Estimado(a) " + cita.getPaciente().getNombreCompleto() + ", tu reserva ha sido agendada con éxito.", "")
            + "<div style=\"background-color:#F8FAFC; border:1px solid #E2E8F0; border-radius:8px; padding:24px; text-align:left; margin-bottom:24px;\">" + detalles + "</div>"
            + buildInfoBox("#EFF6FF", "#BFDBFE", "#1E40AF", "#1E40AF", "", "💡 Recuerda presentarte a la clínica con al menos <strong>10 minutos de anticipación</strong>.");

        enviarSeguro(cita.getPaciente().getEmail(), subject, buildWrapper(html), "confirmación de cita");
    }

    /** Cita cancelada */
    public void enviarCancelacionCita(Cita cita, String motivo) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale.forLanguageTag("es-PE"));
        DateTimeFormatter fmtH = DateTimeFormatter.ofPattern("HH:mm");

        String subject = "❌ Cita cancelada — OdontoApp";
        
        String detalles = "<p style=\"font-size:14px; color:#991B1B; margin:0 0 12px;\"><strong style=\"color:#B91C1C;\">📅 Fecha:</strong> " + cita.getFechaHoraInicio().format(fmt) + "</p>"
                        + "<p style=\"font-size:14px; color:#991B1B; margin:0 0 12px;\"><strong style=\"color:#B91C1C;\">🕐 Hora:</strong> " + cita.getFechaHoraInicio().format(fmtH) + " hrs</p>"
                        + "<p style=\"font-size:14px; color:#991B1B; margin:0;\"><strong style=\"color:#B91C1C;\">📝 Motivo:</strong> " + (motivo != null ? motivo : "No especificado") + "</p>";

        String html = buildIcon("#FEE2E2", "#DC2626", SVG_X)
            + buildTitle("Cita Cancelada", "Estimado(a) " + cita.getPaciente().getNombreCompleto() + ", tu reserva ha sido cancelada.", "")
            + "<div style=\"background-color:#FEF2F2; border:1px solid #FECACA; border-radius:8px; padding:24px; text-align:left; margin-bottom:24px;\">" + detalles + "</div>"
            + buildButton("#DC2626", "Agendar nueva cita", baseUrl + "/login");

        enviarSeguro(cita.getPaciente().getEmail(), subject, buildWrapper(html), "cancelación de cita");
    }

    /** Cita reprogramada */
    public void enviarReprogramacionCita(Cita citaAntigua, Cita citaNueva) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale.forLanguageTag("es-PE"));
        DateTimeFormatter fmtH = DateTimeFormatter.ofPattern("HH:mm");

        String subject = "🔄 Cita reprogramada — OdontoApp";
        
        String detalles = "<p style=\"font-size:14px; color:#334155; margin:0 0 12px;\"><strong style=\"color:#64748B;\">📅 Nueva Fecha:</strong> " + citaNueva.getFechaHoraInicio().format(fmt) + "</p>"
                        + "<p style=\"font-size:14px; color:#334155; margin:0 0 12px;\"><strong style=\"color:#64748B;\">🕐 Nueva Hora:</strong> " + citaNueva.getFechaHoraInicio().format(fmtH) + " hrs</p>"
                        + "<p style=\"font-size:14px; color:#334155; margin:0;\"><strong style=\"color:#64748B;\">👨‍⚕️ Dentista:</strong> " + citaNueva.getOdontologo().getNombreCompleto() + "</p>";

        String html = buildIcon("#FEF3C7", "#D97706", SVG_BELL)
            + buildTitle("Cita Reprogramada", "Estimado(a) " + citaNueva.getPaciente().getNombreCompleto() + ", tu cita ha sido reprogramada.", "")
            + "<div style=\"background-color:#F8FAFC; border:1px solid #E2E8F0; border-radius:8px; padding:24px; text-align:left; margin-bottom:24px;\">" + detalles + "</div>"
            + buildInfoBox("#EFF6FF", "#BFDBFE", "#1E40AF", "#1E40AF", "", "Antigua fecha: <strike>" + citaAntigua.getFechaHoraInicio().format(fmt) + " - " + citaAntigua.getFechaHoraInicio().format(fmtH) + "</strike>");

        enviarSeguro(citaNueva.getPaciente().getEmail(), subject, buildWrapper(html), "reprogramación de cita");
    }

    /** Recordatorio 24 h antes */
    public void enviarRecordatorioCita(Cita cita) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM", Locale.forLanguageTag("es-PE"));
        DateTimeFormatter fmtH = DateTimeFormatter.ofPattern("HH:mm");

        String subject = "🔔 Recordatorio: tu cita es mañana — OdontoApp";
        
        String detalles = "<p style=\"font-size:14px; color:#334155; margin:0 0 12px;\"><strong style=\"color:#64748B;\">📅 Fecha:</strong> " + cita.getFechaHoraInicio().format(fmt) + "</p>"
                        + "<p style=\"font-size:14px; color:#334155; margin:0 0 12px;\"><strong style=\"color:#64748B;\">🕐 Hora:</strong> " + cita.getFechaHoraInicio().format(fmtH) + " hrs</p>"
                        + "<p style=\"font-size:14px; color:#334155; margin:0;\"><strong style=\"color:#64748B;\">👨‍⚕️ Dentista:</strong> " + cita.getOdontologo().getNombreCompleto() + "</p>";

        String html = buildIcon("#CFFAFE", "#0891B2", SVG_BELL)
            + buildTitle("¡Tu cita es mañana!", "Estimado(a) " + cita.getPaciente().getNombreCompleto() + ", te recordamos que tienes una reserva programada.", "")
            + "<div style=\"background-color:#F8FAFC; border:1px solid #E2E8F0; border-radius:8px; padding:24px; text-align:left; margin-bottom:24px;\">" + detalles + "</div>"
            + buildInfoBox("#ECFEFF", "#A5F3FC", "#155E75", "#155E75", "", "⏰ Si no puedes asistir, por favor ponte en contacto con nosotros cuanto antes.");

        enviarSeguro(cita.getPaciente().getEmail(), subject, buildWrapper(html), "recordatorio de cita");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BUILDERS DEL DISEÑO TAILWIND (EN HTML/CSS INLINE)
    // ══════════════════════════════════════════════════════════════════════════

    private String buildWrapper(String contentHtml) {
        return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"/><title>OdontoApp</title></head>"
             + "<body style=\"margin:0;padding:0;background-color:#F8FAFC;font-family:ui-sans-serif, system-ui, -apple-system, sans-serif;\">"
             + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color:#F8FAFC;padding:40px 16px;\">"
             + "<tr><td align=\"center\">"
             + "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"max-width:600px;width:100%;background-color:#ffffff;border-radius:16px;box-shadow:0 10px 25px rgba(0,0,0,0.1);border:1px solid #e2e8f0;text-align:center;\">"
             + "<tr><td style=\"padding:40px 32px;\">"
             + contentHtml
             + "</td></tr></table>"
             + "<p style=\"text-align:center; color:#94A3B8; font-size:12px; margin-top:24px;\">© " + java.time.Year.now().getValue() + " OdontoApp. Todos los derechos reservados.</p>"
             + "</td></tr></table></body></html>";
    }

    private String buildIcon(String bgColor, String textColor, String svgContent) {
        return "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"margin-bottom:24px;\"><tr><td align=\"center\">"
             + "<div style=\"background-color:" + bgColor + "; padding:24px; border-radius:9999px; display:inline-block;\">"
             + "<div style=\"width:64px; height:64px; color:" + textColor + ";\">" + svgContent + "</div>"
             + "</div></td></tr></table>";
    }

    private String buildTitle(String title, String p1, String p2) {
        String html = "<div style=\"margin-bottom:24px;\">"
             + "<h2 style=\"font-size:24px; font-weight:700; color:#0f172a; margin:0 0 8px 0;\">" + title + "</h2>"
             + "<p style=\"color:#475569; margin:0 0 8px 0; font-size:16px;\">" + p1 + "</p>";
        if (p2 != null && !p2.isEmpty()) {
            html += "<p style=\"color:#64748B; margin:0; font-size:14px;\">" + p2 + "</p>";
        }
        html += "</div>";
        return html;
    }

    private String buildInfoBox(String bgColor, String borderColor, String titleColor, String textColor, String title, String listHtml) {
        String html = "<div style=\"background-color:" + bgColor + "; border:1px solid " + borderColor + "; border-radius:8px; padding:16px; text-align:left; margin-bottom:24px;\">";
        if (title != null && !title.isEmpty()) {
            html += "<p style=\"font-size:14px; color:" + titleColor + "; font-weight:600; margin:0 0 8px 0;\">" + title + "</p>";
        }
        html += "<div style=\"font-size:14px; color:" + textColor + "; margin:0;\">" + listHtml + "</div></div>";
        return html;
    }

    private String buildButton(String btnColor, String text, String url) {
        return "<div style=\"margin-bottom:24px;\">"
             + "<a href=\"" + url + "\" target=\"_blank\" style=\"display:inline-block; background-color:" + btnColor + "; color:#ffffff; padding:14px 32px; border-radius:8px; font-weight:600; text-decoration:none; font-size:16px;\">" + text + "</a>"
             + "</div>";
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CORE — ENVÍO
    // ══════════════════════════════════════════════════════════════════════════

    private void enviarEmail(String para, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(para);
        helper.setSubject(subject);
        helper.setText(content, true);
        helper.setFrom("OdontoApp <system.momentum.noreply@gmail.com>");
        mailSender.send(message);
    }

    private void enviarSeguro(String para, String subject, String content, String tipo) {
        try {
            enviarEmail(para, subject, content);
            System.out.println("✓ Email [" + tipo + "] enviado a: " + para);
        } catch (MessagingException e) {
            System.err.println("✗ Error al enviar email [" + tipo + "]: " + e.getMessage());
        }
    }
}