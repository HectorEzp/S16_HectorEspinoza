package com.restaurante.service;

import com.restaurante.util.Conexion;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Reto 1: genera un reporte PDF con JasperReports usando datos de MySQL.
 */
public class ReporteService {

    private static final String PLANTILLA = "/reportes/reporte_platos.jrxml";

    public void generarPdf(String rutaSalida) throws Exception {
        try (InputStream jrxml = ReporteService.class.getResourceAsStream(PLANTILLA);
             Connection cn = Conexion.getConexion()) {

            if (jrxml == null) {
                throw new IllegalStateException("No se encontró la plantilla " + PLANTILLA);
            }

            JasperReport reporte = JasperCompileManager.compileReport(jrxml);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("TITULO", "Carta de platos del restaurante");

            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, cn);
            JasperExportManager.exportReportToPdfFile(print, rutaSalida);
        }
    }
}
